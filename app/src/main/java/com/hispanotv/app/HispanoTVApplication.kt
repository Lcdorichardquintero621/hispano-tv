package com.hispanotv.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HispanoTVApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any global configurations here
    }
}