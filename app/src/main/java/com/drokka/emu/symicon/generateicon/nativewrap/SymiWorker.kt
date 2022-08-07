package com.drokka.emu.symicon.generateicon.nativewrap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.database.SymiTypeConverters
import com.drokka.emu.symicon.generateicon.ui.main.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

const val PNG_BUFFER = "pngBuffer"

 const val SAVED_DATA = "savedData"

 const val PNG_BUFFER_LEN = "pngBufferLen"

 const val ICON_IMAGE_TYPE = "iconImageType"

 const val PARAMS_USED = "paramsUsed"

 const val INT_ARGS = "intArgs"

 const val ICON_TYPE = "iconType"

 const val D_ARGS = "dArgs"

private const val SYMI_WORKER = "symi_worker"

private const val RUN_SYMI_EXAMPLE = "runSymiExample"

class SymiWorker(context: Context, params:WorkerParameters) : CoroutineWorker(context, params){

    // var generatedData: OutputData? = null
/*
    private fun createForegroundInfo(progress: String): ForegroundInfo {
      //  val id = applicationContext.getString(R)
       // val title = applicationContext.getString(R.string.notification_title)
      //  val cancel = applicationContext.getString(R.string.cancel_download)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, SYMI_WORKER)
            .setContentTitle("Symmetry")
            .setTicker("Big jobs")
            .setContentText(progress)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, "cancel big job", intent)
            .build()

        return ForegroundInfo(0,notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        // Create a Notification channel
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(SYMI_WORKER, "Big jobs", importance)
        mChannel.description = "Big jobs status"
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(applicationContext,NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

    }
*/


 //   @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun doWork(): Result {

        val intArgs:IntArray? = inputData.getIntArray(INT_ARGS)
        intArgs?.let {
            val iconImageType: Byte = inputData.getByte(ICON_TYPE, 'S'.toByte())
            val dArgs = inputData.getDoubleArray(D_ARGS)

            val dataFileName = inputData.getString("dataFileName")
            val imageFileName = inputData.getString("imageFileName")
      //      var job: Deferred<OutputData>
            dArgs?.let {
     //           withContext(Dispatchers.IO) {
       //              job = coroutineScope {
         //               async {
                   val   generatedData:OutputData =    callRunSampleFromJNI(intArgs, iconImageType, dArgs)
                Log.d("SymiWorker", "on thread::  " + Thread.currentThread().id.toString())

                //              }
            //        }
              //  }
              //  setForeground(createForegroundInfo("Started big job"))
             //   job.await()
               // val generatedData = job.getCompleted()

                if (generatedData?.savedData?.isNotEmpty() == true) {
                    //  generatedIcon.generatedData = generatedData!!.savedData

                    if (!generatedData?.savedData!!.startsWith("Error")) {
                        if (imageFileName != null) {
                            if (dataFileName != null) {
                                storeWork(applicationContext,dataFileName, imageFileName,  generatedData!!, iconImageType, intArgs, dArgs)
                            }
                        }
                        return Result.success() // outData)
                    }
                    else {
                        Log.e(
                            RUN_SYMI_EXAMPLE,
                            "output data is error message: " + generatedData!!.savedData
                        )
                     //   val errData = Data.Builder().putString(SAVED_DATA, generatedData.savedData)
                       //     .build()
                        return Result.failure()   // errData)
                    }
                }
                Log.e(RUN_SYMI_EXAMPLE, "generatedData?.savedData?.isNotEmpty() FALSE. Got nothing back on callRunSampleFromJNI.")
                return Result.failure()
            }
        }

        if(this.isStopped){
            Log.d(RUN_SYMI_EXAMPLE, "worker is stopped, number of attempts: " + this.runAttemptCount)
        }
        return Result.failure()
    }

    fun storeWork(context:Context, dataFileName:String, imageFileName:String, generatedData: OutputData, iconImageType:Byte,
                  intArray: IntArray, iconDefData:DoubleArray) {
        val quiltType = when(iconImageType){
            'S'.toByte() -> QuiltType.SQUARE
            'H'.toByte() -> QuiltType.HEX
            'F'.toByte() -> QuiltType.FRACTAL
            else -> QuiltType.SQUARE
        }

        val degreeSym = intArray.get(3) //HERE
        val iconDefW = IconDef(UUID.randomUUID(), iconDefData[0], iconDefData[1],iconDefData[2],iconDefData[3],
                    iconDefData[4],iconDefData[5],
                    quiltType,
                    degreeSym
                )


        val symIconW = SymIcon(icon_def_id = iconDefW.icon_def_id, label = "go big")
        val generatorDefW = GeneratorDef(sym_icon_id = symIconW.sym_icon_id, width = intArray.get(1),
            height = intArray.get(2), iterations = intArray.get(0)
        )
        val generatedIconW = GeneratedIcon(gen_def_id = generatorDefW.gen_def_id, generatedDataFileName = dataFileName)
        val oStream = context.openFileOutput(dataFileName, Context.MODE_APPEND)

        ZipOutputStream(oStream).use { zipStream ->
            val zipEntry = ZipEntry(dataFileName)
            zipStream.putNextEntry(zipEntry)
            zipStream.write(generatedData.savedData.toByteArray())
        }

       // oStream.bufferedWriter(Charsets.UTF_8).write("Saved data not stored for Big Image")  // still something to delete fo DB clean up
        oStream.flush(); oStream.close()

        try {
            val imagesDirPath = File(context.getFilesDir(), "images")
            Log.i("saveImage", "dirPath is:" + imagesDirPath.toString())
            imagesDirPath.mkdirs()
            val imFile = File(imagesDirPath, imageFileName)
            val pngStream = FileOutputStream(imFile)
            val image = BitmapFactory.decodeByteArray(generatedData.pngBuffer,
                0, generatedData.pngBufferLen)
            image?.compress(
                Bitmap.CompressFormat.PNG,
                100,
                pngStream
            )
            pngStream.flush()
            pngStream.close()
        } catch (xx: Exception) {
            xx.message?.let { Log.e("storeWork", it) }
            Log.e("storeWork", "exception thrown saving png " + imageFileName)
        }
        assert(iconDefData.size == 18)
        val bgClr = iconDefData.sliceArray(6..9)
        val minClr = iconDefData.sliceArray(10..13)
        val maxClr = iconDefData.sliceArray(14..17)

        val generatedImageDataW = GeneratedImageData(UUID.randomUUID(), generatedIconW.id, imageFileName,
            SymiTypeConverters.JSONArrayfromDoubleArray( bgClr),
            SymiTypeConverters.JSONArrayfromDoubleArray( minClr),
            SymiTypeConverters.JSONArrayfromDoubleArray( maxClr) , "default",generatedData.pngBufferLen)

        val bigIADW = GeneratedIconAndImageData(generatedIconW, generatedImageDataW)

        MainViewModel.symiRepo.addGeneratedIconAndData(iconDefW,symIconW, generatorDefW, bigIADW)
        Log.d("storeWork", "done symiRepo add")
        generatedData.bitmap = null
        generatedData.savedData = ""

    }

}