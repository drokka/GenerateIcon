package com.drokka.emu.symicon.generateicon.database

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.ui.main.MainViewModel
import org.junit.Assert
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
    fun getIconDefId(l:Double, a:Double, b:Double, g:Double, o:Double, m:Double, q:QuiltType):UUID

    fun getIconDefId(iconDef: IconDef):UUID{
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
    fun getGeneratedIconId(genDefId:UUID):UUID

    @Query("select * from GeneratedIconWithAllImageData")
    fun getAllGeneratedIconWithAllImageData():LiveData<List<GeneratedIconWithAllImageData>>


  //  @Query("select count(*) from GeneratedIcon where ")
  //  fun existsGeneratedIcon()

    @Query("select count(*) from GeneratorDef where gen_def_id = (:id)")
    fun existsGeneratorDef(id:UUID):Int

    @Query("select * from GeneratedImageData")
    fun getGeneratedImageDataList():LiveData<List<GeneratedImageData>>

    @Query("select gid_id from GeneratedImageData where gen_icon_id = (:genIconId)")
    fun getGeneratedImageDataId(genIconId:UUID):UUID

   // @Query("select * from GeneratedIconAndImageData"+
   //         " where width = (:size) and len >0")
  //  fun getSymIconData( size: Int):LiveData<List<GeneratedIconAndImageData>>

    //@RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("select * from GeneratedIcon inner join GeneratedImageData "  +
            "on (GeneratedIcon.id = GeneratedImageData.gen_icon_id)")
    fun getAllSymIconData():LiveData<List<GeneratedIconAndImageData>>

//    @Query("select byteArray from GeneratedIconAndImageData" +
  //          " where label=(:ll) and width = (:size) and len >0")
   // fun getImage(ll: String, size: Int):ByteArray?

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

    @Transaction
    fun addGeneratedIconAndImageData(iconDef: IconDef,symIcon: SymIcon,generatorDef: GeneratorDef,
                                     genIconAndData:GeneratedIconAndImageData){
        var iconDefId: UUID?
        var symIconId:UUID?
        var genDefId:UUID?
        var genIconId:UUID?
        var genImDataId:UUID?
        var resy = 1

        iconDefId  = getIconDefId(iconDef)
        if(iconDefId == null){
            //Reset the id. Can I have auto increment primary key using Room?
            iconDefId = UUID.randomUUID()
            iconDef.icon_def_id = iconDefId
            addIconDef(iconDef)
        }

        symIcon.icon_def_id = iconDefId!!

        symIconId = getSymIconId(iconDefId,symIcon.label)
        if(symIconId == null){
            symIconId = UUID.randomUUID()
            symIcon.sym_icon_id = symIconId
            addSymIcon(symIcon)
        }
        generatorDef.sym_icon_id = symIconId!!
        genDefId = getGenDefId(symIconId,generatorDef.width, generatorDef.height,generatorDef.iterations)
        if(genDefId == null){
            genDefId = UUID.randomUUID()
                generatorDef.gen_def_id = genDefId!!
            addGeneratorDef(generatorDef)
            Log.d("addGeneratedImageData() after inserting NEW generatorDef" , "sz = "+ generatorDef.height.toString())
        }

        val logTag = "addGeneratedIconand..."
        genIconAndData.generatedIcon?.let{
     //       Log.d(logTag, "gen_def_id = " +it.gen_def_id.toString() +" genDefId= "+ genDefId)
            it.gen_def_id = genDefId
            genIconId = getGeneratedIconId(genDefId)
            if(genIconId == null) {
                Log.d(logTag, "adding generated icon")
                genIconId = UUID.randomUUID()
                it.id = genIconId!!
                addGeneratedIcon(it)
            }
            else{
                Log.d(logTag, "There was already a generated icon genIconId set to found value")
                //There was already a generated icon. Fix the image data ref.
               // genIconAndData.generatedImageData?.gen_icon_id = genIconId as UUID
                it.id = genIconId!!
            }
        }

            genIconAndData.generatedImageData?.let {
                 it.gen_icon_id = genIconAndData?.generatedIcon.id
                genImDataId = getGeneratedImageDataId(it.gen_icon_id)
                if (genImDataId == null) {
                    Log.d(logTag, "adding gen image data. gen_icon_id = " +it.gen_icon_id +
                    "image data id = " + it.gid_id +"data length = " + it.len.toString())
                    it.gid_id = UUID.randomUUID()
                  addGeneratedImageData(it)
                }
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

}
