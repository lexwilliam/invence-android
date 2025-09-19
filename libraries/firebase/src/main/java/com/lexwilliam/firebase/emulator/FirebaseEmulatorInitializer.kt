package com.lexwilliam.firebase.emulator

import android.content.Context
import com.lexwilliam.firebase.BuildConfig

class FirebaseEmulatorInitializer {
    
    fun initialize(context: Context) {
        if (BuildConfig.DEBUG && BuildConfig.USE_FIREBASE_EMULATOR) {
            FirebaseEmulatorConfig.connectToEmulator()
        }
    }
}
