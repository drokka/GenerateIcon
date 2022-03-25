package com.drokka.emu.symicon.generateicon.nativewrap

import com.drokka.emu.symicon.generateicon.data.GeneratorDef
import com.drokka.emu.symicon.generateicon.data.IconDef
import kotlinx.coroutines.*

//external fun getHelloFromJNI(): String

external fun callRunSampleFromJNI( intArgs:IntArray, type:Byte,  dArgs:DoubleArray): OutputData

external fun callReColourBufFromJNI(symIn:String, bgClr:DoubleArray,  minClr:DoubleArray,  maxClr:DoubleArray):OutputData

class SymiNativeWrapper {

    suspend fun reColourSym(symData:String, bgClr:DoubleArray,  minClr:DoubleArray,  maxClr:DoubleArray):Deferred<OutputData>{

        return coroutineScope {
            async {
                callReColourBufFromJNI(symData, bgClr, minClr,maxClr)
            }
        }
    }

    suspend fun runSample(generatorDef: GeneratorDef, iconDef: IconDef, bgClr: DoubleArray,
                          minClr: DoubleArray, maxClr: DoubleArray): Deferred<OutputData> {
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
        return coroutineScope {
            async {
                callRunSampleFromJNI(intArgs, iconImageType, dArgs)
            }
        }

//    fun getHellow() : String{
        //       return getHelloFromJNI()
        //   }
    }

  //  companion object {
  //      fun generateSymi(intArgs: IntArray, iconImageType: Byte, dArgs: DoubleArray): OutputData {
   //         return callRunSampleFromJNI(intArgs, iconImageType, dArgs)
  //      }
   // }
}