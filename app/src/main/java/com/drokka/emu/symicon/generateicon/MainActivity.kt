package com.drokka.emu.symicon.generateicon

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.drokka.emu.symicon.generateicon.R.id.*
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.ui.main.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import java.io.File
import java.io.FileInputStream
import java.util.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener, MainFragment.Callbacks , ImageIconFragment.Callbacks,
    SymIconListFragment.Callbacks, WrapListFragment.Callbacks,
    MainActivityFragment.Callbacks, PickColourFragment.Callbacks  , BigImageFragment.Callbacks{

    companion object  {

        /* this is used to load the  library on application
         * startup.
         */
        init {
            System.loadLibrary("emutil")
        }
    }
   private var mainActivityFragment:MainActivityFragment? = null
   private var mainFragment: MainFragment? = null
    private var symiListFragment: SymIconListFragment? = null
   private var wrapListFragment:WrapListFragment? = null
    private var imageIconFragment:ImageIconFragment? = null
    var bigImageFragment:BigImageFragment? = null
    private var pickColourFragment: PickColourFragment? = null
    private var bigImageViewPagerFragment:BigImageViewPagerFragment? = null
  //  val blankTag = "blankFragment"
    lateinit var viewModel: MainViewModel
 //   lateinit var recyclerViewModel: SymIconListViewModel
 var navController:NavController? = null   //
    /** data access using Room **/

    // private lateinit var  symiRepo:SymiRepo
 //   private lateinit var symIconList :LiveData<List<GeneratedIconAndImageData>>
    /*********************/
    private var SPLASH_DONE = false
    override fun onAttachedToWindow() {


        super.onAttachedToWindow()
        if (navController == null) {
            navController = findNavController(R.id.fragmentContainerView)
        }
    }

        override fun onResume() {
        super.onResume()
        SPLASH_DONE = false
        if(!viewModel.workItemsList.isEmpty()){
            val wm = WorkManager.getInstance(applicationContext)
            for(wi in viewModel.workItemsList){
               val workInfo = wm.getWorkInfoByIdLiveData(wi.key)
                workInfo.value?.let { checkWI(it,applicationContext,wi.key, wm) }
            }
        }
            System.gc()
    }

  /*  override fun ???? {
        super.???()
            //shut down pending work
        if(!viewModel.workItemsList.isEmpty()){
            val wm = WorkManager.getInstance(applicationContext)
            for(wi in viewModel.workItemsList){
                wm.cancelWorkById(wi.key)
            }
        }
        finish()
    }

   */
    override fun onImageIconSelected() {
        if(imageIconFragment == null) {
            imageIconFragment = ImageIconFragment.newInstance()
        }
        /****
        supportFragmentManager.beginTransaction().replace(id.container, imageIconFragment!!)
            .addToBackStack("imageIconFragment")
            .commit()
        ***********/
        //Make sure the TINY icon got saved before navigating to the bigger image display
      //  viewModel.saveSymi()
        if(navController?.currentDestination?.id == R.id.mainFragment) {
            navController?.navigate(R.id.action_mainFragment_to_imageIconFragment)
        }
    }

    /**********************************************************************
     * GENERATE
     */
    //MainFragment GENERATE. MainFragment is not the MainActivityFragment which is a navigation container
    @SuppressLint("SuspiciousIndentation")
    override fun onGenerateClicked(context:Context) :Deferred<Unit>?{
       // var generateJob:Job? = null
        var deferredJob:Deferred<Unit>? = null
/*********************************************
            findNavController(
                R.id.fragmentContainerView
            ).navigate(R.id.action_mainFragment_to_blankFragment2)
        }***************************************************************************/

            //Save the TINY image first, before navigating to generated Icon fragment.
            if(viewModel.generatedTinyIAD == null) {
                deferredJob = viewModel.runSymiExample(context, TINY, QUICK_LOOK)

                deferredJob!!.invokeOnCompletion { viewModel.saveTinySymi(context) }
            }else{
                viewModel.saveTinySymi(context)
            }

            deferredJob =     viewModel.runSymiExample(context, MEDIUM, GO_GO)
        return deferredJob
    }

    override fun generateLargeIcon(requireContext: Context, goBigButton: Button) {
        goBigButton.isEnabled = false
        goBigButton.isClickable = false //has no effect
      //  goBigButton.isFocusable = false
        try {

            val id: Pair<UUID, String> = viewModel.runSymiExampleWorker(requireContext)
            if(viewModel.workItemsList.containsValue(id.second)){ // Already a job running for this one
                return
            }
            setBigsIndicator(false)
            viewModel.workItemsList.put(id.first, id.second)
            requireContext.let { it1 ->
               val wm = WorkManager.getInstance(it1)
                wm.getWorkInfoByIdLiveData(id.first)
                    .observe(this) { workInfo ->
                        checkWI(workInfo, it1, id.first, wm)
                    }
            }
        } catch (xx:Exception){
            xx.message?.let { Log.i("generateLargeIcon", it) }
        }
        goBigButton.isEnabled = true
        goBigButton.isClickable = true
      //  goBigButton.isFocusable = true

        System.gc()
    }

        private fun checkWI(
            workInfo: WorkInfo,
            it1: Context,
            id: UUID,
            wm: WorkManager
        ) {

        if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
            Toast.makeText(applicationContext, "Go Big completed.", Toast.LENGTH_SHORT)
                .show()
            Log.i("Go Big checkWI", "success for work: " +id +" " + workInfo.toString())
            viewModel.workItemsList.remove(id)

            // viewModel.storeWork(requireContext(),workInfo.outputData)
        } else if (workInfo?.state == WorkInfo.State.ENQUEUED) {
            Toast.makeText(applicationContext, "Go Big  task queued."  , Toast.LENGTH_SHORT)
                .show()
            Log.d("Go Big checkWI", "queued")
        } else if (workInfo?.state == WorkInfo.State.RUNNING) {
            Toast.makeText(applicationContext, "Go Big task running." , Toast.LENGTH_SHORT)
                .show()
            Log.d("Go Big checkWI", "running" +id +" " )
        } else if(workInfo.state.isFinished) {
            Toast.makeText(
                applicationContext, "Go Big generation did not complete.",
                Toast.LENGTH_SHORT
            )
                .show()
            Log.e("Go Big checkWI", "Error generating large image. workinfo state: " +id +" "  + workInfo?.toString())
            viewModel.workItemsList.remove(id)

        } else {
            // could be Failed, cancelled or blocked

            Log.e("Go Big checkWI", "Fail? fall through generating large image. workinfo: " +id +" "  + workInfo?.toString())

            wm.cancelWorkById(id)
            viewModel.workItemsList.remove(id)
        }

        setBigsIndicator(viewModel.workItemsList.isEmpty())
    }

    private fun setBigsIndicator(workDone: Boolean) {
        val bigsWorkIndicator = findViewById<ImageButton>(R.id.bigsWorkImageButton)
        if(workDone) {
            bigsWorkIndicator.setImageResource(R.drawable.ic_baseline_circle_24green)
//                wrapListFragment?.sitBigsButtonVisible()

        }else{
            bigsWorkIndicator.setImageResource(R.drawable.ic_baseline_circle_24)

        }
        bigsWorkIndicator.visibility = View.VISIBLE

        bigsWorkIndicator.isDirty
      //  toolbar?.isDirty
    }

    override fun showBigImage() {
      //  if(bigImageFragment == null){
            bigImageFragment = BigImageFragment.newInstance(viewModel.largeIm, viewModel?.generatedLargeImage?.iconImageFileName)
      //  }
      //  if(viewModel.imageExists(LARGE)){
        //Save the big image
        //Now show it.
        //    viewModel.getIconBitmap(applicationContext, viewModel.genIAD?.generatedImageData!!.gid_id)?.let {

         //     val  bitMap = BitmapFactory.decodeByteArray(it, 0, viewModel.genIAD?.generatedImageData!!.len)
               // bigImageFragment?.bigImageView?.setImageBitmap(it)  // bigImageView null, OnCreateView not finished?
           //     bigImageFragment?.view?.findViewById<ImageView>(R.id.bigImageView)?.setImageBitmap(viewModel.largeIm)
            //    bigImageFragment?.view?.invalidate()
                if(navController?.currentDestination?.id == R.id.imageIconFragment) {
                    navController?.navigate(R.id.action_imageIconFragment_to_bigImageFragment)
                          }
       // }
    }

    override fun reColour(){  //Call from reColour button to open colours dialog
        if (pickColourFragment == null) {
            pickColourFragment = PickColourFragment.newInstance(viewModel.bgClrInt, viewModel.minClrInt, viewModel.maxClrInt)
            Log.d("main activity recolour", "pickColurfragment newInstance() called. " +
            " viewModel.bgClr[0] " + viewModel.bgClr[0] + " viewModel.bgClrInt[0] " + viewModel.bgClrInt[0])
        }
        if(navController?.currentDestination?.id == R.id.imageIconFragment) {
            navController?.navigate(action_imageIconFragment_to_pickColourFragment)
        }
    }

    override fun doQuickDraw(context: Context): Job {
        val generateJob =  viewModel.runSymiExample(context, TINY, QUICK_LOOK, true)

        return generateJob
    }

    /*
    private fun galleryAddPic2(imageUri:Uri, title:String) {

    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri)
    val savedImageURL = MediaStore.Images.Media.insertImage(
        requireContext().contentResolver,
        bitmap,
        title,
        "Image of $title"
    )
    Toast.makeText(requireContext(), "Picture Added to Gallery", Toast.LENGTH_SHORT).show()
}
     */
    override fun saveImageToGallery(
        imFileName: String,
        context: Context?
    ) {
        context?.let {
                val imPath = File(context?.filesDir, "images/")
                val imFile = File(imPath, imFileName)

          //  val newFile: File = File(MediaStore.Images.Media.EXTERNAL_CONTENT_URI., generatedImage.iconImageFileName)
         //  imFile.copyTo(newFile)

    //        Log.d("saveImageToGallery", "saved to " + newFile.absolutePath)

                var imageUri = FileProvider.getUriForFile(
                    it,
                    "com.drokka.emu.symicon",
                    imFile
                )



            // Add a media item that other apps shouldn't see until the item is
// fully written to the media store.

// Find all audio files on the primary external storage device.
            val imageCollection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL_PRIMARY
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                 val resolver = context.contentResolver

            val cvals = ContentValues().apply {
                put(DISPLAY_NAME, imFileName)
                put(MIME_TYPE, "image/png")
                put(RELATIVE_PATH, DIRECTORY_PICTURES)
                put(IS_PENDING, 1)
            }

                val uri = resolver.insert(imageCollection, cvals)    //(imageUri, cvals)
               uri?.let {
                   resolver.openOutputStream(uri).use { medStr ->
                       // Write data into the pending image file.

                       val imStr = FileInputStream(imFile)
                       medStr?.let { it1 ->
                           imStr.copyTo(it1)
                           imStr.close()
                           medStr.close()
                       }
                   }

                   cvals.clear()
                   cvals.put(IS_PENDING, 0)
                   resolver.update(uri, cvals, null, null)
               }

        }
    }

    override fun onFloatingActionButtonClicked() {
        Log.d("MainActivity", "onFloatingActionButtonClicked is CALLED" )
        if(mainFragment == null) {
            mainFragment = MainFragment.newInstance()
        }
        /*********
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, mainFragment!!)
            .commitNow()
**************************************/
        /***************
        var transaction:FragmentTransaction? = null
        supportFragmentManager.commit {
            setReorderingAllowed(true)

            transaction= add<BlankFragment>(R.id.fragmentContainerView,blankTag)
        }

          supportFragmentManager.findFragmentByTag(blankTag)?.let { transaction?.hide(it) }
        supportFragmentManager.executePendingTransactions()
        ***************************/

        navController?.navigate(R.id.action_wrapListFragment_to_mainFragment)
        Log.d("MainActivity", "after action_wrapListFragment_to_mainFragment call")
       // supportFragmentManager.findFragmentByTag(blankTag)?.let { transaction?.hide(it) }

       // viewModel.clearGeneratedData()

    }

    override fun viewBigsFloatingActionClicked() {

        if(bigImageViewPagerFragment == null) {
            bigImageViewPagerFragment = BigImageViewPagerFragment.newInstance()
        }
        if(viewModel.getSymBigsList().isEmpty()){
            Toast.makeText(applicationContext,"There are no saved big images to view.", Toast.LENGTH_SHORT).show()
        }else {
            navController?.navigate(R.id.action_wrapListFragment_to_bigImageViewPagerFragment)
        }
    }

    lateinit var   symImageListAllObserver:Observer<List<GeneratedIconWithAllImageData>>
//var keepSplashOnScreen = true
  //  val splashDelay = 500L

 override fun onCreate(savedInstanceState: Bundle?) {
    // setTheme(R.style.Theme_App_Starting)
     // Handle the splash screen transition.
  //   val splashScreen = installSplashScreen()
     super.onCreate(savedInstanceState)

     StrictMode.setVmPolicy(
         StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
             .detectLeakedClosableObjects()
             .build()
     )
        SymiRepo.initialize(applicationContext)
       // symiRepo = SymiRepo.get()
     //   symIconList = symiRepo.getAllSymIconData()  //symiRepo.getSymIconDataList(TINY)
       val TAG = "main activity onCreate"
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

  //      recyclerViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)
   //         .get(SymIconListViewModel::class.java)

  //   Handler(Looper.getMainLooper()).postDelayed({ keepSplashOnScreen = false }, splashDelay)
  //   splashScreen.setKeepOnScreenCondition{
   //      keepSplashOnScreen
   //  }
        setContentView(R.layout.main_activity)

     /***************
        listObserver = Observer<List<GeneratedIconAndImageData>>{ listy ->
            // Update the UI, in this case, a TextView.
            Log.i(TAG, "symilist size is: " + listy.size)
            Log.i("main activity listObserver", "liveList size is: " + viewModel.liveList?.size)

        }*****************************************/

        symImageListAllObserver = Observer<List<GeneratedIconWithAllImageData>>{
            list ->
            Log.i(TAG, "symImageListAllObserver size is: " + list.size)

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
         viewModel.symImageListAll.observe(this, symImageListAllObserver)


        //setContentView(R.layout.wrap_sym_icon_list)

  /****      if(symIconList.value.isNullOrEmpty()) {
            if (savedInstanceState == null) {
                mainFragment = MainFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, mainFragment!!)
                    .commitNow()
           }
        } else **********/    /* DB seems to have entries so show list */
      //  {
         //   if(savedInstanceState == null){
        if(symiListFragment == null) {
            symiListFragment = SymIconListFragment.newInstance(1)
        }
        if(wrapListFragment == null) {
            wrapListFragment = WrapListFragment.newInstance()
        }
     if(mainActivityFragment == null){
         mainActivityFragment = MainActivityFragment.newInstance("hell", "yeah")
     }

     setSupportActionBar(findViewById(R.id.my_toolbar))

     if(!viewModel.workItemsList.isEmpty()){
         val wm = WorkManager.getInstance(applicationContext)
         for(wi in viewModel.workItemsList){
             val workInfo = wm.getWorkInfoByIdLiveData(wi.key)
             workInfo.value?.let { checkWI(it, applicationContext, wi.key, wm) }
         }
     }

     setBigsIndicator(viewModel.workItemsList.isEmpty())

                /*******
                supportFragmentManager.beginTransaction().replace(id.container,wrapListFragment!!)
                 //   .addToBackStack("wrapListFragment")
                    .commitNow() *************/
             //   supportFragmentManager.beginTransaction().replace(id.container, symiListFragment!!)
               //     .commitNow()
      //     }
       // }
    }

    override fun onSymIconItemSelected(generatedImageAndImageData: GeneratedIconWithAllImageData) {
        Log.d("MainActivity ", "onSymIconItemSelected called")
       // if(mainFragment == null) {
         //   mainFragment = MainFragment.newInstance()
       // }
        if(imageIconFragment == null){
            imageIconFragment = ImageIconFragment.newInstance()
        }

       // findNavController(R.id.fragmentContainerView).navigate(R.id.action_wrapListFragment_to_mainFragment)
        navController?.navigate(R.id.action_wrapListFragment_to_imageIconFragment)
        // always reset symi data....
    //    if (generatedImageAndImageData != null) {
            viewModel.isLoadingFromData = true
            viewModel.setSymiData(applicationContext, generatedImageAndImageData)

        Log.d("MainActivity onSymIconItemSelected", "viewModel.bgClr[0] " + viewModel.bgClr[0])
            //   }

         /***********
        supportFragmentManager.beginTransaction().replace(id.container, mainFragment!!)
          //  .addToBackStack("mainFragment")
            .commit()
        *****************************/
    }

    override fun deleteSymIcon(context: Context, generatedIconWithAllImageData: GeneratedIconWithAllImageData) {
        Log.d("MainActivity", "deleteSymIcon called")
        viewModel.deleteSymiData(context , generatedIconWithAllImageData)
      //  wrapListFragment.
    }

    enum class CurrentFragmet(val fragment:String){
        WRAP_LIST("wrap_list"),MAIN_EDIT("main"), BLANK("blank")
            ,ICON_DISPLAY("icon")
    }
    var currentFragment:CurrentFragmet? = null
    //Callback for main activity fragment which is just splash, called by countdown timer that closes the page.
    override fun onCloseMe(b: Boolean) {
        if(b) SPLASH_DONE = false
        if (navController == null) {
            navController = findNavController(R.id.fragmentContainerView)
        }
         if((mainActivityFragment != null)
             && (navController?.currentDestination?.id == R.id.mainActivityFragment
                     && !SPLASH_DONE)
         ) {
             navController?.navigate( R.id.action_mainActivityFragment_to_wrapListFragment)
             SPLASH_DONE = true
         }
          //  navController?.popBackStack(R.id.mainActivityFragment, false)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when(val destId = (NavDestination as FragmentNavigator.Destination).id){
                R.id.mainActivityFragment -> currentFragment= CurrentFragmet.MAIN_EDIT
            R.id.wrapListFragment -> {
                currentFragment = CurrentFragmet.WRAP_LIST
              //  navController?.popBackStack(R.id.wrapListFragment, true)
            }
            R.id.image_icon_fragment -> currentFragment = CurrentFragmet.ICON_DISPLAY
            else -> Log.d("onDestinationCanged", "ID not handled id=$destId")
        }
    }

    override fun pickedColours(
        context: Context,
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray,
        clrFunction:String,
        clrFunExp:Double
    )  : Deferred<Unit?>  {


        val deferredJob =  viewModel.runReColour(context, bgClrArray, minClrArray,maxClrArray,  clrFunExp)

        return deferredJob
    }

    //OK to change colour
    override fun redisplayMedImage() {
        if(imageIconFragment == null){
            imageIconFragment = ImageIconFragment.newInstance()
        }

        if(navController?.currentDestination?.id  == R.id.pickColourFragment) {
            navController?.navigate(R.id.action_pickColourFragment_to_imageIconFragment)
           navController?.popBackStack(R.id.pickColourFragment,true)

        }
    }

    override fun doQuickReColour(
        context: Context,
        imageView: ImageView,
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray,
        clrFunction: String,
        clrFunExp: Double
    ) : Job  {
       return  viewModel.quickRecolour(context, imageView,  bgClrArray, minClrArray, maxClrArray, clrFunExp)
    }

    override fun cancelPickColours() {
        navController?.navigate(R.id.action_pickColourFragment_to_imageIconFragment)
        navController?.popBackStack(R.id.imageIconFragment,false)

    }

   /* override fun saveBigImageToGallery(bitmap: Bitmap?, context: Context) {

        val imageCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val resolver = context.contentResolver

        val cvals = ContentValues().apply {
            put(DISPLAY_NAME, "SymiconBig"+Date().time.toString())
            put(MIME_TYPE, "image/png")
            put(RELATIVE_PATH, DIRECTORY_PICTURES)
            put(IS_PENDING, 1)
        }

        val uri = resolver.insert(imageCollection, cvals)    //(imageUri, cvals)
        uri?.let {
            resolver.openOutputStream(uri).use { medStr ->
                // Write data into the pending image file.

               medStr?.let { it1 ->
                    bitmap.w .copyTo(it1)
                    imStr.close()
                    medStr.close()
                }
            }

            cvals.clear()
            cvals.put(IS_PENDING, 0)
            resolver.update(uri, cvals, null, null)
        }

    }
       bitmap?.let {
           MediaStore.Images.Media.insertImage(
               context.contentResolver,
               bitmap,
               "SymiconBig"+Date().time.toString(),
               "Symicon generated large image"
           )
         //  Toast.makeText(context, "Image Added to Gallery ", Toast.LENGTH_SHORT)
           //    .show()

       }
       }

    */


}