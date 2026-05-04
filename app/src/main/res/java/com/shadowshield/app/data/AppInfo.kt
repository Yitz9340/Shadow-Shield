package com.shadowshield.app.data

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L,
    val versionName: String = "",
    val permissions: List<String> = emptyList()
)
