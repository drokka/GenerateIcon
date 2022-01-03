//package com.drokka.emu.symicon.generateicon
/********************

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

import com.drokka.emu.symicon.generateicon.nativewrap.SymiNativeWrapper
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeParcelable
import org.junit.internal.Classes.getClass
@Parcelize
class SymiGenerateInputData(
    val intArgs:IntArray,
    val iconImageType:Byte,
    val dArgs:DoubleArray
) : Parcelable

@Parcelize
class SymiOutputData(
    val savedData: String?,
    val pngBuffer: ByteArray?,
    val pngBufferLen: Int
):Parcelable

class SymiService : Service() {

    companion object {
        val symiService = SymiService()
        val symiBinder = SymiBinder()
    }

    override fun onBind(intent: Intent): IBinder {
        return symiBinder
    }


  //  fun getService(): SymiService {return this@SymiService}


    class SymiBinder: Binder() {
        override fun getInterfaceDescriptor(): String {
            return "Symi generate image service"
        }

      //  fun getService(): SymiService {return symiBinder}

            override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            var symiOutputData:SymiOutputData? = null
            try{
                val inData:SymiGenerateInputData? = data.readParcelable<SymiGenerateInputData>(getClass("SymiGenerateInputData").getClassLoader())
                val outputData = inData?.let { SymiNativeWrapper.generateSymi(it.intArgs, inData.iconImageType, inData.dArgs) }
                if (outputData != null) {
                    symiOutputData = SymiOutputData(outputData.savedData, outputData.pngBuffer, outputData.pngBufferLen)
                }
            }catch (error:Exception){
                symiOutputData = SymiOutputData(error.message,null,0)
            }
            reply?.writeParcelable(symiOutputData, flags)

            return true
        }
    }


}
        ****************************************/