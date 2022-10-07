package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.drokka.emu.symicon.generateicon.SymiRepo
import com.drokka.emu.symicon.generateicon.clear
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.database.SymiTypeConverters
import com.drokka.emu.symicon.generateicon.getBitmap
import com.drokka.emu.symicon.generateicon.getGeneratedData
import com.drokka.emu.symicon.generateicon.nativewrap.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class MainViewModel() : ViewModel() {

    companion object {

        /** data access using Room **/
        val symiRepo = SymiRepo.get()

    }

    var clrFunctionExp: Double = 0.0
    val symiNativeWrapper = SymiNativeWrapper(this).also {
        Log.d("MainViewModel create SymiNativeWrapper", "wrapper on thread::  " + Thread.currentThread().id.toString())
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

    val paletteList: LiveData<List<ClrPalette>> = symiRepo.getAllClrPalette()
    fun getSymBigsList():List<GeneratedIconWithAllImageData>{
        return  symiRepo.getAllGeneratedIconWithAllImageDataSize(LARGE)
    }
    var workItemsList = mutableMapOf<UUID, String>()
    //var symiMedList = List<GeneratedIconWithAllImageData>(0)

    //val symImageListAll:LiveData<List<GeneratedIconAndImageData>> = symiRepo.getAllSymIconData()
    var saveSymiData = false
    var genIAD:GeneratedIconAndImageData? = null
        get() = field

    var imCounter = 0

    var storageCheckDone = false

    fun clearGeneratedImage(){

        genIAD = null
        generatedMedIAD = null
        generatedTinyIAD = null

        generatedImage.clear()
        generatedMedImage?.clear()

        generatedTinyImage?.clear()
        generatedLargeImage?.clear()

        generatedIcon.clear()
        generatedMedIcon.clear()
        generatedTinyIcon.clear()

        tinyIm = null
        medIm = null
        largeIm = null

        medSymDataString =""
        tinySymDataString = ""

    }

    var clrFunction:String = "default"
    var colourChanged = false
    //The C++ library uses inverted alpha so 0 is opaque?? or something weird.
    var bgClrInt:IntArray = intArrayOf(255,255,255,255)
    var minClrInt:IntArray = intArrayOf(255,0,25,25)
    var maxClrInt:IntArray = intArrayOf(0,0,255,255)

    var bgClr = doubleArrayOf(1.0,1.0,1.0,1.0)
    var minClr = doubleArrayOf(0.99,0.0,0.1, 0.2)
    var maxClr = doubleArrayOf(0.0, 0.0, 1.0, 1.0)
    var generatedTinyIAD:GeneratedIconAndImageData? = null
    var generatedTinyImage:GeneratedImage? = null
    var generatedLargeImage:GeneratedImage? = null

    var tinyIm:Bitmap? = null
    var medIm:Bitmap? = null
    var largeIm:Bitmap? = null

    var medSymDataString =""
    var tinySymDataString = ""
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
    var symiMed = GeneratorDef(sym_icon_id = symIcon.sym_icon_id,width = MEDIUM,
        height = MEDIUM, iterations = GO_GO)
    var symiLarge = GeneratorDef(sym_icon_id = symIcon.sym_icon_id,width = LARGE,
        height = LARGE, iterations = GO_GO_GO)
        var generatedIcon = GeneratedIcon(gen_def_id = symi.gen_def_id,
            generatedDataFileName = "symdata"
        )
    var generatedMedIcon = GeneratedIcon(gen_def_id = symiMed.gen_def_id,
        generatedDataFileName = "symdataMed"
    )
    var generatedTinyIcon = GeneratedIcon(gen_def_id = symiTiny.gen_def_id,
        generatedDataFileName = "symdataTiny"
    )
    var generatedLargeIcon = GeneratedIcon(gen_def_id = symiLarge.gen_def_id,
        generatedDataFileName = "symdataLarge"
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
         clearGeneratedImage()
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
        }catch (_:Exception){}
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

    fun setDegSym(degreeSymText:CharSequence){
        try {
            iconDef.degreeSym = degreeSymText.toString().toInt()
            clearGeneratedImage()
        }catch (x:Exception){
            Log.e("setDegSym","Error: "+ x.message)
        }
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
                    quiltType = data.quiltType,
                    degreeSym = data.degreeSym
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
        symiTiny = GeneratorDef(
            height = TINY,
            width = TINY,
            iterations = QUICK_LOOK,
            sym_icon_id = symIcon.sym_icon_id
        )
        symiMed = GeneratorDef(
                height = MEDIUM,
        width = MEDIUM,
        iterations = GO_GO,
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
            degreeSym = allImageData.degreeSym,
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
               generatedImage.iconImageFileName,allImageData.bgClr, allImageData.minClr, allImageData.maxClr, clrFunction,
                clrFunctionExp, generatedImage.len
            )
            bgClr = SymiTypeConverters.JSONArrayToDoubleArray(allImageData.bgClr)
        minClr = SymiTypeConverters.JSONArrayToDoubleArray(allImageData.minClr)
        maxClr = SymiTypeConverters.JSONArrayToDoubleArray(allImageData.maxClr)
        setIntColourArrays()
        genIAD = GeneratedIconAndImageData(generatedIcon, generatedImageData)
     //   }

        when(symi.width){
            TINY -> {
                if(allImageData.width != TINY){
                    Log.e("setSymiData", "allImageData.width not equal to symi.width")
                } /*else { */
                    generatedTinyImage = generatedImage
                    generatedTinyIAD = genIAD
                generatedImage.getBitmap(context)?.let{tinyIm = it}
                    tinySymDataString = generatedTinyImage!!.getGeneratedData(context)
                    Log.d("setSymiData", "adding TINY symi.width = " + symi.width.toString() +
                            " allImageData.width = " +allImageData.width.toString() +
                    " bgClr " + bgClr[0] +" allImageData.bgClr " + allImageData.bgClr[0] +
                    "bgClrInt[0] " + bgClrInt[0])
              //  }
            }
            MEDIUM -> {
                        generatedMedImage = generatedImage
                    generatedMedIAD = genIAD
                medIm = generatedImage.getBitmap(context)
                Log.d("setSymiData", "adding MEDIUM width = " + symi.width.toString())

                medSymDataString = generatedImage.getGeneratedData(context)
        }
            LARGE-> {
            generatedLargeImage = generatedImage
           // generatedLargeIAD = genIAD
            largeIm = generatedImage.getBitmap(context)
            Log.d("setSymiData", "adding  width = " + symi.width.toString())

           // medSymDataString = generatedImage.getGeneratedData(context)
        }

        }
        // Actually with current view sql will always be TINY that has loaded. So load MEDIUM.
        val tempIconDef = IconDef(    UUID.randomUUID(), allImageData.lambda,
            allImageData.alpha,
            allImageData.beta,
            allImageData.gamma,
            allImageData.omega,
            allImageData.ma,
            allImageData.quiltType,
            allImageData.degreeSym
        )
        var symiMedList = symiRepo.getGeneratedIconWithAllImageDataSizeClr(tempIconDef, MEDIUM, SymiTypeConverters.JSONArrayfromDoubleArray( bgClr),
            SymiTypeConverters.JSONArrayfromDoubleArray( minClr),
            SymiTypeConverters.JSONArrayfromDoubleArray( maxClr))
        if(symiMedList.size == 0) {
            symiMedList = symiRepo.getGeneratedIconWithAllImageDataSize(tempIconDef, MEDIUM) // fall back to any colour
            Log.d("setSymiData", "No medium inage for this colour scheme, falling back to any.")
        }
        if(symiMedList.size > 0){
            if(symiMedList.size >1){
                Log.i("setSymiData", "There is more than one MEDIUM image!! Number is: "+symiMedList.size.toString())
            }
            val generatedMedIcon = GeneratedIcon(symiMedList[0].genIconId,symiMedList[0].gen_def_id,symiMedList[0].generatedDataFileName)   //it.generatedIcon

            generatedMedImage =  GeneratedImage( generatedMedIcon, symiMedList[0].len, symiMedList[0].iconImageFileName)
            val generatedMedImageData = GeneratedImageData(
                symiMedList[0].generatedImageDataId, generatedMedIcon.id,
                symiMedList[0].iconImageFileName, symiMedList[0].bgClr, symiMedList[0].minClr, symiMedList[0].maxClr,
                symiMedList[0].clrFunction, symiMedList[0].clrFunExp, symiMedList[0].len
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
        haveImage = haveImage&&!colourChanged
        if(!haveImage){
            //check DB
            val symiSizedDataList = symiRepo.getGeneratedIconWithAllImageDataSizeClr(iconDef,sz, SymiTypeConverters.JSONArrayfromDoubleArray( bgClr),
                SymiTypeConverters.JSONArrayfromDoubleArray( minClr),
                SymiTypeConverters.JSONArrayfromDoubleArray( maxClr))
            if(symiSizedDataList.isNotEmpty()) {
                generatedIcon = getGeneratedIcon(symiSizedDataList[0])
                generatedImage = getGeneratedImage(generatedIcon, symiSizedDataList[0])

                genIAD = getGeneratedIconAndImageData(
                    generatedIcon, GeneratedImageData(
                        symiSizedDataList[0].generatedImageDataId, generatedIcon.id,
                        symiSizedDataList[0].iconImageFileName, symiSizedDataList[0].bgClr,
                        symiSizedDataList[0].minClr, symiSizedDataList[0].maxClr,
                        symiSizedDataList[0].clrFunction,symiSizedDataList[0].clrFunExp, symiSizedDataList[0].len
                    )
                )
                val bitmap = generatedImage.getBitmap(context)
                if (bitmap != null) {
                    when (symiSizedDataList[0].width) {
                        TINY -> {
                            generatedTinyImage = generatedImage
                            generatedTinyIAD = genIAD
                            Log.d("imageExists", "adding TINY sz = " + sz.toString() + " ")
                            tinyIm = bitmap
                        }
                        MEDIUM -> {
                            generatedMedImage = generatedImage
                            generatedMedIAD = genIAD

                            medIm = bitmap
                            Log.d("setSymiData", "adding MEDIUM width sz = " + sz.toString())

                            medSymDataString = generatedImage.getGeneratedData(context)
                        }
                        LARGE -> {
                            largeIm = bitmap
                        }
                    }

                    haveImage = true
                }
            }
        }
        return haveImage
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun runSymiExample(context: Context, size: Int = 0, count: Int = 0, quickDraw:Boolean = false): Deferred<Unit>{
       // if(isLoading ) return
  //      if(!(genIAD == null)) return
        val TAG = "runSymiExample"
        val runRequired = !imageExists(context, size)
        Log.i(TAG , "runRequired is " + runRequired + "number of DB entries is " + symImageListAll.value?.size)
       if (!runRequired) {
           return CoroutineScope(Dispatchers.Main).async{/* do nothing*/}
       }
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
        colourChanged = false
        var outputData:OutputData? = null
       val generateJob = CoroutineScope(Dispatchers.Main).async {
           Log.d(TAG, "runSymiExample: symi.lambda " + iconDef.lambda.toString())
           val outputDataJob = symiNativeWrapper.runSample(symi, iconDef, bgClr, minClr, maxClr, clrFunction, clrFunctionExp)
           outputDataJob.await()
           outputData = outputDataJob.getCompleted()

           if (outputData?.savedData?.isNotEmpty() == true) {
               //  generatedIcon.generatedData = outputData!!.savedData

               if (!outputData?.savedData!!.startsWith("Error")) {

                   generatedImage.len = size*size*4  // make it rgbaBuffer len //outputData!!.pngBufferLen

                   Log.i(
                       "MainViewModel runSymiExMPLE",
                       "gENERATE Params: " + outputData!!.paramsUsed
                   )
                   when (size) {
                       TINY -> {
                           tinySymDataString = outputData!!.savedData
                           tinyIm = outputData!!.bitmap //bitmapFromBytes(outputData)
                           if(tinyIm == null){
                               Log.e("MainViewModel runSymiExMPLE", "TINY just extracted BUT IS NULL bitmap")
                           }
                           Log.d("MainViewModel runSymiExMPLE", "TINY just set tinyIm size is " + tinyIm?.height)
                       }
                       MEDIUM -> {
                           medSymDataString = outputData!!.savedData
                           medIm = outputData!!.bitmap
                           saveDataFile(context, medSymDataString, MEDIUM,"symdata"+imCounter.toString()+"_"+Date().time.toString()+".txt")
                           saveImage(
                               context,
                               medIm,
                               MEDIUM,
                               imCounter.toString()+"_"+Date().time.toString()+"MED.png",
                               ""
                           )

                       }
                       else -> {
                           Log.d("runSymiExample","size not TINY or MEDIUM")

                       }

                   }


               } else {
                   Log.e(
                       "runSymiExample",
                       "output data is error message: " + outputData!!.savedData
                   )
               }

           }
           }
        outputData?.bitmap = null
        outputData?.savedData =""
        System.gc()
        return  generateJob
        }

    private fun bitmapFromBytes(outputData: OutputData?): Bitmap? {
        Log.d("bitmapFromBytes", outputData!!.pngBuffer.slice(0..200).toString())

       // val options = BitmapFactory.Options()
      //  options.inPreferredConfig = Bitmap.Config.ARGB_8888
      //  val bytes = rgbaToArgb(outputData!!.pngBuffer)
        val  bitmap = BitmapFactory.decodeByteArray(
            outputData!!.pngBuffer,
            0,
            outputData!!.pngBufferLen // , options
        )
        return bitmap
    }

    private fun rgbaToArgb(bytes: ByteArray): ByteArray {
     //  Log.d("rgbaToArgb", bytes.slice(0..200).toString())
        return bytes
    }

    fun saveDataFile(context: Context, savedData:String, size: Int, fname:String){
        when(size) {
            TINY -> generatedTinyIcon.generatedDataFileName = fname
            MEDIUM ->generatedMedIcon.generatedDataFileName = fname
            LARGE -> generatedLargeIcon.generatedDataFileName = fname
        }
        generatedIcon.generatedDataFileName = fname

        val oStream = context.openFileOutput(fname, Context.MODE_PRIVATE)

        ZipOutputStream(oStream).use { zipStream ->
            val zipEntry = ZipEntry(fname)
            zipStream.putNextEntry(zipEntry)
            zipStream.write(savedData.toByteArray())
        }

        //oStream.bufferedWriter(Charsets.UTF_8).write(savedData)
        oStream.flush(); oStream.close()
    }

    fun readDataFile(context: Context, fname: String): String{
        val iStream = context.openFileInput(fname)
        var symData:String =""
        ZipInputStream(iStream).use {
            try {

                val zipEntry = it.nextEntry
             //   if (zipEntry.name == fname) {
                    symData = it.bufferedReader().readText()
             //   }
            }catch (xx:Exception){
                Log.e("readDataFile", "exception loading file: " +xx.message)

            }
        }
        return symData
    }

fun saveImage(
    context: Context,
    image: Bitmap?,
    size: Int,
    imFileSuffix: String,
    imageFileName: String
) {
    imCounter++
    var fname:String
    if(imageFileName.isNotEmpty()){
     fname =  imageFileName}
    else{ fname =  "symimage_" + imCounter.toString() + "_" + imFileSuffix}

    when(size) {
        MEDIUM -> generatedMedImage?.iconImageFileName = fname
        TINY -> generatedTinyImage?.iconImageFileName = fname
        LARGE ->generatedLargeImage?.iconImageFileName = fname
    }
    generatedImage.iconImageFileName = fname
    try {
        val imagesDirPath = File(context.getFilesDir(), "images")
        Log.i("saveImage", "dirPath is:" + imagesDirPath.toString())
        imagesDirPath.mkdirs()
        val imFile = File(imagesDirPath, fname)
        val pngStream = FileOutputStream(imFile)
        //   image = BitmapFactory.decodeByteArray(outputData.pngBuffer, 0, outputData.pngBufferLen) // generatedImage.getBitmap()
        image?.compress(
            Bitmap.CompressFormat.PNG,
            100,
            pngStream
        )
        pngStream.flush()
        pngStream.close()
    } catch (xx: Exception) {
        xx.message?.let { Log.e("saveImage", it) }
    }
        Log.d(
            "saveImage",
            " saveImage completed after JNI call seeemingly OK size = " + size
        )


        if (image != null) {
            when (image.width) {
                TINY -> {
                    Log.i("runSymiExample", "assigning TINYs")

                    val generatedImageData = GeneratedImageData(
                        UUID.randomUUID(), generatedTinyIcon.id,
                        generatedImage.iconImageFileName!!,
                       SymiTypeConverters.JSONArrayfromDoubleArray( bgClr),
                        SymiTypeConverters.JSONArrayfromDoubleArray(  minClr),
                            SymiTypeConverters.JSONArrayfromDoubleArray( maxClr),clrFunction, clrFunctionExp, generatedImage?.len!!
                    )
                    generatedTinyIAD = GeneratedIconAndImageData(generatedTinyIcon, generatedImageData)
                    generatedTinyImage = generatedImage
                    //  saveTinySymi()
                }
                MEDIUM -> {
                    val generatedImageData = GeneratedImageData(
                        UUID.randomUUID(), generatedMedIcon.id,
                        generatedImage?.iconImageFileName!!,
                        SymiTypeConverters.JSONArrayfromDoubleArray( bgClr),
                            SymiTypeConverters.JSONArrayfromDoubleArray( minClr),
                                SymiTypeConverters.JSONArrayfromDoubleArray( maxClr),clrFunction, clrFunctionExp, generatedImage?.len!!
                    )

                    generatedMedIAD = GeneratedIconAndImageData(generatedMedIcon, generatedImageData)
                    generatedMedImage = generatedImage
                    //          medSymDataString = outputData.savedData
                    //   saveSymi()
                }
                LARGE -> {
                    val generatedImageData = GeneratedImageData(
                        UUID.randomUUID(),
                        generatedLargeIcon.id,
                        generatedImage.iconImageFileName,
                        SymiTypeConverters.JSONArrayfromDoubleArray(bgClr),
                        SymiTypeConverters.JSONArrayfromDoubleArray(minClr),
                        SymiTypeConverters.JSONArrayfromDoubleArray(maxClr),
                        clrFunction,
                        clrFunctionExp,
                        generatedImage.len
                    )

                 val    generatedLargeIAD =
                        GeneratedIconAndImageData(generatedLargeIcon, generatedImageData)
                    generatedLargeImage = generatedImage

                    symiRepo.addGeneratedIconAndData(iconDef,symIcon, symiLarge, generatedLargeIAD)
                    //          medSymDataString = outputData.savedData
                    //   saveSymi()
                }
                else ->{
                    val generatedImageData = GeneratedImageData(
                        UUID.randomUUID(), generatedIcon.id,
                        generatedImage.iconImageFileName, SymiTypeConverters.JSONArrayfromDoubleArray( bgClr),
                        SymiTypeConverters.JSONArrayfromDoubleArray( minClr),
                        SymiTypeConverters.JSONArrayfromDoubleArray( maxClr),clrFunction,clrFunctionExp, generatedImage.len
                    )


                    genIAD = GeneratedIconAndImageData(generatedIcon, generatedImageData)
                }
            }
            //Also save data. Make only deletes explicit.
            //  saveSymi()
        }

    }


fun saveTinySymi(context: Context): String{
        val tag = "saveTinySymi"
    if(tinySymDataString.isNullOrEmpty()){
        if(symi.width == TINY){
            Log.d("saveTinySymi", "tinySymDataString NO DATA but symi width TINY")
        }
        Log.d("saveTinySymi", "tinySymDataString NO DATA")

        return "no data string"
    }
    if(tinyIm == null){
        return "no tiny image"
    }
    val fname = "symdata"+imCounter.toString()+"_"+Date().time.toString()+".txt"
    saveDataFile(context, tinySymDataString, TINY,fname)
    saveImage(
        context,
        tinyIm,
        TINY,
        imCounter.toString()+"_"+Date().time.toString()+".png",
        ""
    )

        if (generatedTinyIAD != null) {
            Log.d(tag,"generatedTinyAD not null" )

            generatedTinyIAD!!.generatedIcon.generatedDataFileName = fname

            symiRepo.addGeneratedIconAndData(iconDef, symIcon, symiTiny, generatedTinyIAD!!)
Log.d(tag, "done repo add TINY. Width is "+ symiTiny.width + " length is " + (generatedTinyIAD!!.generatedImageData?.len
    ?: 0))
            return "TINY_SAVED"
        }
        else return "TINY_FAIL"
    }

    fun saveMedSymImage(context: Context?){
        //Assume called after MEDIUM recolour. So just saving image.
        // medIm must have been set to the recolour
        val tag = "saveMedSymImage"
        if(generatedMedIAD?.generatedImageData == null){
            Log.e(tag,"Error genIAD generatedMedIAD im data is null. NOT saving image")
            return
        }else {
            if (context == null) {
                Log.e(tag, "context is null doing nothing!!")
                return
            } else {
              //  genIAD = generatedMedIAD   //saveImage updates image file name on this.
                saveImage(
                    context,
                    medIm,
                    MEDIUM,
                    imCounter.toString() + "_RCLR_" + Date().time.toString() + ".png",
                    ""
                )
                // JUST the image!!!!
                var generatedImageData = generatedMedIAD?.generatedImageData!!
                generatedImageData.gid_id = UUID.randomUUID()   //New image (sigh, all this should be in repo)

                generatedMedIAD?.generatedIcon?.let { symiRepo.addGeneratedIconAndData(iconDef,symIcon,symiMed, generatedMedIAD!!) }

            }
        }
    }

    fun saveSymi() { //Only called after a generate click so should have MEDIUM image
  //      generatedTinyIAD?.let{
    //        symiRepo.addGeneratedIconAndData(iconDef, symIcon, symiTiny!!, it)
      //  }
        // image id updated in recolour call
        generatedMedIAD?.let {
            symiRepo.addGeneratedIconAndData(iconDef, symIcon,  symiMed, it)
            Log.d("save symi", "done repo add. Width is "+ symiMed.width + " length is " + (it.generatedImageData?.len
                ?: 0) + " MED image file is " + (generatedMedIAD?.generatedImageData?.iconImageFileName
                ?: "no medIAD or icon image file name")
            )
        }

        }

    fun runReColour(  //Set default colours and recolour MEDIUM image
        context: Context,
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray,
        clrFun: Double
    ) : Deferred<Unit?> {
        setColours(bgClrArray, minClrArray, maxClrArray, clrFun)

        var symDataStr: String? = medSymDataString
        if (symDataStr == "") {
            val genData = symiRepo.getGeneratedIconWithAllImageDataSize(iconDef, MEDIUM) // generatedMedImage?.getGeneratedData(context)
            if(genData.isNotEmpty()){
                symDataStr = genData[0].getGeneratedData(context)
            }

        }

        // generatedMedIAD?.generatedImageData?.gid_id = UUID.randomUUID()
        // doReColour used for quick recolour, where this values not set.
      return   doReColour(context, null,MEDIUM, symDataStr, bgClrArray, minClrArray, maxClrArray,
            clrFun)
    }

    private fun setColours(
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray,
        clrFun: Double
    ) {
        colourChanged = true
        bgClrArray.forEachIndexed { i, vali ->
            this.bgClrInt[i] = vali
        }
        minClrArray.forEachIndexed { i, vali ->
            this.minClrInt[i] = vali
        }
        maxClrArray.forEachIndexed { i, vali ->
            this.maxClrInt[i] = vali
        }

        bgClr = convItoD(bgClrInt)
        minClr = convItoD(minClrInt)
        maxClr = convItoD((maxClrInt))
        clrFunctionExp = clrFun
    }
    private fun setIntColourArrays(){
        bgClrInt = convDtoI(bgClr)
        minClrInt = convDtoI(minClr)
        maxClrInt = convDtoI(maxClr)
        }

    fun quickRecolour(context: Context, imageView: ImageView?,bgClrArray: IntArray,
                      minClrArray: IntArray, maxClrArray: IntArray,
                      clrFun: Double ) : Deferred< Unit?>  {
        var symDataStr: String? = tinySymDataString
        if (symDataStr == "") {
            symDataStr = generatedTinyImage?.getGeneratedData(context)
        }
       return  doReColour(context, imageView, TINY, symDataStr, bgClrArray, minClrArray, maxClrArray,
            clrFun)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun doReColour(context: Context,
                   imageView: ImageView?, sz:Int, symDataStr: String?, bgClrArray: IntArray,
                   minClrArray: IntArray, maxClrArray: IntArray,
                   clrFun: Double): Deferred<Unit?> {
        val bgClr = convItoD(bgClrArray)
        val minClr = convItoD(minClrArray)
        val maxClr = convItoD(maxClrArray)

        val reColrJob = CoroutineScope(Dispatchers.Main).async {

                        val rcOutDeferred = symDataStr?.let {symiNativeWrapper.reColourSym(
                            it,
                            sz,
                            bgClr,
                            minClr,
                            maxClr,
                            "default",
                            clrFun
                        ) }
                            rcOutDeferred?.await()
                          val outputData = rcOutDeferred?.getCompleted()

                        val date = Date().time.toString()
                        rcOutDeferred?.isCompleted.let {
                        val image = outputData?.bitmap //bitmapFromBytes(outputData)

                        if (image == null || outputData?.savedData?.startsWith(
                                "Error",
                                true
                            ) == true
                        ) {
                             this.cancel("reColourSym: There was a problem creating bitmap")
                         /*   Log.e(
                                "recolour",
                                "null image returned from jni " + outputData?.savedData?.substring(
                                    100
                                )

                            )

                            */
                        } else {
                            if (imageView == null && sz == MEDIUM) { // doing MEDIUM or LARGE
                                //      saveImage(context, bitmap, date+".png")
                                //  saveSymi() doesn't seem to happen when expected
                                medIm = image
                            } else { //quick recolour for display only
                                image?.let {
                                    if (imageView != null) {
                                        imageView.setImageBitmap(image)
                                    }
                                }
                            }
                            //   }  //else { Log.e("reColour", "returned error: " + outputData?.savedData)}
                        }
                    }
                }
                  //  outputData?.bitmap = null
                 //   outputData?.savedData = ""

              //  }
               //
      //  }
        System.gc()
    return reColrJob
    }

    private fun convItoD(clr: IntArray): DoubleArray {
        var doubleArray:DoubleArray = DoubleArray(4)
        clr.forEachIndexed { index, i ->  doubleArray[index] = i/255.0 }
        return doubleArray
    }

    private fun convDtoI(clr: DoubleArray): IntArray{
        var intArray = IntArray(4)
        clr.forEachIndexed{ i, dval -> intArray[i] = (255*dval).toInt()}
        return  intArray
    }

    fun deleteSymiData(context: Context,generatedIconWithAllImageData: GeneratedIconWithAllImageData) {
        symiRepo.deleteSymIcon(context, generatedIconWithAllImageData)
    }

    @Throws(Exception::class)
    @OptIn(ExperimentalCoroutinesApi::class)
    fun runSymiExampleWorker(context: Context): Pair<UUID, String> {
        val TAG = "runSymiExampleWorker"
        var workerId:UUID? = null
        var generatorDef = symi
        generatorDef.height = LARGE
        generatorDef.width = LARGE

        generatorDef.iterations = GO_GO_GO
        var reiter = false
        val tt = Date().time.toString()

        var newColour = false
        var imageFileName = "symBIG" +tt + ".png"
        var largeList = symiRepo.getGeneratedIconWithAllImageDataSizeClr(iconDef, LARGE,
            SymiTypeConverters.JSONArrayfromDoubleArray(bgClr),
            SymiTypeConverters.JSONArrayfromDoubleArray(minClr), SymiTypeConverters.JSONArrayfromDoubleArray(maxClr))
        if(largeList.isNotEmpty()) {
            imageFileName = largeList[0].iconImageFileName
        }
        else{
                largeList = symiRepo.getGeneratedIconWithAllImageDataSize(iconDef, LARGE)
            newColour = true
            }
        if(largeList.isNotEmpty()) {
            val symGenIconId = largeList[0].genIconId
            val generatedIcon = symiRepo.getGeneratedIcon(symGenIconId)
         //   val symData = generatedIcon.getGeneratedData(context)
            val fname = generatedIcon.generatedDataFileName
         //   if(symData.isNotEmpty()) {
            Log.d(TAG, "generatedLargeImage not null doing reiter")

            val ity = workItemsList.filterValues { v -> v==fname }
            if(!ity.isEmpty()){
                    //Already running for this data
                    Log.i("MVM runSymiExampleWorker","Not reiterating job already running")
                return ity.toList()[0]
                }
               // var outputData: OutputData? = null
//                    Log.d(TAG, "runSymiExample: symi.lambda " + iconDef.lambda.toString())


                Log.d(TAG, "colours[0] are: ${bgClr[0]} , ${minClr[0]} , ${maxClr[0]}")
                    workerId = symiNativeWrapper.runMoreIterWorker(
                        context,
                        GO_GO_GO.toLong(),
                        fname,
                        imageFileName,
                        bgClr, minClr, maxClr,
                        clrFunction,
                        clrFunctionExp
                    )

                  //  outputDataJob.await()
                  //  outputData = outputDataJob.getCompleted()

                   // saveOutputData(outputData, context, fname)
             //   }

                return Pair(workerId, fname)     //RETURN jump out here if we found existing bigs
          //  }
        }
        // MUST BE NEW BIG
        val dataFileName = "symdata"+"BIG" + tt +".txt"
        val idFname =
                symiNativeWrapper.runSampleWorker(context, dataFileName, imageFileName, symi, iconDef,
                    bgClr, minClr, maxClr, clrFunction, clrFunctionExp)
        System.gc()

        return idFname
    }

    fun saveOutputData(
        outputData: OutputData?,
        context: Context,
        fname: String,
        imageFileName:String =""
    ) {
        if (outputData?.savedData?.isNotEmpty() == true) {
            //  generatedIcon.generatedData = outputData!!.savedData

            if (!outputData?.savedData!!.startsWith("Error")) {

                generatedImage.len = outputData!!.pngBufferLen

                val bitmap = bitmapFromBytes(outputData)

                when (bitmap?.height) {
                    TINY -> {
                        tinySymDataString = outputData!!.savedData
                        tinyIm = bitmap
                    }
                    MEDIUM -> {
                        medSymDataString = outputData!!.savedData
                        medIm = bitmap
                        saveDataFile(
                            context,
                            medSymDataString,
                            MEDIUM, fname
                        )
                        saveImage(
                            context,
                            medIm,
                            MEDIUM,
                            imCounter.toString() + "_" + Date().time.toString() + "MED.png",
                            imageFileName
                        )

                    }
                    LARGE -> {
                        // largeSymDataString = outputData!!.savedData
                        largeIm = bitmap
                        saveDataFile(
                            context,
                            outputData!!.savedData,
                            LARGE,
                            fname
                        )
                        saveImage(
                            context,
                            largeIm,
                            LARGE,
                            imCounter.toString() + "_" + Date().time.toString() + ".png",
                            imageFileName
                        )

                    }
                    else -> {
                        Log.d("runSymiExample", "size not TINY or MEDIUM or LARGE")

                        bitmap?.height?.let {
                            saveDataFile(
                                context,
                                outputData!!.savedData,
                                it,
                                fname
                            )
                        }
                        if (bitmap != null) {
                            saveImage(
                                context,
                                bitmap,
                                bitmap.height,
                                imCounter.toString() + "_" + Date().time.toString() + ".png",
                                imageFileName
                            )
                        }

                    }
                }


            } else {
                Log.e(
                    "runSymiExample",
                    "output data is error message: " + outputData!!.savedData
                )
            }
        }
    }

    /******************************************************
    fun update(o: Observable?, arg: Any?) {
        Log.i("MainViewModel update", "liveList size: " + liveList?.size.toString())
    }
***********************************************************/

}