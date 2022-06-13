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
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

private val DB_NAME = "SYMI_DATABASE"

// GetBitmap extension to GeneratedImage for convenience
fun GeneratedImage.getBitmap(context: Context):Bitmap?{
    if(iconImageFileName.isNullOrEmpty()){
        Log.e("GeneratedImage.getBitmap", "ERROR iconImageFileName not set")
        return null
    }
    try {
        val imagesDirPath = File(context.filesDir, "images")

        val imFile = File(imagesDirPath, iconImageFileName)

        val inputStream = FileInputStream(imFile.path)

        Log.d("GeneratedImage.getBitmap", "iconImageFileName is $iconImageFileName")
    var byteArray = ByteArray(len)
    inputStream.read(byteArray,0,len)
    var bitmapImage:Bitmap? = null
    if (byteArray != null) {
        bitmapImage = BitmapFactory.decodeByteArray(byteArray, 0, len)
    }
        inputStream.close()
    return bitmapImage
    }catch ( xx:Exception){
        Log.e("GeneratedImage.getBitmap", "ERROR msg is: " + xx.message)

        return null
    }
}

fun GeneratedImage.getGeneratedData(context: Context):String{
    var dataString =""
    try{

        val filesPath = context.filesDir
        val dataFile = File(filesPath, generatedIcon.generatedDataFileName)

        val zipFile = ZipFile(dataFile)
        val entry = zipFile.getEntry(generatedIcon.generatedDataFileName)

        val zipInputStream = zipFile.getInputStream(entry)
        dataString = zipInputStream.bufferedReader().readText()

        zipInputStream.close()
        zipFile.close()
    }catch ( xx:Exception){
        Log.e("getGeneratedData", "ERROR msg is: " + xx.message)
        return ""
    }
    return dataString
}

fun GeneratedImage.clear(){
    iconImageFileName =""
    len = 0
}

fun GeneratedIcon.clear(){
    generatedDataFileName = ""
}
fun GeneratedImageData.clear(){
    iconImageFileName = ""
    len = 0
}
fun GeneratedIconAndImageData.clear(){
    generatedIcon.clear()
    generatedImageData?.clear()
}

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
/**********************************************************************************
    fun getAllSymIconData():List<GeneratedIconAndImageData>?{
        return symiDao.getAllSymIconData()
    }

    fun getSymIconData(genDefId:UUID):List<GeneratedIconAndImageData>?{
        return symiDao.getSymIconData(genDefId)
    }
******************************************************************************************/
/*********************
    fun getImageByteArray(context: Context, gidId:UUID): ByteArray? {
    try {

        val fname = getSymiImageFileName(gidId)
        if (fname.isNullOrEmpty()) return null
        val imagesDirPath = File(context.getFilesDir(), "images")

        val imFile = File(imagesDirPath, fname)

        val inputStream = FileInputStream(imFile.path)   //context.openFileInput(imFile.path)
        val byteArray = inputStream.readBytes()
        return byteArray
    }catch (xx:Exception) {
        Log.e("getImagesByteArray", "ERROR msg is: " + xx.message)
        return null
    }
    }
*************************************************************************/


    private fun getSymiImageFileName(gidId: UUID): String? {
       return symiDao.getSymiImageFileName(gidId)
    }

    /******************************************************
    fun getMergedDataFromIconAndImageData(generatedIconAndImageData: GeneratedIconAndImageData): GeneratedIconAndImageDataMerged? {
        return symiDao.mergedDataFromIconAndImageData(generatedIconAndImageData)
    } **********************************************************************************/

    fun getAllGeneratedIconWithAllImageData(): LiveData<List<GeneratedIconWithAllImageData>>{
        return symiDao.getAllGeneratedIconWithAllImageData()
    }

    fun getAllGeneratedIconWithAllImageDataSize(sz:Int): List<GeneratedIconWithAllImageData>{
        return symiDao.getAllGeneratedIconWithAllImageDataSize(sz)
    }

    fun getGeneratedIconWithAllImageDataSize(iconDef: IconDef, size:Int):List<GeneratedIconWithAllImageData>{
        return symiDao.getGeneratedIconWithAllImageDataSize(iconDef.ma,
            iconDef.alpha,
            iconDef.beta,
            iconDef.lambda,
            iconDef.omega,
            iconDef.gamma,
            iconDef.quiltType,
            iconDef.degreeSym,
            size)
    }

  //  fun getSymIconDataList(size:Int): LiveData<List<GeneratedIconAndImageData>> {
        //TODO return symiDao.getSymIconData(size)
 //   }

  //  fun getSymiSizedData(iconDefId:UUID, size:Int):GeneratedIconAndImageData{
  //      return symiDao.getSymIconSizedData(iconDefId,size)
   // }

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
        try {
            executor.execute {
                symiDao.addGeneratedIconAndImageData(
                    iconDef,
                    symIcon,
                    generatorDef,
                    generatedIconAndImageData
                )
            }
        }catch (xx:Exception){
            Log.e("addGeneratedIconAndData", "exception thrown from DB add: " +xx.message)
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

    fun deleteSymIcon(context: Context, generatedIconWithAllImageData: GeneratedIconWithAllImageData) {
            symiDao.deleteSymIcon(context,generatedIconWithAllImageData)
    }

    fun addGeneratedImageData(generatedImageData: GeneratedImageData) {
        symiDao.addGeneratedImageData(generatedImageData)
    }

    fun getGeneratedIcon(genIconId: UUID): GeneratedIcon {
        return symiDao.getGeneratedIcon(genIconId)
    }

    fun getGeneratedIconWithAllImageDataSizeClr(
        iconDef: IconDef,
        sz: Int,
        bgClr: String,
        minClr: String,
        maxClr: String
    ):List<GeneratedIconWithAllImageData> {
        return symiDao.getGeneratedIconWithAllImageDataSizeClr(iconDef.ma,
            iconDef.alpha,
            iconDef.beta,
            iconDef.lambda,
            iconDef.omega,
            iconDef.gamma,
            iconDef.quiltType,
            iconDef.degreeSym,
            sz, bgClr, minClr, maxClr)
    }

    //  fun addGeneratedImageDataForGenData(generatedMedIcon: GeneratedIcon, generatedImageData: GeneratedImageData) {
   //     symiDao.addGeneratedImageDataForGenData(generatedMedIcon, generatedImageData)
   // }


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