package com.drokka.emu.symicon.generateicon.nativewrap

import com.drokka.emu.symicon.generateicon.data.GeneratorDef
import com.drokka.emu.symicon.generateicon.data.IconDef
import kotlinx.coroutines.*

//external fun getHelloFromJNI(): String

external fun callRunSampleFromJNI( intArgs:IntArray, type:Byte,  dArgs:DoubleArray): OutputData

class SymiNativeWrapper {

    suspend fun runSample(generatorDef: GeneratorDef, iconDef: IconDef): Deferred<OutputData> {
        var iconImageType: Byte
        val dArgs = DoubleArray(6)

        iconImageType = iconDef.quiltType.label[0].toByte()
        dArgs[0] = iconDef.lambda
        dArgs[1] = iconDef.alpha
        dArgs[2] = iconDef.beta
        dArgs[3] = iconDef.gamma
        dArgs[4] = iconDef.omega
        dArgs[5] = iconDef.ma

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