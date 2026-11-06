package com.example.tasklocker

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AppLockerService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // Görev tamamlanmadıysa ve kilitli uygulamaya girmeye çalıştıysa
            if (TaskManager.isAppLocked(this, packageName)) {
                Toast.makeText(this, "Önce görevi tamamlamalısın! Kamera açılıyor...", Toast.LENGTH_LONG).show()

                // Doğrudan Kamera Doğrulama Ekranını Başlat
                val intent = Intent(this, CameraActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("TASK_ID", 1) 
                }
                startActivity(intent)
            }
        }
    }

    override fun onInterrupt() {}
}
