package com.shadowshield.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ShadowAccessibilityService : AccessibilityService() {

    private lateinit var temporalAnalyzer: TemporalAnalyzer

    override fun onServiceConnected() {
        super.onServiceConnected()
        temporalAnalyzer = TemporalAnalyzer(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        
        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                temporalAnalyzer.recordAction(packageName, "CLICK")
            }
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                temporalAnalyzer.recordAction(packageName, "TEXT_INPUT")
            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                temporalAnalyzer.recordAction(packageName, "WINDOW_CONTENT_CHANGED")
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                temporalAnalyzer.recordAction(packageName, "UI_CHANGE")
            }
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                temporalAnalyzer.recordAction(packageName, "NOTIFICATION")
            }
        }

        checkForSuspiciousPatterns(event, packageName)
    }

    private fun checkForSuspiciousPatterns(event: AccessibilityEvent, packageName: String) {
        val rootNode = rootInActiveWindow ?: return
        
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val windowInfo = event.source
            if (windowInfo != null && isSuspiciousOverlay(windowInfo)) {
                temporalAnalyzer.recordAction(packageName, "SUSPICIOUS_OVERLAY")
            }
        }

        rootNode.recycle()
    }

    private fun isSuspiciousOverlay(node: AccessibilityNodeInfo): Boolean {
        return node.isClickable && node.isFocusable && 
               node.contentDescription?.contains("overlay", ignoreCase = true) == true
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}
