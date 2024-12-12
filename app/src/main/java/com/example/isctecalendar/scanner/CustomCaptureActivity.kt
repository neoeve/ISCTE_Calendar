package com.example.isctecalendar

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Surface
import com.journeyapps.barcodescanner.CaptureActivity

class CustomCaptureActivity : CaptureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Força a orientação para retrato durante a captura
        lockOrientationToPortrait()
    }

    private fun lockOrientationToPortrait() {
        val rotation = windowManager.defaultDisplay.rotation

        requestedOrientation = if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        }
    }
}
