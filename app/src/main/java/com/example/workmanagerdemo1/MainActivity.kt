package com.example.workmanagerdemo1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Filter
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.work.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_COUNT_VALUE = "key_count"
    }

    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.text_view)
    }

    fun setOneTimeWorkRequest(view: View) {
        //Work requests
        val filteringRequest = OneTimeWorkRequest.Builder(FilteringWorker::class.java)
            .build()

        val compressingRequest = OneTimeWorkRequest.Builder(CompressingWorker::class.java)
            .build()

        val downloadingRequest = OneTimeWorkRequest.Builder(DownloadingWorker::class.java)
            .build()

        //Parallel workers (Workers will work in parallel)
        val parallelWorks = mutableListOf<OneTimeWorkRequest>()
        parallelWorks.add(downloadingRequest)
        parallelWorks.add(filteringRequest)


        val data: Data = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 1000).build()

        val constraint = Constraints.Builder(
        )
            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .setRequiresCharging(true)
            .build()

        val workManager = WorkManager.getInstance(applicationContext)

        //Upload request with constraints and input data

        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraint)
            .setInputData(data)
            .build()

        //Chaining worker
        workManager
            .beginWith(parallelWorks)
            .then(compressingRequest)
            .then(uploadRequest)
            .enqueue()

        //WORK MANAGER STATUS OBSERVING IN LIVE DATA
        workManager.getWorkInfoByIdLiveData(uploadRequest.id).observe(this,
            Observer {
                it?.let {
                    textView.text = it.state.name
                    if (it.state.isFinished) {
                        val data = it.outputData
                        val message = data.getString(UploadWorker.KEY_WORKER)
                        textView.text = message
                    }
                }
            })

    }
}