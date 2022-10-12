package com.drokka.emu.symicon.generateicon.nativewrap

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drokka.emu.symicon.generateicon.database.SymiTypeConverters
import com.drokka.emu.symicon.generateicon.ui.main.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class MoreIterWorker( val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {

        val iterations = inputData.getLong("iterations", 1000)
     //   val symIn = inputData.getString("symin")
        val fname = inputData.getString("fname")
        val imageFileName = inputData.getString("imageFileName")
        val bgClr = inputData.getDoubleArray("bgClr")
        val minClr = inputData.getDoubleArray("minClr")
        val maxClr = inputData.getDoubleArray("maxClr")
        val clrFunction = inputData.getString("clrFuncion")?:"default"
        val clrFunExp = inputData.getDouble("clrFunExp", 0.0)

        if(bgClr != null && minClr!=null && maxClr!=null) {
            Log.d("doWork", "colours are: ${bgClr[0]} ${bgClr[1]} ${bgClr[2]} ${bgClr[3]}  , " +
                    "${minClr[0]} ${minClr[1]} ${minClr[2]} ${minClr[3]}  ${minClr[3]} , " +
                    "${maxClr[0]} ${maxClr[1]} ${maxClr[2]} ${maxClr[3]}  ${maxClr[3]} "+
                    "\n clrFunction " + clrFunction +" clrFunExp "+ clrFunExp)
        }else{
            Log.e("doWork", "colour arrays  are null")
        }

                  SymiNativeWrapper.mainViewModel?.let {

                        val generatedData = fname?.let { it1 ->
                            callMoreIterSampleFromJNI(
                                it,
                                context,
                                iterations,
                                it1,
                                imageFileName,
                                bgClr,
                                minClr,
                                maxClr,
                                clrFunction,
                                clrFunExp
                            )
                        }
                      Log.d("MoreIterWorker", "on thread::  " + Thread.currentThread().id.toString())
                      Log.d("MoreIterWorker", "clrFunExp " +clrFunExp)
                          if (generatedData?.savedData?.isNotEmpty() == true) {
                            //  generatedIcon.generatedData = generatedData!!.savedData

                            if (!generatedData?.savedData!!.startsWith("Error")) {
                                if (imageFileName != null) {
                                  val resy =   storeWork(
                                        applicationContext,
                                        fname,
                                        imageFileName, generatedData)
                                    if(resy && bgClr!=null && minClr != null && maxClr!= null){
                                        MainViewModel.symiRepo.addGeneratedImageDataForDef(fname,
                                                imageFileName,SymiTypeConverters.JSONArrayfromDoubleArray(bgClr),
                                            SymiTypeConverters.JSONArrayfromDoubleArray(minClr),
                                            SymiTypeConverters.JSONArrayfromDoubleArray(maxClr),
                                            clrFunction,
                                            clrFunExp,
                                            generatedData.bitmap.height * generatedData.bitmap.width *4 //assuming alpha
                                        )
                                    }
                                }
                                return Result.success() // outData)
                            } else {
                                Log.e(
                                    "moreIterWorker",
                                    "output data is error message: " + generatedData!!.savedData
                                )
                                //   val errData = Data.Builder().putString(SAVED_DATA, generatedData.savedData)
                                //     .build()
                                return Result.failure()   // errData)
                            }
                        }

        }

        return Result.success()
    }

    fun storeWork(context:Context, dataFileName:String, imageFileName:String, generatedData: OutputData) : Boolean{

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
            val image = generatedData.bitmap   //BitmapFactory.decodeByteArray(generatedData.pngBuffer,
               // 0, generatedData.pngBufferLen)
            image?.compress(
                Bitmap.CompressFormat.PNG,
                100,
                pngStream
            )


            pngStream.flush()
            pngStream.close()

            return true
        } catch (xx: Exception) {
            xx.message?.let { Log.e("storeWork", it) }
            Log.e("storeWork", "exception thrown saving png " + imageFileName)
        }
        generatedData.bitmap = null
        generatedData.savedData = ""
        return false
    }

}