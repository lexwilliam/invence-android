package com.lexwilliam.invence

import android.app.Application
import com.google.firebase.appcheck.FirebaseAppCheck
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class InvenceApplication : Application() {
    @Inject
    lateinit var firebaseAppCheck: FirebaseAppCheck
}