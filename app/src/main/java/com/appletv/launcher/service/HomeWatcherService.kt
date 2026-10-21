package com.appletv.launcher.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.appletv.launcher.MainActivity

class HomeWatcherService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val pkg = event.packageName?.toString()
            // When Google TV home or stock launcher is brought to the foreground, redirect to our Apple TV Launcher
            if (pkg == "com.google.android.apps.tv.launcherx" ||
                pkg == "com.google.android.tungsten.setupwraith") {
                val launchIntent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                startActivity(launchIntent)
            }
        }
    }

    override fun onInterrupt() {}
}
