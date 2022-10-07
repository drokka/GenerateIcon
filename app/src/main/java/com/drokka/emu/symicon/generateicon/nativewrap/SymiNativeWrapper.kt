package com.drokka.emu.symicon.generateicon.nativewrap

import android.content.Context
import android.util.Log
import androidx.work.*
import com.drokka.emu.symicon.generateicon.data.GeneratorDef
import com.drokka.emu.symicon.generateicon.data.IconDef
import com.drokka.emu.symicon.generateicon.ui.main.MainViewModel
import kotlinx.coroutines.*
import java.util.*

//external fun getHelloFromJNI(): String

external fun callMoreIterSampleFromJNI(
    mainViewModel: MainViewModel,
    context: Context,
    iterations: Long,
   // inData: String,
    fname: String,
    imageFileName: String?,
    bgClr: DoubleArray?,
    minClr: DoubleArray?,
    maxClr: DoubleArray?,
    clrFunction: String,
    clrFunExp: Double
): OutputData

external fun callRunSampleFromJNI(
    intArgs: IntArray,
    type: Byte,
    dArgs: DoubleArray,
    clrFunction: String,
    clrFunExp: Double
): OutputData

external fun callReColourBufFromJNI(symIn:String,sz:Int, bgClr:DoubleArray,  minClr:DoubleArray,  maxClr:DoubleArray,
                                    clrFunction:String, clrFunExp: Double):OutputData

class SymiNativeWrapper (mainViewModel:MainViewModel){

    init {
         Companion.mainViewModel = mainViewModel
    }
    companion object Companion {
        var mainViewModel:MainViewModel? = null
    }

    suspend fun reColourSym(symData:String,sz:Int, bgClr:DoubleArray,  minClr:DoubleArray,  maxClr:DoubleArray,
                           clrFunction: String, clrFunExp: Double):Deferred< OutputData> {

        Log.d("callReColourBufFromJNI", "clrFunction is " + clrFunction  +"  clrFunExp is " + clrFunExp)

        return coroutineScope {
            async {
                callReColourBufFromJNI(symData, sz, bgClr, minClr,maxClr, clrFunction, clrFunExp)
           }
        }
    }

    /*
    suspend fun runMoreIter(
      //  mainViewModel: MainViewModel,
        context: Context,
        iterations: Long,
        symIn: String,
        fname: String,
        imageFileName: String,
        bgClr: DoubleArray,
        minClr: DoubleArray,
        maxClr: DoubleArray
    ):Deferred<Int>{

        return coroutineScope {
            async{
                callMoreIterSampleFromJNI(
                    Companion.mainViewModel!!,
                    context,
                    iterations,
                    fname,
                    imageFileName,
                    bgClr,
                    minClr,
                    maxClr
                )
            }
        }
    }

     */

    fun runMoreIterWorker(
        context: Context,
        iterations: Long,
        fname: String,
        imageFileName: String,
        bgClr: DoubleArray,
        minClr: DoubleArray,
        maxClr: DoubleArray,
    clrFunction: String,
    clrFunExp: Double):UUID{

        Log.d("runMoreIterWorker", "colours are: $bgClr , $minClr , $maxClr")
        val params =   Data.Builder().putLong("iterations", iterations)
            .putString("fname", fname)
            .putString("imageFileName", imageFileName)
            .putDoubleArray("bgClr", bgClr)
            .putDoubleArray("minClr", minClr)
            .putDoubleArray("maxClr", maxClr)
            .putString("clrFunction", clrFunction)
            .putDouble("clrFunExp", clrFunExp)
            .build()
        val symiWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<MoreIterWorker>()
                .setInputData(params)
                .build()

        WorkManager
            .getInstance(context)
            .enqueue(symiWorkRequest)


        return symiWorkRequest.id

    }

    suspend fun runSample(generatorDef: GeneratorDef, iconDef: IconDef, bgClr: DoubleArray,
                          minClr: DoubleArray, maxClr: DoubleArray, clrFunction: String,
                          clrFunExp: Double): Deferred<OutputData> {
        var iconImageType: Byte
        val dArgs = DoubleArray(18)

        iconImageType = iconDef.quiltType.label[0].toByte()
        dArgs[0] = iconDef.lambda
        dArgs[1] = iconDef.alpha
        dArgs[2] = iconDef.beta
        dArgs[3] = iconDef.gamma
        dArgs[4] = iconDef.omega
        dArgs[5] = iconDef.ma

        dArgs[6] = bgClr[0]
        dArgs[7] = bgClr[1]
        dArgs[8] = bgClr[2]
        dArgs[9] = bgClr[3]

        dArgs[10] = minClr[0]
        dArgs[11] = minClr[1]
        dArgs[12] = minClr[2]
        dArgs[13] = minClr[3]

        dArgs[14] = maxClr[0]
        dArgs[15] = maxClr[1]
        dArgs[16] = maxClr[2]
        dArgs[17] = maxClr[3]

        val intArgs: IntArray = intArrayOf(
            generatorDef.iterations,
            generatorDef.width,
            generatorDef.height,
            iconDef.degreeSym
        )
        Log.d("RunSample", "on thread::  " + Thread.currentThread().id.toString())

        return coroutineScope {
            async {
                callRunSampleFromJNI(intArgs, iconImageType, dArgs, clrFunction, clrFunExp)
            }
        }
    }
        fun runSampleWorker(context:Context,dataFileName:String, imageFileName:String, generatorDef: GeneratorDef, iconDef: IconDef, bgClr: DoubleArray,
                              minClr: DoubleArray, maxClr: DoubleArray, clrFunction: String, clrFunExp: Double): Pair<UUID,String> {
            var iconImageType: Byte
            val dArgs = DoubleArray(18)

            iconImageType = iconDef.quiltType.label[0].toByte()
            dArgs[0] = iconDef.lambda
            dArgs[1] = iconDef.alpha
            dArgs[2] = iconDef.beta
            dArgs[3] = iconDef.gamma
            dArgs[4] = iconDef.omega
            dArgs[5] = iconDef.ma

            dArgs[6] = bgClr[0]
            dArgs[7] = bgClr[1]
            dArgs[8] = bgClr[2]
            dArgs[9] = bgClr[3]

            dArgs[10] = minClr[0]
            dArgs[11] = minClr[1]
            dArgs[12] = minClr[2]
            dArgs[13] = minClr[3]

            dArgs[14] = maxClr[0]
            dArgs[15] = maxClr[1]
            dArgs[16] = maxClr[2]
            dArgs[17] = maxClr[3]

            val intArgs: IntArray = intArrayOf(
                generatorDef.iterations,
                generatorDef.width,
                generatorDef.height,
                iconDef.degreeSym
            )

            val inputData = Data.Builder().putIntArray(INT_ARGS, intArgs)
                .putByte(ICON_TYPE, iconImageType)
                .putDoubleArray(D_ARGS, dArgs)
                .putString("dataFileName", dataFileName)
                .putString("imageFileName", imageFileName)
                .putString("clrFunction", clrFunction)
                .putDouble("clrFunExp", clrFunExp)
                .build()
            val symiWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<SymiWorker>()
                    .setInputData(inputData)
                    .build()


            WorkManager
                .getInstance(context)
                .enqueue(symiWorkRequest)


            return Pair(symiWorkRequest.id, dataFileName)
        }
//    fun getHellow() : String{
        //       return getHelloFromJNI()
        //   }


  //  companion object {
  //      fun generateSymi(intArgs: IntArray, iconImageType: Byte, dArgs: DoubleArray): OutputData {
   //         return callRunSampleFromJNI(intArgs, iconImageType, dArgs)
  //      }
   // }
}