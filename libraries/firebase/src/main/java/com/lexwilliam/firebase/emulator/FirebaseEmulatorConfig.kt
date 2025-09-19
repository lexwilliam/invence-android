package com.lexwilliam.firebase.emulator

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage

object FirebaseEmulatorConfig {
    private const val EMULATOR_HOST = "10.0.2.2" // Android emulator host
    private const val AUTH_PORT = 9099
    private const val FIRESTORE_PORT = 8080
    private const val FUNCTIONS_PORT = 5001
    private const val STORAGE_PORT = 9199

    fun connectToEmulator() {
        try {
            // Connect Auth to emulator
            FirebaseAuth.getInstance().useEmulator(EMULATOR_HOST, AUTH_PORT)
            
            // Connect Firestore to emulator
            FirebaseFirestore.getInstance().useEmulator(EMULATOR_HOST, FIRESTORE_PORT)
            
            // Connect Functions to emulator
            FirebaseFunctions.getInstance().useEmulator(EMULATOR_HOST, FUNCTIONS_PORT)
            
            // Connect Storage to emulator
            FirebaseStorage.getInstance().useEmulator(EMULATOR_HOST, STORAGE_PORT)
        } catch (e: Exception) {
            // Log error but don't crash the app
            e.printStackTrace()
        }
    }
}
