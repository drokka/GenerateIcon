package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.drokka.emu.symicon.generateicon.SymiRepo
import com.drokka.emu.symicon.generateicon.data.*
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

    var liveList:List<GeneratedIconAndImageDataMerged?>? = null

    val symImageListAll:LiveData<List<GeneratedIconWithAllImageData>> = symiRepo.getAllGeneratedIconWithAllImageData()
    var saveSymiData = false
    private   var genIAD:GeneratedIconAndImageData? = null
        get() = field
    fun clearGeneratedImage(){
        genIAD = null
    }

    var generatedTinyIAD:GeneratedIconAndImageData? = null
    var generatedTinyImage:GeneratedImage? = null
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

        var generatedIcon = GeneratedIcon(gen_def_id = symi.sym_icon_id, generatedData = "",
            generatedDataFileName = "symdata"
        )
        var generatedImage = GeneratedImage(generatedIcon, null, 0, "symimage")

    var companion = Companion

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
        }catch(x:Exception){}

    }
    fun setAlpha(sz: Double){
        try {
            iconDef.alpha = sz.toDouble()
        }catch(x:Exception){}
    }
    fun setBeta(sz: Double){
        try {
            iconDef.beta = sz.toDouble()
        }catch(x:Exception){}
    }
    fun setGamma(sz: Double){
        try{
        iconDef.gamma = sz.toDouble()
        }catch (x:Exception){}
    }
    fun setOmega(sz: Double){
        try{
        iconDef.omega = sz.toDouble()
        }catch (x:Exception){}
    }
    fun setMa(sz: Double){
        try{
        iconDef.ma = sz.toDouble()
        }catch (x:Exception){}
    }

    fun resetSymiDef(data:SymImageDefinition){

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
    fun setSymiData(allImageData: GeneratedIconWithAllImageData){
        isLoading = true
        //Assume it's already been added to the database. This only called from an item view
        // of DB list of symis. So no need to set ids etc!
        // Do need to manage isdirty

        resetSymiDef(fromGeneratedIconWithAllImageData( allImageData))

        generatedIcon = GeneratedIcon(
        generatedData = allImageData.generatedData,
        generatedDataFileName = allImageData.generatedDataFileName,
            gen_def_id = symi.gen_def_id
        )

        generatedImage = GeneratedImage(generatedIcon,allImageData.byteArray,
            allImageData.len,allImageData.iconImageFileName)

        val generatedImageData = GeneratedImageData(allImageData.generatedImageDataId,generatedIcon.id,
            generatedImage.iconImageFileName,generatedImage.byteArray,generatedImage.len )

        genIAD = GeneratedIconAndImageData( generatedIcon, generatedImageData)

        when(allImageData.width){
            TINY -> {generatedTinyImage = generatedImage
                        generatedTinyIAD = genIAD
                    Log.d("setSymiData", "adding TINY width = " + symi.width.toString())}
            MEDIUM -> {
                        generatedMedImage = generatedImage
                    generatedMedIAD = genIAD
                Log.d("setSymiData", "adding TINY width = " + symi.width.toString())
        }
        }
        isLoading = false
    }

    fun imageExists(sz:Int):Boolean{
        return (symi.width == sz )&& generatedImage.len >0
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun runSymiExample(context: Context, size: Int = 0, count: Int = 0): Deferred<Unit> {
       // if(isLoading ) return
  //      if(!(genIAD == null)) return
        val TAG = "runSymiExample"
        val i1 = Log.i(TAG, "before scoped launch")
        val runRequired = !imageExists(size)
  //      Log.i(TAG , "runRequired is " + runRequired + "number of DB entries is " + symImageListAll.value?.size)
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
     //   clearGeneratedImage()
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
               generatedIcon.generatedData = outputData!!.savedData

               val oStream =
                   context.openFileOutput(generatedIcon.generatedDataFileName, Context.MODE_APPEND)
               oStream.bufferedWriter(Charsets.UTF_8).write(generatedIcon.generatedData)
               oStream.flush(); oStream.close()

               if (outputData!!.pngBufferLen > 0) {
                   generatedImage.byteArray =
                       ByteArray(outputData!!.pngBufferLen) { i: Int -> (outputData!!.pngBuffer[i]) }
                   generatedImage.len = outputData!!.pngBufferLen
                   generatedImage.iconImageFileName = "symimage" + date + ".png"
                   try {
                       val imFile = File(imagesDirPath, generatedImage.iconImageFileName)
                       val pngStream = FileOutputStream(imFile)
                       //val pngStream = context.openFileOutput(
                        //   generatedImage.iconImageFileName,
                        //   Context.MODE_APPEND
                       //)

                       val image = generatedImage.getBitmap()
                       image?.compress(
                           Bitmap.CompressFormat.PNG,
                           100,
                           pngStream
                       )
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
                   generatedImage.iconImageFileName,generatedImage.byteArray,generatedImage.len )
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
Log.d(tag, "done repo add")
            return "TINY_SAVED"
        }
        else return "TINY_FAIL"
    }

    fun saveSymi() {
  //      generatedTinyIAD?.let{
    //        symiRepo.addGeneratedIconAndData(iconDef, symIcon, symiTiny!!, it)
      //  }
        genIAD?.let { symiRepo.addGeneratedIconAndData(iconDef, symIcon, symi, it) }

        }

    /******************************************************
    fun update(o: Observable?, arg: Any?) {
        Log.i("MainViewModel update", "liveList size: " + liveList?.size.toString())
    }
***********************************************************/
}