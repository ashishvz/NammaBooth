package com.payoman.nammabooth.workManagers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.payoman.nammabooth.database.UpdatePhoneNumber
import com.payoman.nammabooth.interfaces.WorkerDependencyProvider
import com.payoman.nammabooth.repository.UpdatePhoneNumberRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class UpdatePhoneNumberWorker(
    private val appContext: Context,
    private val workerParameters: WorkerParameters
) : Worker(appContext, workerParameters) {

    private val tag = "UpdatePhoneNumberAPIWorker"

    override fun doWork(): Result {
        val updatePhoneList = mutableListOf<UpdatePhoneNumber>()
        val hiltEntryPoint = EntryPointAccessors.fromApplication(appContext, WorkerDependencyProvider::class.java)
        val updatePhoneNumberRepository = hiltEntryPoint.updatePhoneRepo()
        var status = false
        runBlocking(Dispatchers.IO) {
            updatePhoneList.addAll(updatePhoneNumberRepository.getAllUpdatePhone())
        }
        if (updatePhoneList.size > 0) {
            runBlocking {
                val response = updatePhoneNumberRepository.postUpdatePhoneNumber(updatePhoneList).body()
                if (response != null) {
                    status = if (response.status.equals("SUCCESS")) {
                        runBlocking(Dispatchers.IO) {
                            updatePhoneNumberRepository.deleteAllUpdatePhone()
                        }
                        true
                    } else
                        false
                }
            }
        } else {
            status = true
            Log.d(tag, "No data to update phone number")
        }
        return if (status) Result.success() else Result.failure()
    }
}