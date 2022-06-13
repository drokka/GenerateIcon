package com.drokka.emu.symicon.generateicon.database

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.ui.main.MainViewModel
import org.junit.Assert
import java.io.File
import java.util.*

@Dao
interface SymiDao {
    @Query("select * from SymIcon")
    fun getSymIconList(): LiveData<List<SymIcon>>

    @Query("select count(*) from IconDef where " +
            " lambda = (:l)  and alpha = (:a)   and beta = (:b)   and gamma = (:g)   and omega = (:o)  " +
            " and ma = (:m)   and quiltType = (:q)  ")
    fun existsIconDef(l:Double, a:Double, b:Double, g:Double, o:Double, m:Double, q:QuiltType):Int

    @Query("select icon_def_id from IconDef where" +
            " lambda = (:l)  and alpha = (:a)   and beta = (:b)   and gamma = (:g)   and omega = (:o)  " +
            " and ma = (:m)   and quiltType = (:q)  ")
    fun getIconDefId(l:Double, a:Double, b:Double, g:Double, o:Double, m:Double, q:QuiltType):UUID?

    fun getIconDefId(iconDef: IconDef):UUID?{
        return  getIconDefId(iconDef.lambda,iconDef.alpha,iconDef.beta,iconDef.gamma,iconDef.omega,
        iconDef.ma,iconDef.quiltType)
    }

    @Query("select * from SymIcon where label=(:ll)")
    fun getSymIcon(ll:String): LiveData<SymIcon>

    @Query("select sym_icon_id from SymIcon where icon_def_id = (:i) and label = (:l)")
    fun getSymIconId(i:UUID, l:String):UUID

    @Query("select gen_def_id from GeneratorDef where sym_icon_id = (:i) and width = (:w) and height = (:h) and iterations = (:iter)")
    fun getGenDefId(i:UUID, w:Int,h:Int, iter:Int):UUID

    @Query("select * from GeneratedIcon")
    fun getGeneratedIconList():LiveData<List<GeneratedIcon>>

    @Query("select count(*) from GeneratedIcon where id = (:id)")
    fun existsGeneratedIcon(id:UUID):Int

    @Query("select * from GeneratedIcon where id = (:id)")
    fun getGeneratedIcon(id:UUID):GeneratedIcon

    @Query("select id from GeneratedIcon where gen_def_id = (:genDefId)")
    fun getGeneratedIconId(genDefId:UUID):UUID?


    @Query("select * from GeneratedIconWithAllImageData where width = " + TINY)
    fun getAllGeneratedIconWithAllImageData():LiveData<List<GeneratedIconWithAllImageData>>

    @Query("select * from GeneratedIconWithAllImageData where width >= (:sz)")
    fun getAllGeneratedIconWithAllImageDataSize(sz: Int):List<GeneratedIconWithAllImageData>

    @Query("select * from GeneratedIconWithAllImageData where ma = (:ma) " +
            "and alpha = (:alpha) " +
            "and beta = (:beta) " +
            "and lambda = (:lambda) " +
            "and omega = (:omega) " +
            "and gamma = (:gamma) " +
            "and quiltType = (:quiltType) " +
            "and degreeSym = (:degreeSym) " +
            "and width = (:sz)")
    fun  getGeneratedIconWithAllImageDataSize(ma:Double,
                                              alpha:Double,
    beta:Double,
    lambda:Double,
    omega:Double,
    gamma:Double,
    quiltType:QuiltType, degreeSym:Int,  sz:Int):List<GeneratedIconWithAllImageData>

  //  @Query("select count(*) from GeneratedIcon where ")
  //  fun existsGeneratedIcon()

    @Query("select count(*) from GeneratorDef where gen_def_id = (:id)")
    fun existsGeneratorDef(id:UUID):Int

    @Query("select * from GeneratedImageData")
    fun getGeneratedImageDataList():LiveData<List<GeneratedImageData>>

    @Query("select gid_id from GeneratedImageData where gen_icon_id = (:genIconId)")
    fun getGeneratedImageDataId(genIconId:UUID):List<UUID>

  //  @Query("select byteArray from GeneratedImageData where gid_id = (:gidId)")
  //  fun getImageBitmap(gidId: UUID):ByteArray?

   // @Query("select * from GeneratedIconAndImageData"+
   //         " where width = (:size) and len >0")
  //  fun getSymIconData( size: Int):LiveData<List<GeneratedIconAndImageData>>
/***********************************************************************************************************************
    @Query("select gi.id, gi.gen_def_id , gi.generatedDataFileName, giddy.gid_id, giddy.gen_icon_id, giddy.iconImageFileName, "+
            " giddy.len "+
            " from GeneratedIcon gi inner join GeneratedImageData giddy "  +
                     "on (gi.id = giddy.gen_icon_id)")
   fun getAllSymIconDataMinusByteArray():List<GeneratedIconAndImageData>

    @Transaction
     fun getAllSymIconData(): List<GeneratedIconAndImageData>?{
        val listyVal = getAllSymIconDataMinusByteArray()
        if (listyVal != null) {
            for(item in listyVal){
                item.generatedImageData?.let {
                    it.byteArray = getImage(it.gid_id, it.len)
                }
                item.generatedIcon?.let {
                    it.generatedData = getGeneratedData(it.id)
                }
            }
        }

        return listyVal
    }

// query getting all symis for an icon def, minus the blobs (the actual data)
    @Query("select gi.id, gi.gen_def_id , gi.generatedDataFileName, giddy.gid_id, giddy.gen_icon_id, giddy.iconImageFileName, "+
            " giddy.len "+
            " from GeneratedIcon gi inner join GeneratedImageData giddy "  +
            "on (gi.id = giddy.gen_icon_id)"+
            "inner join GeneratorDef gd on (gi.gen_def_id = gd.gen_def_id)"+
            "inner join SymIcon si on (gd.sym_icon_id = si.sym_icon_id)"+
            "inner join IconDef def on (si.icon_def_id = def.icon_def_id)"+
            "where def.icon_def_id = (:iconDefId)")
    fun getSymIconDataMinusByteArray(iconDefId:UUID):List<GeneratedIconAndImageData>

    // get the data blobs to complete above GeneratedIconAndImageData using raw query
    @Transaction
    fun getSymIconData(iconDefId:UUID): List<GeneratedIconAndImageData>{
        val listyVal = getSymIconDataMinusByteArray(iconDefId)
        if (listyVal != null) {
            for(item in listyVal){
                item.generatedImageData?.let {
                    it.byteArray = getImage(it.gid_id, it.len)
                }
                item.generatedIcon?.let {
                    it.generatedData = getGeneratedData(it.id)
                }
            }
        }

        return listyVal
    }

    // query specific SIZE symis for an icon def, minus the blobs (the actual data)
    @Query("select gi.id, gi.gen_def_id , gi.generatedDataFileName, giddy.gid_id, giddy.gen_icon_id, giddy.iconImageFileName, "+
            " giddy.len "+
            " from GeneratedIcon gi inner join GeneratedImageData giddy "  +
            "on (gi.id = giddy.gen_icon_id)"+
            "inner join GeneratorDef gd on (gi.gen_def_id = gd.gen_def_id)"+
            "inner join SymIcon si on (gd.sym_icon_id = si.sym_icon_id)"+
            "inner join IconDef def on (si.icon_def_id = def.icon_def_id)"+
            "where def.icon_def_id = (:iconDefId)"+
            "and gd.width = (:sz)")
    fun getSymIconDataSizedMinusByteArray(iconDefId:UUID, sz:Int):GeneratedIconAndImageData

    // get the data blobs to complete above GeneratedIconAndImageData using raw query
    @Transaction
    fun getSymIconSizedData(iconDefId:UUID, sz:Int): GeneratedIconAndImageData{
        val symi = getSymIconDataSizedMinusByteArray(iconDefId, sz)
        if (symi != null) {
            symi.generatedImageData?.let {
                it.byteArray = getImage(it.gid_id, it.len)
            }
            symi.generatedIcon?.let {
                it.generatedData = getGeneratedData(it.id)
            }
        }
        return symi
    }
    @RawQuery
    fun getImageSubstr(query: SupportSQLiteQuery ):ByteArray?

    fun getImageSubstrQuery(gidId:UUID,start:Int, size: Int):String{
        return "select substr(byteArray," +start.toString() +"," + size.toString() +") from GeneratedImageData" +
                " where gid_id = \'"+ gidId.toString() +"\'"
    }

   fun getImage(gidId: UUID, len:Int): ByteArray? {
       var byteArray:ByteArray = ByteArray(0)
       var count=0
       while(count <len){
          val query = SimpleSQLiteQuery(getImageSubstrQuery(gidId,count+1,500000))
           val nextBytes = getImageSubstr(query)
           if(nextBytes != null) {
              byteArray =  byteArray.plus(nextBytes)
           }
           count+=500000
       }
       return  byteArray
   }
    @RawQuery
    fun getGeneratedDataSubstr(query: SupportSQLiteQuery):String?

    fun getGeneratedDataSubstrQuery(id: UUID, start: Int,size: Int):String{
        return "select substr(generatedData," +start.toString() +"," + size.toString() +") from GeneratedIcon " +
                " where id = \'" + id.toString() +"\'"
    }

    fun getGeneratedData(id: UUID):String{
        var str:String = ""
        var count=0
        while(true){
            val query = SimpleSQLiteQuery(getGeneratedDataSubstrQuery(id,count+1,500000))
            val nextBytes = getGeneratedDataSubstr(query)

            if( nextBytes.isNullOrEmpty()) {
                return str
            }
            str =  str.plus(nextBytes)

            count+=500000
        }
        return str
    }
***********************************************************************************************/ //stop fighting SQLite size restrictions!!

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addIconDef(iconDef: IconDef)

    @Update(onConflict= OnConflictStrategy.ABORT)
    fun updateSymIcon(symIcon: SymIcon)

    @Insert(onConflict= OnConflictStrategy.ABORT)
    fun addSymIcon(symIcon: SymIcon)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addGeneratorDef(generatorDef: GeneratorDef)

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    fun addGeneratedIcon(generatedIcon:GeneratedIcon)

    @Insert //(onConflict= OnConflictStrategy.REPLACE)
    fun addGeneratedImageData(generatedImage: GeneratedImageData)

    @SuppressLint("SuspiciousIndentation")
    @Transaction
    fun addGeneratedIconAndImageData(iconDef: IconDef,symIcon: SymIcon,generatorDef: GeneratorDef,
                                     genIconAndData:GeneratedIconAndImageData){
        var iconDefId: UUID? = null
        var symIconId:UUID?= null
        var genDefId:UUID?= null
        var genIconId:UUID?= null
        var genImDataId:UUID?= null
        var resy = 1

        var addingNew :Boolean = false
        iconDefId  = getIconDefId(iconDef)
        if(iconDefId == null){
            //Reset the id. Can I have auto increment primary key using Room?
            iconDefId = UUID.randomUUID()
            iconDef.icon_def_id = iconDefId
            addIconDef(iconDef)
            addingNew = true
        }

        symIcon.icon_def_id = iconDefId!!


        if(!addingNew) symIconId = getSymIconId(iconDefId,symIcon.label)
        if(symIconId == null){
            symIconId = UUID.randomUUID()
            symIcon.sym_icon_id = symIconId
            addSymIcon(symIcon)
            addingNew = true
        }

        generatorDef.sym_icon_id = symIconId!!

        if(!addingNew) genDefId = getGenDefId(symIconId,generatorDef.width, generatorDef.height,generatorDef.iterations)
        if(genDefId == null){
            genDefId = UUID.randomUUID()
                generatorDef.gen_def_id = genDefId!!
            addGeneratorDef(generatorDef)
            Log.d("addGeneratedImageData() after inserting NEW generatorDef" , "sz = "+ generatorDef.height.toString())
            addingNew = true
        }

        val logTag = "addGeneratedIconand..."
        genIconAndData.generatedIcon?.let{
     //       Log.d(logTag, "gen_def_id = " +it.gen_def_id.toString() +" genDefId= "+ genDefId)
            it.gen_def_id = genDefId
            if(!addingNew) genIconId = getGeneratedIconId(genDefId)
            if(genIconId == null) {
                Log.d(logTag, "adding new generated icon")
                genIconId = UUID.randomUUID()
                it.id = genIconId!!
                addGeneratedIcon(it)
                addingNew = true
            }
            else{
                Log.d(logTag, "There was already a generated icon genIconId set to found value")
                //There was already a generated icon. Fix the image data ref.
               // genIconAndData.generatedImageData?.gen_icon_id = genIconId as UUID
                it.id = genIconId!!
            }
        }

            genIconAndData.generatedImageData?.let {
                it.gen_icon_id = genIconId!!
              //  genImDataId = getGeneratedImageDataId(it.gen_icon_id)
              // if (genImDataId == null) {
               //     Log.d(logTag, "adding gen image data. gen_icon_id = " +it.gen_icon_id +
                 //   "image data id = " + it.gid_id +"data length = " + it.len.toString())
                    it.gid_id = UUID.randomUUID()
                  addGeneratedImageData(it)
              //  }
            }
    }

    /******************
    @Transaction
    fun getAllMergedData():LiveData<MutableList<GeneratedIconAndImageDataMerged>>{
        val listData = getAllSymIconData().value
        var listMerged:MutableList<GeneratedIconAndImageDataMerged> = emptyList<GeneratedIconAndImageDataMerged>().toMutableList()
        if(!listData.isNullOrEmpty()) {
            listData.iterator().forEach {

                var merged = mergedDataFromIconAndImageData(it)

                if (merged != null) {
                    listMerged.add(merged)
                }
            }
        }

        return listMerged
    }
*************************************************/
/**************************************************************************************
    @Transaction
    fun mergedDataFromIconAndImageData(generatedIconAndImageData: GeneratedIconAndImageData): GeneratedIconAndImageDataMerged? {

        val generatedImageDataData = generatedIconAndImageData.generatedImageData
        var generatedIconAndImageDataMerged:GeneratedIconAndImageDataMerged? = null
        generatedImageDataData?.let{
            val genIconIdData = generatedImageDataData.gen_icon_id
            genIconIdData.let {
                val generatedIcon = getGeneratedIcon(genIconIdData)
                generatedIcon?.let{
                    val generatorDefId = getGenDefIdFromGenData(genIconIdData)
                    val genDef = generatorDefId?.let { it1 -> getGenDef(it1) }
                    if(genDef != null) {
                        val symIcon = getSymIcon(genDef.sym_icon_id)
                        symIcon?.let {

                            val iconDef = getIconDef(generatorDefId)

                            iconDef?.let {
                                //Construct the generatedImage

                            generatedIconAndImageDataMerged = GeneratedIconAndImageDataMerged(
                                iconDef,
                                symIcon,
                                genDef,
                                generatedIcon,
                                generatedImageDataData
                            )
                                }
                            }}}}}
        return generatedIconAndImageDataMerged
    }
**********************************************************************************************/

    @Query("select * from SymIcon where sym_icon_id = (:id)")
    fun getSymIcon(id: UUID): SymIcon

    @Query("select * from IconDef where icon_def_id = (:id)")
    fun getIconDef(id:UUID):IconDef

    @Query("select gen_def_id from GeneratedIcon where id = (:id)")
    fun getGenDefIdFromGenData(id: UUID): UUID

    @Query("select * from GeneratorDef where gen_def_id = (:id)")
    fun getGenDef(id: UUID):GeneratorDef

    @Query("select gen_def_id from GeneratorDef where sym_icon_id = (:id)")
    fun getGenDefList(id: UUID):List<UUID>

    @Query("select iconImageFileName from GeneratedImageData where gid_id = (:gidId)")
    fun getSymiImageFileName(gidId: UUID): String?

    @Delete
    fun deleteGeneratedImageData(generatedImageData: GeneratedImageData)

    @Query("delete from SymIcon where sym_icon_id = (:id)")
    fun deleteSymIconDef(id: UUID)

    @Query("delete from GeneratorDef where gen_def_id = (:id)")
    fun deleteGenDef(id: UUID)

    @Query("select * from GeneratedIcon where gen_def_id = (:id)")
    fun getGeneratedIconList(id: UUID):List<GeneratedIcon>

    @Delete
    fun deleteGeneratedIcon(generatedIcon: GeneratedIcon)

    @Query("select * from GeneratedImageData where gen_icon_id = (:id)")
    fun getGeneratedImageDataList(id: UUID):List<GeneratedImageData>

    @Query("delete from IconDef where icon_def_id = (:id)")
    fun deleteIconDef(id: UUID)

    @Transaction
    fun deleteSymIcon(context: Context, generatedIconWithAllImageData: GeneratedIconWithAllImageData){

        for ( symIconId in getSymIconList(generatedIconWithAllImageData.iconDefId)){
            for(genDefId in getGenDefList(symIconId)){
                for (genIcon in getGeneratedIconList(genDefId)) {
                       // delete file genIcon.generatedDataFileName
                    val dataFile = File(context.filesDir, genIcon.generatedDataFileName)
                    dataFile.delete()

                    for (genImageData in getGeneratedImageDataList(genIcon.id)){
                       // delete  genImageData.iconImageFileName
                        val imagesDirPath = File(context.filesDir, "images")
                        val imFile = File(imagesDirPath, genImageData.iconImageFileName)
                        imFile.delete()

                        deleteGeneratedImageData(genImageData)
                    }

                    deleteGeneratedIcon(genIcon)
                }
                deleteGenDef(genDefId)
            }
            deleteSymIconDef(symIconId)
        }

        deleteIconDef(generatedIconWithAllImageData.iconDefId)


         /*   iconDefId
        var symIconId:UUID?
        var genDefId:UUID?
        var genIconId:UUID?
        var genImDataId:UUID?

          */
    }

    @Query("select sym_icon_id from SymIcon where icon_def_id = (:iconDefId)")
    fun getSymIconList(iconDefId: UUID): List<UUID>

    @SuppressLint("SuspiciousIndentation")
    @Transaction
    fun addGeneratedImageDataForGenData(generatedMedIcon: GeneratedIcon, generatedImageData: GeneratedImageData){
        val genIconId = getGeneratedIconId(generatedMedIcon.gen_def_id)
        if(genIconId != null){
            generatedImageData.gen_icon_id =genIconId
        }
        else {
            generatedImageData.gen_icon_id =UUID.randomUUID()
            generatedMedIcon.id = generatedImageData.gen_icon_id
            addGeneratedIcon(generatedMedIcon)
        }
        Log.d("addGeneratedImageDataForGenData", "generatedImageData " + generatedImageData.gid_id
           +" " + generatedImageData.gen_icon_id +"  " + generatedImageData.iconImageFileName)
         addGeneratedImageData(generatedImageData)
    }

    @Query("select * from GeneratedIconWithAllImageData where ma = (:ma) " +
            "and alpha = (:alpha) " +
            "and beta = (:beta) " +
            "and lambda = (:lambda) " +
            "and omega = (:omega) " +
            "and gamma = (:gamma) " +
            "and quiltType = (:quiltType) " +
            "and degreeSym = (:degreeSym) " +
            "and width = (:sz) "+
            "and bgClr = (:bgClr) and minClr = (:minClr) and maxClr = (:maxClr)")
    fun getGeneratedIconWithAllImageDataSizeClr(
        ma:Double,
        alpha:Double,
        beta:Double,
        lambda:Double,
        omega:Double,
        gamma:Double,
        quiltType:QuiltType,
        degreeSym:Int,
        sz:Int,
        bgClr:String,
        minClr:String,
        maxClr:String
    ): List<GeneratedIconWithAllImageData>


}
