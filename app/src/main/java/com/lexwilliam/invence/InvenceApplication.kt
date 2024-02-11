package com.lexwilliam.invence

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InvenceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}