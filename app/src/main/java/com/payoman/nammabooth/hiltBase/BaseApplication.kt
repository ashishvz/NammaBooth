package com.payoman.nammabooth.hiltBase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm

@HiltAndroidApp
class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
    }
}