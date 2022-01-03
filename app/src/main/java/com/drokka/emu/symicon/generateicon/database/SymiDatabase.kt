package com.drokka.emu.symicon.generateicon.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.drokka.emu.symicon.generateicon.data.*


@Database(entities = [IconDef::class , SymIcon::class,
    GeneratorDef::class, GeneratedIcon::class, GeneratedImageData::class],
    views = [GeneratedIconWithAllImageData::class,SymImageDefinition::class],
    version = 2
)
@TypeConverters(SymiTypeConverters::class)
abstract class SymiDatabase: RoomDatabase() {

    init {

    }
    abstract fun symiDao():SymiDao
}