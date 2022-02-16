package com.drokka.emu.symicon.generateicon.database

import androidx.room.ColumnInfo
import androidx.room.TypeConverter
import com.drokka.emu.symicon.generateicon.data.GeneratedIconAndImageData
import com.drokka.emu.symicon.generateicon.data.GeneratedIconAndImageDataMerged
import com.drokka.emu.symicon.generateicon.data.GeneratedIconWithAllImageData
import com.drokka.emu.symicon.generateicon.data.SymImageDefinition
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
         //   byteArray = null,
            len = 0,
            generatedImageDataId = UUID.randomUUID()
        )
}
}