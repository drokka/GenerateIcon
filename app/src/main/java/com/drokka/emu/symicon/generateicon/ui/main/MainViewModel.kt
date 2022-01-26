package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.drokka.emu.symicon.generateicon.SymiRepo
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.getBitmap
import com.drokka.emu.symicon.generateicon.nativewrap.SymiNativeWrapper
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainViewModel() : ViewModel() {

    companion object {
        val symiNativeWrapper = SymiNativeWrapper()

        /** data access using Room **/
        private val symiRepo = SymiRepo.get()

    }
/***********************************************************
    var mBound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as SymiService.SymiBinder
            mService = wi.bind
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }
*********************************************************************************************/
//init {
  //  Intent(this, SymiService::class.java).also { intent ->
  //      bindService(intent, connection, Context.BIND_AUTO_CREATE)
   // }

//}

 //   var liveList:List<GeneratedIconAndImageDataMerged?>? = null

    val symImageListAll:LiveData<List<GeneratedIconWithAllImageData>> = symiRepo.getAllGeneratedIconWithAllImageData()
//val symImageListAll:LiveData<List<GeneratedIconAndImageData>> = symiRepo.getAllSymIconData()
    var saveSymiData = false
    var genIAD:GeneratedIconAndImageData? = null
        get() = field

    fun clearGeneratedImage(){
        genIAD = null
      //  generatedImage.clear()
     //   generatedIcon.clear()
        generatedMedIAD = null
     //   generatedMedImage?.clear()

        generatedTinyIAD = null
    //    generatedTinyImage?.clear()

        tinyIm = null
        medIm = null
        largeIm = null
    }

    var generatedTinyIAD:GeneratedIconAndImageData? = null
    var generatedTinyImage:GeneratedImage? = null
    var tinyIm:Bitmap? = null
    var medIm:Bitmap? = null
    var largeIm:Bitmap? = null

    var generatedMedIAD:GeneratedIconAndImageData? = null
    var generatedMedImage:GeneratedImage? = null
    var isLoading:Boolean = false   //flag to not keep updating while loading symi data
    var isLoadingFromData = false //flag to show loading value from DB

         var iconDef = IconDef()
        var symIcon = SymIcon(
            icon_def_id = iconDef.icon_def_id,
                    )

         private var symi: GeneratorDef = GeneratorDef(
           sym_icon_id = symIcon.sym_icon_id,
        )
       var symiTiny = GeneratorDef(sym_icon_id = symIcon.sym_icon_id,width = TINY,
                        height = TINY, iterations = QUICK_LOOK)

        var generatedIcon = GeneratedIcon(gen_def_id = symi.sym_icon_id,
            generatedDataFileName = "symdata"
        )
        var generatedImage = GeneratedImage(generatedIcon, 0,  "symimage")

    var companion = Companion

    //utility to get image bitmap from DB
 //   fun getIconBitmap(context: Context,gidId:UUID):ByteArray?{
  //      return symiRepo.getImageByteArray(context, gidId)
  //  }

     fun setIconType(iconType:String) {
        when (iconType.substring(0,1)) {
            QuiltType.SQUARE.label -> {
                iconDef.quiltType = QuiltType.SQUARE
            }

            QuiltType.HEX.label -> {
                iconDef.quiltType = QuiltType.HEX
            }
            QuiltType.FRACTAL.label -> {
                iconDef.quiltType = QuiltType.FRACTAL
            }
        }
    }

   fun setNumIterations(iter: CharSequence){
        try {
            symi.iterations = iter.toString().toInt()
        }catch (x:Exception){

        }
    }

    fun setSize(sz:CharSequence) {
        try {
            symi.height = sz.toString().toInt()
            symi.width = sz.toString().toInt()
        } catch (x: Exception) {
        }
    }
    fun setLambda(sz: Double){
        try {
            iconDef.lambda = sz.toDouble()
            clearGeneratedImage()
        }catch(x:Exception){}

    }
    fun setAlpha(sz: Double){
        try {
            iconDef.alpha = sz.toDouble()
            clearGeneratedImage()
        }catch(x:Exception){}
    }
    fun setBeta(sz: Double){
        try {
            iconDef.beta = sz.toDouble()
            clearGeneratedImage()

        }catch(x:Exception){}
    }
    fun setGamma(sz: Double){
        try{
        iconDef.gamma = sz.toDouble()
            clearGeneratedImage()
        }catch (x:Exception){}
    }
    fun setOmega(sz: Double){
        try{
        iconDef.omega = sz.toDouble()
            clearGeneratedImage()
        }catch (x:Exception){}
    }
    fun setMa(sz: Double){
        try{
        iconDef.ma = sz.toDouble()
            clearGeneratedImage()
        }catch (x:Exception){}
    }

    fun resetSymiDef(data:SymImageDefinition){
    clearGeneratedImage()
        iconDef = IconDef(
            alpha = data.alpha,
                    beta = data.beta,
                    ma = data.ma,
                    gamma = data.gamma,
                    lambda = data.lambda,
                    omega = data.omega,
                    quiltType = data.quiltType
        )
        symIcon = SymIcon(
             icon_def_id = iconDef.icon_def_id,
            label = iconDef.quiltType.toString() + " no name"
        )
        symi = GeneratorDef(
            height = data.height,
                    width = data.width,
                    iterations = data.iterations,
            sym_icon_id = symIcon.sym_icon_id
        )
    }

    fun fromGeneratedIconWithAllImageData(allImageData: GeneratedIconWithAllImageData): SymImageDefinition {
        return SymImageDefinition(
            alpha = allImageData.alpha,
            beta = allImageData.beta,
            ma = allImageData.ma,
            gamma = allImageData.gamma,
            lambda = allImageData.lambda,
            omega = allImageData.omega,
            quiltType = allImageData.quiltType,

            height = allImageData.height,
            width = allImageData.width,
            iterations = allImageData.iterations,

            label = allImageData.label
        )
    }

    fun getGeneratedIcon(allImageData: GeneratedIconWithAllImageData):GeneratedIcon{
        return GeneratedIcon(allImageData.genIconId,allImageData.gen_def_id,allImageData.generatedDataFileName)
    }
    fun getGeneratedImage(genIcon:GeneratedIcon, allImageData: GeneratedIconWithAllImageData):GeneratedImage{
        return GeneratedImage(genIcon, allImageData.len, allImageData.iconImageFileName)

    }

    fun getGeneratedIconAndImageData(generatedIcon: GeneratedIcon,
                                     generatedImageData: GeneratedImageData): GeneratedIconAndImageData{
        return GeneratedIconAndImageData(generatedIcon, generatedImageData )

     }

    fun setSymiData(context: Context, allImageData: GeneratedIconWithAllImageData){
        isLoading = true
        //Assume it's already been added to the database. This only called from an item view
        // of DB list of symis. So no need to set ids etc!
        // Do need to manage isdirty

        resetSymiDef(fromGeneratedIconWithAllImageData( allImageData))
        // Get the actual generated data!
        /**************
        val symiDataBytes = symiRepo.getSymIconData(allImageData.iconDefId)

        if(symiDataBytes.isNullOrEmpty()){
            Log.e("setSymiData", "Error getting bitmap data")
            // and do what?
            return
        }
********************************************************************/
        iconDef.icon_def_id = allImageData.iconDefId  //Only correctly set ID. Queries using this and size.
        symIcon.icon_def_id = allImageData.iconDefId

   //     symiDataBytes.find { it.generatedImageData?.gid_id == allImageData.generatedImageDataId }?.let {


            generatedIcon = GeneratedIcon(allImageData.genIconId,allImageData.gen_def_id,allImageData.generatedDataFileName)   //it.generatedIcon

            generatedImage =  GeneratedImage(
                generatedIcon,
                allImageData.len, allImageData.iconImageFileName
            )

            val generatedImageData = GeneratedImageData(
                allImageData.generatedImageDataId, generatedIcon.id,
               generatedImage.iconImageFileName,  generatedImage.len
            )

            genIAD = GeneratedIconAndImageData(generatedIcon, generatedImageData)
     //   }
        when(allImageData.width){
            TINY -> {generatedTinyImage = generatedImage
                        generatedTinyIAD = genIAD
                tinyIm = generatedImage.getBitmap(context)
                    Log.d("setSymiData", "adding TINY width = " + symi.width.toString())}
            MEDIUM -> {
                        generatedMedImage = generatedImage
                    generatedMedIAD = genIAD
                medIm = generatedImage.getBitmap(context)
                Log.d("setSymiData", "adding MEDIUM width = " + symi.width.toString())
        }

        }
        // Actually with current view sql will always be TINY that has loaded. So load MEDIUM.
        val tempIconDef = IconDef(    UUID.randomUUID(), allImageData.lambda,
            allImageData.alpha,
            allImageData.beta,
            allImageData.gamma,
            allImageData.omega,
            allImageData.ma,
            allImageData.quiltType
        )
        val symiMedList = symiRepo.getGeneratedIconWithAllImageDataSize(tempIconDef, MEDIUM)
        if(symiMedList.size > 0){
            if(symiMedList.size >1){
                Log.i("setSymiData", "There is more than one MEDIUM image!! Number is: "+symiMedList.size.toString())
            }
            val generatedMedIcon = GeneratedIcon(symiMedList[0].genIconId,symiMedList[0].gen_def_id,symiMedList[0].generatedDataFileName)   //it.generatedIcon

            generatedMedImage =  GeneratedImage( generatedMedIcon, symiMedList[0].len, symiMedList[0].iconImageFileName)
            val generatedMedImageData = GeneratedImageData(
                symiMedList[0].generatedImageDataId, generatedMedIcon.id,
                symiMedList[0].iconImageFileName,  symiMedList[0].len
            )
            generatedMedIAD = GeneratedIconAndImageData(generatedMedIcon, generatedMedImageData)
         //   medIm = getIconBitmap(context = generatedMedIcon.id)
            }

        isLoading = false
    }

    fun imageExists(context: Context, sz:Int):Boolean{
        var haveImage:Boolean = when(sz){
            TINY -> tinyIm != null
            MEDIUM -> medIm != null
            LARGE -> largeIm != null
          else -> (symi.width == sz )&& generatedImage.len >0
        }
        if(!haveImage){
            //check DB
            val symiSizedDataList = symiRepo.getGeneratedIconWithAllImageDataSize(iconDef,sz)
            if(symiSizedDataList.isNotEmpty()){
                generatedIcon = getGeneratedIcon(symiSizedDataList[0])
                generatedImage = getGeneratedImage(generatedIcon, symiSizedDataList[0])

                genIAD = getGeneratedIconAndImageData(generatedIcon, GeneratedImageData(
                    symiSizedDataList[0].generatedImageDataId, generatedIcon.id,
                    symiSizedDataList[0].iconImageFileName,  symiSizedDataList[0].len
                ))

                when(symiSizedDataList[0].width){
                    TINY -> {generatedTinyImage = generatedImage
                        generatedTinyIAD = genIAD
                        Log.d("setSymiData", "adding TINY width = " + symi.width.toString())
                        tinyIm = generatedImage.getBitmap(context)
                    }
                    MEDIUM -> {
                        generatedMedImage = generatedImage
                        generatedMedIAD = genIAD

                        medIm = generatedImage.getBitmap(context)
                        Log.d("setSymiData", "adding MEDIUM width = " + symi.width.toString())
                    }
                    LARGE -> {
                        largeIm = generatedImage.getBitmap(context)
                    }
                }

                    haveImage = true
            }
        }
        return haveImage
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun runSymiExample(context: Context, size: Int = 0, count: Int = 0): Deferred<Unit> {
       // if(isLoading ) return
  //      if(!(genIAD == null)) return
        val TAG = "runSymiExample"
        val runRequired = !imageExists(context, size)
        Log.i(TAG , "runRequired is " + runRequired + "number of DB entries is " + symImageListAll.value?.size)
       if (!runRequired) return CoroutineScope(Dispatchers.Main).async{/* do nothing*/}
        /*viewModelScope.launch(Dispatchers.Unconfined) ***/
            val imagesDirPath = File(context.getFilesDir(), "images")
            Log.i(TAG, "dirPath is:" + imagesDirPath.toString())
            imagesDirPath.mkdirs()
            val date = Date().time.toString()
            if (size > 0) {  /* override the size defined */
                symi.height = size
                symi.width = size
            }
            if (count > 0) {  /* override the number of iterations defined */
                symi.iterations = count
            }
        //TODO If no image already continue to generate new data
       // clearGeneratedImage()
            generatedIcon.generatedDataFileName = "symdata" + date + ".txt"
            // val file = File(dirPath, "test" + LocalDateTime.now().second.toString() + ".txt")
            //________  Native call here to generate symi  -----------

       val generateJob = CoroutineScope(Dispatchers.Main).async {
            val     outputDataJob = symiNativeWrapper.runSample(symi, iconDef)
        outputDataJob.await()
        val outputData = outputDataJob.getCompleted()
/*******************************************
           // TRY intent with service ------------------------------------------------------------
           val intArray = intArrayOf(  symi.iterations,symi.width, symi.height  )
           val dArray = doubleArrayOf(
               iconDef.lambda,
               iconDef.alpha,
               iconDef.beta,
               iconDef.gamma,
               iconDef.omega,
               iconDef.ma
           )
           val inputSym = SymiGenerateInputData(intArray,iconDef.quiltType.toString().toByte(),dArray)
           val intent = Intent(context, SymiService::class.java)

           val service = SymiService.symiService
           intent.putExtra("SymiGenerateInputData", inputSym).also {
               SymiService.symiService.startService(it)
           }
           var ss ="categories in intent "
           for(cc in intent.categories){ ss.plus(cc  + " ")}
           Log.i("test", ss)
*************************************************************************************/


//---------------------------------------------------------------------------------------------


           if (outputData!!.savedData.length > 0) {
             //  generatedIcon.generatedData = outputData!!.savedData

               val oStream =
                   context.openFileOutput(generatedIcon.generatedDataFileName, Context.MODE_APPEND)
               oStream.bufferedWriter(Charsets.UTF_8).write(outputData.savedData)
               oStream.flush(); oStream.close()

               if (outputData!!.pngBufferLen > 0) {
                //   generatedImage.byteArray =
                 //      ByteArray(outputData!!.pngBufferLen) { i: Int -> (outputData!!.pngBuffer[i]) }
                   generatedImage.len = outputData!!.pngBufferLen
                   generatedImage.iconImageFileName = "symimage" + date + ".png"
                   try {
                       val imFile = File(imagesDirPath, generatedImage.iconImageFileName)
                       val pngStream = FileOutputStream(imFile)
                       //val pngStream = context.openFileOutput(
                        //   generatedImage.iconImageFileName,
                        //   Context.MODE_APPEND
                       //)

                       val image = BitmapFactory.decodeByteArray(outputData.pngBuffer, 0, outputData.pngBufferLen) // generatedImage.getBitmap()
                       image?.compress(
                           Bitmap.CompressFormat.PNG,
                           100,
                           pngStream
                       ).also {
                           when(size){
                               TINY -> tinyIm = image
                               MEDIUM -> medIm = image
                               LARGE -> largeIm = image
                           }
                       }
                       //   pngStream.write(pngBuf,0,outputData.pngBufferLen)
                       // pngStream.write(bbb, 0, 3)
                       pngStream.flush()
                       pngStream.close()
                   } catch (xx: Exception) {
                       xx.message?.let { Log.e("runSymiExample", it) }
                   }
               } else { /* error */
                   Log.e("runSymiExample", "No output data from JNI call.")
               }
               //If it is TINY set the TINY generated image for icon.

               Log.i("runSymiExample", "completed JNI call seeemingly OK width = " +symi.width)
               val generatedImageData = GeneratedImageData(UUID.randomUUID(),generatedIcon.id,
                   generatedImage.iconImageFileName,generatedImage.len )
               genIAD = GeneratedIconAndImageData( generatedIcon, generatedImageData)
               when(symi.width ){
                 TINY ->  {Log.i("runSymiExample","assigning TINYs")
                       generatedTinyIAD =  GeneratedIconAndImageData( generatedIcon, generatedImageData)
                        generatedTinyImage = generatedImage}
                   MEDIUM -> {
                       generatedMedIAD =
                           GeneratedIconAndImageData(generatedIcon, generatedImageData)
                        generatedMedImage = generatedImage
                   }
               }
             }
        }
        return  generateJob
        }

    fun saveTinySymi(): String{
        val tag = "saveTinySymi"
        if (generatedTinyIAD != null) {
            Log.d(tag,"generatedTinyAD not null" )


          symiRepo.addGeneratedIconAndData(iconDef, symIcon, symiTiny, generatedTinyIAD!!)
Log.d(tag, "done repo add TINY. Width is "+ symiTiny.width + " length is " + (generatedTinyIAD!!.generatedImageData?.len
    ?: 0))
            return "TINY_SAVED"
        }
        else return "TINY_FAIL"
    }

    fun saveSymi() {
  //      generatedTinyIAD?.let{
    //        symiRepo.addGeneratedIconAndData(iconDef, symIcon, symiTiny!!, it)
      //  }
        genIAD?.let { symiRepo.addGeneratedIconAndData(iconDef, symIcon, symi, it)
            Log.d("save symi", "done repo add. Width is "+ symi.width + " length is " + (it.generatedImageData?.len
                ?: 0))
        }

        }

    /******************************************************
    fun update(o: Observable?, arg: Any?) {
        Log.i("MainViewModel update", "liveList size: " + liveList?.size.toString())
    }
***********************************************************/
}