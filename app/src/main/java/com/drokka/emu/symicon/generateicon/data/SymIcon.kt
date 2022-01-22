package com.drokka.emu.symicon.generateicon.data


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import java.io.Serializable
import java.util.*

enum class QuiltType(val label: String) {
    SQUARE("S"), HEX("H"), FRACTAL("F")
}
// width / height constants in pixels - odd since (0,0) is in the middle and centre of symmetry
const val TINY = 121
const val SMALL = 381  /* 385 is  0.75 of max Google Play icon size (512), which is recommended to accommodate rounding etc. But is it dp not px? */
const val MEDIUM = 941
const val LARGE = 1281
const val XLARGE =2001

const val QUICK_LOOK = 20000
const val GO        = 100000
const val GO_GO     = 2000000
const val GO_GO_GO  = 10000000

//@Entity(primaryKeys = ["lambda","alpha","beta","gamma","omega","ma","quiltType"])
@Entity
data class IconDef(
    @PrimaryKey var icon_def_id: UUID = UUID.randomUUID(),
    var lambda: Double = 0.6,
    var alpha: Double = 0.3,
    var beta: Double =0.2,
    var gamma: Double = 0.4,
    var omega: Double = 0.2,
    var ma: Double = 0.3,
    var quiltType: QuiltType = QuiltType.SQUARE
):Serializable

//@Entity(primaryKeys = ["lambda","alpha","beta","gamma","omega","ma","quiltType"])
@Entity( foreignKeys = [ForeignKey(entity = IconDef::class,
    parentColumns = ["icon_def_id"],
    childColumns = ["icon_def_id"],
    onDelete = ForeignKey.CASCADE)]
)
data class SymIcon ( //@Embedded var definition: IconDef,
    @PrimaryKey var sym_icon_id:UUID = UUID.randomUUID(),
    var icon_def_id: UUID,
    var label:String = "no label"
):Serializable

@Entity( foreignKeys = [ForeignKey(entity = SymIcon::class,
    parentColumns = ["sym_icon_id"],
    childColumns = ["sym_icon_id"],
    onDelete = ForeignKey.CASCADE)]
)
//@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
data class GeneratorDef(
    @PrimaryKey var gen_def_id: UUID = UUID.randomUUID(),
  //  @Embedded var symIcon: SymIcon,
    var sym_icon_id: UUID,
    var width: Int = TINY,
    var height: Int = TINY,
    var iterations: Int = GO,
):Serializable

@Entity( foreignKeys = [ForeignKey(entity = GeneratorDef::class,
    parentColumns = ["gen_def_id"],
    childColumns = ["gen_def_id"],
    onDelete = ForeignKey.CASCADE)]
)
//@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
data class GeneratedIcon(
    @PrimaryKey var id: UUID = UUID.randomUUID(),
   // @Embedded var definition: GeneratorDef,
    var gen_def_id: UUID,
 //   var generatedData:String?,
    var generatedDataFileName: String
):Serializable

@Entity( foreignKeys = [ForeignKey(entity = GeneratedIcon::class,
    parentColumns = ["id"],
    childColumns = ["gen_icon_id"],
    onDelete = ForeignKey.CASCADE)]
)
//@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
data class GeneratedImageData(
    @PrimaryKey var gid_id:UUID,
    var gen_icon_id: UUID,
    val iconImageFileName: String,
//    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//    var byteArray: ByteArray?,
    var len: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeneratedImageData

        if (gid_id != other.gid_id) return false
        if (gen_icon_id != other.gen_icon_id) return false
        if (iconImageFileName != other.iconImageFileName) return false

        if (len != other.len) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gid_id.hashCode()
        result = 31 * result + gen_icon_id.hashCode()
        result = 31 * result + iconImageFileName.hashCode()
        result = 31 * result + len
        return result
    }
}
//-----------
// Get all the TINY images. Every symi should have at least a TINY entry
//__________________-----------------___________________________________
@DatabaseView("Select IconDef.icon_def_id As iconDefId, "+
    "IconDef.lambda, IconDef.alpha, IconDef.beta, IconDef.gamma,"+
        "IconDef.omega, IconDef.ma, IconDef.quiltType,"+
        "SymIcon.label,"+
        " GeneratorDef.gen_def_id, GeneratorDef.width, GeneratorDef.height, GeneratorDef.iterations,"+
        " GeneratedIcon.id as genIconId, "+
                                        /* " GeneratedIcon.generatedData,*/ " GeneratedIcon.generatedDataFileName,"+
        " GeneratedImageData.gid_id as generatedImageDataId, GeneratedImageData.iconImageFileName, "
        +     //"GeneratedImageData.byteArray, "+
        " GeneratedImageData.len"+
        " from IconDef inner join SymIcon on IconDef.icon_def_id = SymIcon.icon_def_id "+
    "inner join GeneratorDef on GeneratorDef.sym_icon_id = SymIcon.sym_icon_id" +
        " inner join GeneratedIcon on GeneratedIcon.gen_def_id = GeneratorDef.gen_def_id"+
        " inner join GeneratedImageData on GeneratedImageData.gen_icon_id = GeneratedIcon.id"  // +
 //   " where GeneratorDef.width ="+ TINY.toString()

)
data class GeneratedIconWithAllImageData
    (
    val iconDefId:UUID,
    var lambda: Double ,
    var alpha: Double,
    var beta: Double,
    var gamma: Double,
    var omega: Double,
    var ma: Double ,
    var quiltType: QuiltType,
    val label:String,
    val gen_def_id: UUID,
    var width: Int,
    var height: Int,
    var iterations: Int,
    var genIconId:UUID,
   // var generatedData:String,
    var generatedDataFileName: String,
    var generatedImageDataId:UUID,
    val iconImageFileName: String,
 //   @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
  //  var byteArray: ByteArray?,
    var len: Int
){
    /*************
    init {
        byteArray = null


            byteArray
        }
    *****************************/
    }

//@Entity
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
data class GeneratedIconAndImageData(
    //@PrimaryKey
   // val genIadId:UUID,

    @Embedded val generatedIcon: GeneratedIcon,
 //  @Relation(parentColumn = "id", entityColumn = "gen_icon_id")
    @Embedded val generatedImageData: GeneratedImageData?
)

data class GeneratedImage(
    var generatedIcon: GeneratedIcon,
 //   var byteArray: ByteArray?,
    var len: Int,
  //  var bitmapImage: Bitmap?,
    var iconImageFileName: String
):Serializable{

    /***************
    fun getBitmap(): Bitmap? {
     //   if (bitmapImage == null) {
            if (byteArray != null) {
               val bitmapImage = BitmapFactory.decodeByteArray(byteArray, 0, len)
                return bitmapImage
            }
      //  }
        return null
    }
    ******************************************************/
}

class GeneratedIconAndImageDataMerged(
    val iconDef: IconDef,
    val symIcon: SymIcon,
    val generatorDef: GeneratorDef,
    val generatedIcon: GeneratedIcon,
    val generatedImageData: GeneratedImageData
)

@DatabaseView("Select  "+
"IconDef.lambda, IconDef.alpha, IconDef.beta, IconDef.gamma,"+
"IconDef.omega, IconDef.ma, IconDef.quiltType,"+
"SymIcon.label,"+
"GeneratorDef.width, GeneratorDef.height, GeneratorDef.iterations"+
" from IconDef inner join SymIcon on IconDef.icon_def_id = SymIcon.icon_def_id "+
"inner join GeneratorDef on GeneratorDef.sym_icon_id = SymIcon.sym_icon_id" +
" inner join GeneratedIcon on GeneratedIcon.gen_def_id = GeneratorDef.gen_def_id" // +
//" inner join GeneratedImageData on GeneratedImageData.gen_icon_id = GeneratedIcon.id"
)
data class SymImageDefinition
    (
    var lambda: Double ,
    var alpha: Double,
    var beta: Double,
    var gamma: Double,
    var omega: Double,
    var ma: Double ,
    var quiltType: QuiltType,
    val label:String,
    var width: Int,
    var height: Int,
    var iterations: Int
){
        companion object{

fun defaultSimyDef(quiltType: QuiltType):SymImageDefinition{
    when(quiltType){
        QuiltType.SQUARE -> return SymImageDefinition( lambda = 0.6,
                            alpha = 0.3, beta =0.2,gamma = 0.4, omega = 0.2,
                            ma = 0.3, quiltType = QuiltType.SQUARE,
                        label = "Square default",
                         width = TINY, height = TINY, iterations = QUICK_LOOK )
        QuiltType.HEX -> return SymImageDefinition( lambda = 0.6,
            alpha = 0.3, beta =0.2,gamma = 0.4, omega = 0.2,
            ma = 0.3, quiltType = QuiltType.HEX,
            label = "Square default",
            width = TINY, height = TINY, iterations = QUICK_LOOK )
        QuiltType.FRACTAL -> return SymImageDefinition( lambda = 0.2,
            alpha = 0.3, beta =0.02,gamma = 0.9, omega = 0.02,
            ma = 0.3, quiltType = QuiltType.FRACTAL,
            label = "Square default",
            width = TINY, height = TINY, iterations = QUICK_LOOK )
    }
}}
    }
//    pp.aA=2; pp.bB=0; pp.gG=1; pp.oO=0.01;
//    pp.degreeSym=4; pp.scale=1;
//    pp.lL=-1.8;

