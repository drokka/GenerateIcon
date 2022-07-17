package com.drokka.emu.symicon.generateicon.database

import androidx.room.TypeConverter
import com.drokka.emu.symicon.generateicon.data.GeneratedIconWithAllImageData
import com.drokka.emu.symicon.generateicon.data.SymImageDefinition
import org.json.JSONArray
import org.json.JSONException
import java.nio.ByteBuffer
import java.util.*

class SymiTypeConverters {
    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun fromGeneratedIconWithAllImageData(allImageData: GeneratedIconWithAllImageData): SymImageDefinition {
        return SymImageDefinition(
            alpha = allImageData.alpha,
            beta = allImageData.beta,
            ma = allImageData.ma,
            gamma = allImageData.gamma,
            lambda = allImageData.lambda,
            omega = allImageData.omega,
            quiltType = allImageData.quiltType,
            degreeSym = allImageData.degreeSym,
            height = allImageData.height,
            width = allImageData.width,
            iterations = allImageData.iterations,

            label = allImageData.label
        )
    }

    @TypeConverter
    fun fromSymImageDefinition(allImageData: SymImageDefinition): GeneratedIconWithAllImageData {
        return GeneratedIconWithAllImageData(
            iconDefId = UUID.randomUUID(),

            alpha = allImageData.alpha,
            beta = allImageData.beta,
            ma = allImageData.ma,
            gamma = allImageData.gamma,
            lambda = allImageData.lambda,
            omega = allImageData.omega,
            quiltType = allImageData.quiltType,
            degreeSym = allImageData.degreeSym,
            gen_def_id = UUID.randomUUID(),
            height = allImageData.height,
            width = allImageData.width,
            iterations = allImageData.iterations,

            genIconId = UUID.randomUUID(),
            label = allImageData.label,
            //   generatedData = "",
            generatedDataFileName = "",
            iconImageFileName = "",
            bgClr = JSONArrayfromDoubleArray(doubleArrayOf(0.0, 0.0, 0.0, 0.0)),
            minClr = JSONArrayfromDoubleArray(doubleArrayOf(0.9, 0.9, 0.9, 0.0)),
            maxClr = JSONArrayfromDoubleArray(doubleArrayOf(0.9, 0.9, 0.9, 0.0)),
            clrFunction = "default",
            //   byteArray = null,
            len = 0,
            generatedImageDataId = UUID.randomUUID()
        )
    }

companion object {
    @TypeConverter
    fun JSONArrayfromDoubleArray(values: DoubleArray): String {
        val jsonArray = JSONArray()
        for (value in values) {
            try {
                jsonArray.put(value)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun JSONArrayToDoubleArray(values: String): DoubleArray {
        try {
            val jsonArray = JSONArray(values)
            val DoubleArray = DoubleArray(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                DoubleArray[i] = jsonArray.getString(i).toDouble()
            }
            return DoubleArray
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return doubleArrayOf(0.0, 0.0, 0.0, 0.0)
    }

    fun doubleArrayToClrInt(clr:DoubleArray):Int{
        val red = (clr[0] * 255.0).toInt().toByte()
        val green = (clr[1]*255.0).toInt().toByte()
        val blue = (clr[2] * 255.0).toInt().toByte()
        val alpha = (clr[3] * 255.0).toInt().toByte()

        val byteArray:ByteArray = byteArrayOf(alpha, red, green, blue)
       // val colour:Int = alpha shl 24 | red shl 16 | green shl 8 |blue

        return ByteBuffer.wrap(byteArray).int
    }

    fun clrIntToIntArray(clrInt:Int):IntArray{
        val A: Int = clrInt shr 24 and 0xff // or color >>> 24

        val R: Int = clrInt shr 16 and 0xff
        val G: Int = clrInt shr 8 and 0xff
        val B: Int = clrInt and 0xff

        //val r = Color.Red(clrInt)
       // val bytes = ByteArray(4) { i -> (clrInt.toLong() shr (i * 8)).toByte() }
        //blue, green, red, alpha
        return intArrayOf(R, G, B, A)
    }
}
}