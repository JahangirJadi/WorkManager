package com.example.workmanagerdemo1

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val KEY_WORKER = "key_worker"
    }

    override fun doWork(): Result {
        try {
            val count = inputData.getInt(MainActivity.KEY_COUNT_VALUE, 0)
            for (i in 0..count) {
                Log.i("MYTAG", "MY WORKER UPLOADING>>> $i")
            }

            val dateFormat = SimpleDateFormat("dd/mm/yyyy hh:mm:ss")
            val currentTime = dateFormat.format(Date())

            val data: Data = Data.Builder()
                .putString(KEY_WORKER, currentTime).build()
//SENDING OUTPUT DATA IN RESULT.SUCCESS
            return Result.success(data)

        } catch (e: Exception) {
            return Result.failure()
        }

    }
}