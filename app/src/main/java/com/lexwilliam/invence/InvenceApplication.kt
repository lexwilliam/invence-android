package com.lexwilliam.invence

import android.app.Application
import com.google.firebase.appcheck.FirebaseAppCheck
import com.lexwilliam.firebase.emulator.FirebaseEmulatorInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class InvenceApplication : Application() {
    @Inject
    lateinit var firebaseAppCheck: FirebaseAppCheck
    
    @Inject
    lateinit var firebaseEmulatorInitializer: FirebaseEmulatorInitializer

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase emulator for debug builds
        firebaseEmulatorInitializer.initialize(this)
        
        // Firebase App Check is initialized through Hilt injection
        // The provider is configured in FirebaseModule
    }
}