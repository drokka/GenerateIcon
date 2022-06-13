package com.drokka.emu.symicon.generateicon.nativewrap

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class MoreIterWorker( val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {

        val iterations = inputData.getLong("iterations", 1000)
     //   val symIn = inputData.getString("symin")
        val fname = inputData.getString("fname")
        val imageFileName = inputData.getString("imageFileName")
        val bgClr = inputData.getDoubleArray("bgClr")
        val minClr = inputData.getDoubleArray("minClr")
        val maxClr = inputData.getDoubleArray("maxClr")

        Log.d("doWork", "colours are: $bgClr , $minClr , $maxClr")

         coroutineScope {
            async{
                SymiNativeWrapper.mainViewModel?.let {
                    if ( fname== null) {
                        Log.e("MoreIterWorker", " fname is null")
                    }
                    else{
                            callMoreIterSampleFromJNI(it, context,iterations, fname, imageFileName, bgClr, minClr, maxClr)
                        }

                }
            }
        }

        return Result.success()
    }

}