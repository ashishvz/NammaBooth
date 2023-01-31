package com.payoman.nammabooth.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.*
import com.payoman.nammabooth.R
import com.payoman.nammabooth.workManagers.UpdatePhoneNumberWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val workerConstraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build()
        val worker = PeriodicWorkRequestBuilder<UpdatePhoneNumberWorker>(15, TimeUnit.MINUTES).setConstraints(workerConstraints).build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork(UpdatePhoneNumberWorker::class.simpleName.toString(), ExistingPeriodicWorkPolicy.KEEP, worker)
    }
}