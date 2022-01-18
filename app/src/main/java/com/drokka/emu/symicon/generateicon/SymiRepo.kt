package com.drokka.emu.symicon.generateicon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.database.SymiDatabase
import com.drokka.emu.symicon.generateicon.database.SymiTypeConverters
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private val DB_NAME = "SYMI_DATABASE"

class SymiRepo   private constructor(context: Context) {

    private val executor = Executors.newSingleThreadExecutor()

    private val database : SymiDatabase = Room.databaseBuilder(
        context.applicationContext,
        SymiDatabase::class.java,
        DB_NAME
    ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
        .also {

        if(!it.isOpen){
            Log.i("SymiRepo constructor", "SYMI_DATABASE is closed trying to open")
        }
            it.openHelper.writableDatabase

    Log.i("SymiRepo constructor isOpen: ", it.isOpen.toString())
    }
    /****************************************************************************/

    private val symiDao = database.symiDao()

    //---- repo interface ----------------------------------------

    fun getSymIconList():LiveData<List<SymIcon> >{
        return symiDao.getSymIconList()
    }

    fun getSymIcon(label:String): LiveData<SymIcon> {
        return symiDao.getSymIcon(label)
    }
    /*****************
    fun getSymIcon(id: UUID): LiveData<SymIcon>{
        return  symiDao.getSymIcon(id)
    }
    fun getIconDef(id: UUID):LiveData<IconDef>{
        return symiDao.getIconDef(id)
    }

    fun getGenDefIdFromGenData(id: UUID): LiveData<UUID>{
        return  symiDao.getGenDefIdFromGenData(id)
    }
    fun getGeneratorDef(id: UUID): LiveData<GeneratorDef>{
        return symiDao.getGenDef(id)
    }

    fun getGeneratedIcon(id: UUID):LiveData<GeneratedIcon>{
        return symiDao.getGeneratedIcon(id)
    }
*****************************************************/

    fun updateSymIcon(symIcon: SymIcon){
        executor.execute{
            symiDao.updateSymIcon(symIcon)
        }
    }

    fun getAllSymIconData():List<GeneratedIconAndImageData>?{
        return symiDao.getAllSymIconData()
    }

    fun getSymIconData(genDefId:UUID):List<GeneratedIconAndImageData>?{
        return symiDao.getSymIconData(genDefId)
    }

    fun getImageByteArray(gidId:UUID): ByteArray? {
        return symiDao.getImageBitmap(gidId)
    }
/******************************************************
    fun getMergedDataFromIconAndImageData(generatedIconAndImageData: GeneratedIconAndImageData): GeneratedIconAndImageDataMerged? {
        return symiDao.mergedDataFromIconAndImageData(generatedIconAndImageData)
    } **********************************************************************************/

    fun getAllGeneratedIconWithAllImageData(): LiveData<List<GeneratedIconWithAllImageData>>{
        return symiDao.getAllGeneratedIconWithAllImageData()
    }

  //  fun getSymIconDataList(size:Int): LiveData<List<GeneratedIconAndImageData>> {
        //TODO return symiDao.getSymIconData(size)
 //   }

    fun getSymiSizedData(iconDefId:UUID, size:Int):GeneratedIconAndImageData{
        return symiDao.getSymIconSizedData(iconDefId,size)
    }

    /*********
    fun getTinyImage(label: String):Bitmap?{
        var bitmap:Bitmap? = null
        val byteArray = symiDao.getImage(label, TINY)
        if(byteArray != null){
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
        return bitmap
    }
*********************************************************/

    fun addSymIcon(symIcon: SymIcon) {
        if (symiDao.getSymIcon(symIcon.label).value == null) {
            executor.execute { symiDao.addSymIcon(symIcon) }
        }
    }

    fun isInTransaction():Boolean{
        return database.inTransaction()
    }

     fun addGeneratedIconAndData(iconDef: IconDef, symIcon: SymIcon, generatorDef: GeneratorDef,
                                generatedIconAndImageData: GeneratedIconAndImageData){
        executor.execute{
            symiDao.addGeneratedIconAndImageData(iconDef,symIcon,generatorDef, generatedIconAndImageData)
        }
        /************
        if(symiDao.getSymIcon(generatedIconAndImageData.generatedIcon.definition.symIcon.label).value == null) {
                executor.execute{symiDao.addGeneratedIconAndImageData(generatedIconAndImageData)}
            }else{
                if(symiDao.getSymIconImageData(generatedIconAndImageData.generatedIcon.definition.symIcon.label,
                    generatedIconAndImageData.generatedIcon.definition.height) == null){
                    executor.execute{
                        symiDao.addImageData(generatedIconAndImageData.generatedImageData)
                    }
                }else{
                    //ToDo UPDATE
                }
            }                ***************************/

    }


    //---------------------------------------------------------
        companion object {
            private var INSTANCE: SymiRepo? = null

            fun initialize(context: Context) {
                if (INSTANCE == null) {
                    INSTANCE = SymiRepo(context)
                }
            }

            fun get(): SymiRepo {
                return INSTANCE ?:
                throw IllegalStateException("SymiRepo must be initialized")
            }
        }
}