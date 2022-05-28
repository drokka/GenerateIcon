package com.drokka.emu.symicon.generateicon.nativewrap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.nativewrap.OutputData
import com.drokka.emu.symicon.generateicon.nativewrap.callRunSampleFromJNI
import com.drokka.emu.symicon.generateicon.ui.main.MainViewModel
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

const val PNG_BUFFER = "pngBuffer"

 const val SAVED_DATA = "savedData"

 const val PNG_BUFFER_LEN = "pngBufferLen"

 const val ICON_IMAGE_TYPE = "iconImageType"

 const val PARAMS_USED = "paramsUsed"

 const val INT_ARGS = "intArgs"

 const val ICON_TYPE = "iconType"

 const val D_ARGS = "dArgs"

class SymiWorker(context: Context, params:WorkerParameters) : CoroutineWorker(context, params){

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun doWork(): Result {

        val intArgs:IntArray? = inputData.getIntArray(INT_ARGS)
        intArgs?.let {
            val iconImageType: Byte = inputData.getByte(ICON_TYPE, 'S'.toByte())
            val dArgs = inputData.getDoubleArray(D_ARGS)
            var job: Deferred<OutputData>
            dArgs?.let {
                withContext(Dispatchers.IO) {
                     job = coroutineScope {
                        async {
                            callRunSampleFromJNI(intArgs, iconImageType, dArgs)
                        }
                    }
                }
                job.await()
                val generatedData = job.getCompleted()
                if (generatedData?.savedData?.isNotEmpty() == true) {
                    //  generatedIcon.generatedData = generatedData!!.savedData

                    if (!generatedData?.savedData!!.startsWith("Error")) {
                        storeWork(applicationContext, generatedData, iconImageType, intArgs, dArgs)
                        return Result.success() // outData)
                    } else {
                        Log.e(
                            "runSymiExample",
                            "output data is error message: " + generatedData.savedData
                        )
                     //   val errData = Data.Builder().putString(SAVED_DATA, generatedData.savedData)
                       //     .build()
                        return Result.failure()   // errData)
                    }
                }
                return Result.failure()
            }
        }

        return Result.failure()
    }

    fun storeWork(context:Context, generatedData: OutputData, iconImageType:Byte,
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

        val tt = Date().time.toString()
        val dataFileName = "symdata"+"BIG" + tt +".txt"
        val imageFileName = "symBIG" +tt + ".png"
        val symIconW = SymIcon(icon_def_id = iconDefW.icon_def_id, label = "go big")
        val generatorDefW = GeneratorDef(sym_icon_id = symIconW.sym_icon_id, width = intArray.get(1),
            height = intArray.get(2), iterations = intArray.get(0)
        )
        val generatedIconW = GeneratedIcon(gen_def_id = generatorDefW.gen_def_id, generatedDataFileName = dataFileName)
        val oStream = context.openFileOutput(dataFileName, Context.MODE_APPEND)
        oStream.bufferedWriter(Charsets.UTF_8).write("Saved data not stored for Big Image")  // still something to delete fo DB clean up
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
        val generatedImageDataW = GeneratedImageData(UUID.randomUUID(), generatedIconW.id, imageFileName,
            generatedData.pngBufferLen)

        val bigIADW = GeneratedIconAndImageData(generatedIconW, generatedImageDataW)

        MainViewModel.symiRepo.addGeneratedIconAndData(iconDefW,symIconW, generatorDefW, bigIADW)
        Log.d("storeWork", "done symiRepo add")
    }
}