package ru.meatgames.tomb

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Game : Application() {

    override fun onCreate() {
        super.onCreate()
        setupLogging()
        appContext = this
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var appContext: Context
    }

}
