//package com.example.openssldemo
//
//import androidx.work.Data
//import androidx.work.PeriodicWorkRequestBuilder
//import androidx.work.WorkInfo
//import androidx.work.WorkManager
//import androidx.work.workDataOf
//import com.example.openssldemo.worker.CreateNewKeyWorker
//import org.hamcrest.MatcherAssert
//import org.junit.Test
//import java.util.concurrent.TimeUnit
//
//
//class WorkerTest {
//    @Test
//    @Throws(Exception::class)
//    fun testPeriodicWork() {
//        // Define input data
//        val input = Data.Builder()
//            .putString(CreateNewKeyWorker.WORK_NAME, "com.example.clientapp")
//            .build()
//
//
//        // Create request
//        val request = PeriodicWorkRequestBuilder<CreateNewKeyWorker>(15, TimeUnit.MINUTES)
//            .setInputData(input)
//            .build()
//
//        val workManager = WorkManager.getInstance(myContext)
//        val testDriver = WorkManagerTestInitHelper.getTestDriver()
//        // Enqueue and wait for result.
//        workManager.enqueue(request).result.get()
//        // Tells the testing framework the period delay is met
//        testDriver.setPeriodDelayMet(request.id)
//        // Get WorkInfo and outputData
//        val workInfo = workManager.getWorkInfoById(request.id).get()
//
//        // Assert
//        MatcherAssert.assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
//    }
//}