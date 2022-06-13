package com.drokka.emu.symicon.generateicon

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import java.io.File
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

    override fun onAttachedToWindow() {


        super.onAttachedToWindow()
        navController = findNavController(R.id.fragmentContainerView)
    }

        override fun onResume() {
        super.onResume()

        if(!viewModel.workItemsList.isEmpty()){
            val wm = WorkManager.getInstance(applicationContext)
            for(wi in viewModel.workItemsList){
               val workInfo = wm.getWorkInfoByIdLiveData(wi)
                workInfo.value?.let { checkWI(it,applicationContext,wi) }
            }
        }
    }
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

    override fun generateLargeIcon(context: Context)  {
        val id = viewModel.runSymiExampleWorker(context)
        if(id == null){
            Log.e("Go Big", "Error getting work id.")

        }else {
            setBigsIndicator(false)
            viewModel.workItemsList.add(id)
            context.let { it1 ->
                WorkManager.getInstance(it1).getWorkInfoByIdLiveData(id)
                    .observe(this) { workInfo ->
                        checkWI(workInfo, it1, id)
                    }
            }
        }
    }

    private fun checkWI(
        workInfo: WorkInfo,
        it1: Context,
        id: UUID
    ) {

        if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
            Toast.makeText(it1, "Go Big completed.", Toast.LENGTH_SHORT)
                .show()
            Log.i("Go Big checkWI", "success for work: " +id +" " + workInfo.toString())
            viewModel.workItemsList.remove(id)

            // viewModel.storeWork(requireContext(),workInfo.outputData)
        } else if (workInfo?.state == WorkInfo.State.ENQUEUED) {
            Toast.makeText(it1, "Go Big  task queued."  , Toast.LENGTH_SHORT)
                .show()
            Log.d("Go Big checkWI", "queued")
        } else if (workInfo?.state == WorkInfo.State.RUNNING) {
            Toast.makeText(it1, "Go Big task running." , Toast.LENGTH_SHORT)
                .show()
            Log.d("Go Big checkWI", "running" +id +" " )
        } else if(workInfo.state.isFinished) {
            Toast.makeText(
                it1, "Go Big generation did not complete.",
                Toast.LENGTH_SHORT
            )
                .show()
            Log.e("Go Big checkWI", "Error generating large image. workinfo state: " +id +" "  + workInfo?.toString())
            viewModel.workItemsList.remove(id)

        } else {
            Log.e("Go Big checkWI", "Fail? fall through generating large image. workinfo: " +id +" "  + workInfo?.toString())

        }
        setBigsIndicator(viewModel.workItemsList.isEmpty())
    }

    private fun setBigsIndicator(workDone: Boolean) {
        val bigsWorkIndicator = findViewById<ImageButton>(R.id.bigsWorkImageButton)
        if(workDone) {
            bigsWorkIndicator.setImageResource(R.drawable.ic_baseline_circle_24green)
        }else{
            bigsWorkIndicator.setImageResource(R.drawable.ic_baseline_circle_24)

        }
        bigsWorkIndicator.visibility = View.VISIBLE

        bigsWorkIndicator.isDirty
      //  toolbar?.isDirty
    }

    override fun showBigImage() {
        if(bigImageFragment == null){
            bigImageFragment = BigImageFragment.newInstance(null)
        }
      //  if(viewModel.imageExists(LARGE)){
        //Save the big image
            viewModel.saveSymi()
        //Now show it.
        //    viewModel.getIconBitmap(applicationContext, viewModel.genIAD?.generatedImageData!!.gid_id)?.let {

         //     val  bitMap = BitmapFactory.decodeByteArray(it, 0, viewModel.genIAD?.generatedImageData!!.len)
               // bigImageFragment?.bigImageView?.setImageBitmap(it)  // bigImageView null, OnCreateView not finished?
                bigImageFragment?.view?.findViewById<ImageView>(R.id.bigImageView)?.setImageBitmap(viewModel.largeIm)
                bigImageFragment?.view?.invalidate()
                if(navController?.currentDestination?.id == R.id.imageIconFragment) {
                    navController?.navigate(R.id.action_imageIconFragment_to_bigImageFragment)
                          }
       // }
    }

    override fun reColour(){  //Call from reColour button to open colours dialog
        if (pickColourFragment == null) {
            pickColourFragment = PickColourFragment.newInstance()
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
        bitmapIn: Bitmap?,
        generatedImage: GeneratedImage,
        context: Context?
    ) {
        context?.let {
                val imPath = File(context?.filesDir, "images/")
                val imFile = File(imPath, generatedImage.iconImageFileName)
                val imageUri = FileProvider.getUriForFile(
                    it,
                    "com.drokka.emu.symicon",
                    imFile
                )


                val resolver = context?.contentResolver
                val bitmap = ImageDecoder.createSource(resolver, imageUri)

                /*
            val cvals = ContentValues().apply {
                put(DISPLAY_NAME, generatedImage.iconImageFileName)
                put(MIME_TYPE, "image/png")
                put(RELATIVE_PATH, DIRECTORY_DCIM)
                put(IS_PENDING, 1)
            }
            resolver.insert(imageUri,cvals)

             */


                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    ImageDecoder.decodeBitmap(bitmap),
                    generatedImage.iconImageFileName,
                    "Image of " + generatedImage.iconImageFileName
                )

          //  Toast.makeText(context, "Picture Added to Gallery ", Toast.LENGTH_SHORT).show()
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
          //  Toast.makeText(applicationContext,"There are no saved big images to view!", Toast.LENGTH_SHORT).show()
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
             val workInfo = wm.getWorkInfoByIdLiveData(wi)
             workInfo.value?.let { checkWI(it,applicationContext,wi) }
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
        Log.i("MainActivity ", "onSymIconItemSelected called")
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
    override fun onCloseMe() {

         if(mainActivityFragment!=null && navController?.currentDestination?.id == R.id.mainActivityFragment) {
             navController?.navigate(/*R.id.action_mainActivityFragment_to_editSymiFragment*/  R.id.action_mainActivityFragment_to_wrapListFragment)
         }
         //   navController?.popBackStack(R.id.mainActivityFragment, true)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when(val destId = (NavDestination as FragmentNavigator.Destination).id){
                R.id.mainActivityFragment -> currentFragment= CurrentFragmet.MAIN_EDIT
            R.id.wrapListFragment -> currentFragment = CurrentFragmet.WRAP_LIST
            R.id.image_icon_fragment -> currentFragment = CurrentFragmet.ICON_DISPLAY
            else -> Log.d("onDestinationCanged", "ID not handled id=$destId")
        }
    }

    override fun pickedColours(
        context: Context,
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray
    ): Deferred<Unit?> {


        val deferredJob =  viewModel.runReColour(context, bgClrArray, minClrArray,maxClrArray)

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

    override fun doQuickReColour( context: Context,
        imageView: ImageView,
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray
    ): Job {
        return viewModel.quickRecolour(context, imageView,  bgClrArray, minClrArray, maxClrArray)
    }

    override fun cancelPickColours() {
        navController?.navigate(R.id.action_pickColourFragment_to_imageIconFragment)
        navController?.popBackStack(R.id.imageIconFragment,false)

    }

    override fun saveBigImageToGallery(bitmap: Bitmap?, context: Context) {
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


}