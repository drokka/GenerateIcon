package com.drokka.emu.symicon.generateicon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.ui.main.*
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener, MainFragment.Callbacks , ImageIconFragment.Callbacks,
    SymIconListFragment.Callbacks, WrapListFragment.Callbacks,
    MainActivityFragment.Callbacks/*, EditSymiFragment.Callbacks*/{

    companion object  {

        /* this is used to load the  library on application
         * startup.
         */
        init {
            System.loadLibrary("emutil")
        }

    }
    var mainActivityFragment:MainActivityFragment? = null
    var mainFragment: MainFragment? = null
    var symiListFragment: SymIconListFragment? = null
    var wrapListFragment:WrapListFragment? = null
    var imageIconFragment:ImageIconFragment? = null
  //  val blankTag = "blankFragment"
    lateinit var viewModel: MainViewModel
 //   lateinit var recyclerViewModel: SymIconListViewModel

    /** data access using Room **/

   // private lateinit var  symiRepo:SymiRepo
 //   private lateinit var symIconList :LiveData<List<GeneratedIconAndImageData>>
    /*********************/

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
        val navvy = findNavController(R.id.fragmentContainerView)
        if(navvy.currentDestination?.id == R.id.mainFragment) {
            navvy.navigate(R.id.action_mainFragment_to_imageIconFragment)
        }
    }

    /**********************************************************************
     * GENERATE
     */
    //MainFragment GENERATE. MainFragment is not the MainActivityFragment which is a navigation container
    @OptIn(ExperimentalCoroutinesApi::class)
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

                deferredJob.invokeOnCompletion { viewModel.saveTinySymi() }
            }else{
                viewModel.saveTinySymi()
            }

            deferredJob =     viewModel.runSymiExample(context, MEDIUM, GO_GO)
        return deferredJob
    }

    override fun doQuickDraw(context: Context): Job? {
        var  generateJob =  viewModel.runSymiExample(context, TINY, QUICK_LOOK)

        return generateJob
    }

    override fun onViewImageButtonSelected(generatedImage: GeneratedImage, context:Context) {
        val imPath = File(context.filesDir ,"images/")
        val imFile = File(imPath, generatedImage.iconImageFileName)
       val imageUri = FileProvider.getUriForFile(context,
            "com.drokka.emu.symicon",
           imFile)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = imageUri   //context.getFileStreamPath(generatedImage.iconImageFileName).toUri()
        }
        val activityComponent = intent.resolveActivity(packageManager)
        if ( activityComponent != null) {
            grantUriPermission(activityComponent.packageName,intent.data,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
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

        findNavController(R.id.fragmentContainerView).navigate(R.id.action_wrapListFragment_to_mainFragment)
        Log.d("MainActivity", "after action_wrapListFragment_to_mainFragment call")
       // supportFragmentManager.findFragmentByTag(blankTag)?.let { transaction?.hide(it) }

       // viewModel.clearGeneratedData()

    }

    //ImageIconFragment callback
    override fun onSaveImageDataButtonSelected(button: Button) {
        button.isEnabled = false
        //  it.setBackgroundColor()
                 viewModel.saveSymi()
         button.isEnabled = true
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
  /***** NO need already added to back stack      if(mainFragment != null) {
            supportFragmentManager.putFragment(outState, "mainFragment", mainFragment!!)
        }
        if(wrapListFragment != null){
            supportFragmentManager.putFragment(outState, "wrapListFragment", wrapListFragment!!)
        }

        if(imageIconFragment != null){
            supportFragmentManager.putFragment(outState, "imageIconFragment",imageIconFragment!!)
        }
        if(symiListFragment!= null){
            supportFragmentManager.putFragment(outState, "symiListFragment", symiListFragment!!)
        }
  ***/
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
 /**       if(symIconList.value.isNullOrEmpty()) {
            val fragment = supportFragmentManager.getFragment(savedInstanceState, "mainFragment")
            if (fragment != null) {
                mainFragment = fragment as MainFragment
            }
        }else{ *************/

 /***
            val fragment = supportFragmentManager.getFragment(savedInstanceState, "wrapListFragment")
            if(fragment != null){
                wrapListFragment = fragment as WrapListFragment
            }********************/
     //   }
    }
//lateinit var listObserver:Observer<List<GeneratedIconAndImageData>>
 lateinit var   symImageListAllObserver:Observer<List<GeneratedIconWithAllImageData>>

 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SymiRepo.initialize(applicationContext)
       // symiRepo = SymiRepo.get()
     //   symIconList = symiRepo.getAllSymIconData()  //symiRepo.getSymIconDataList(TINY)
       val TAG = "main activity onCreate"
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

  //      recyclerViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)
   //         .get(SymIconListViewModel::class.java)

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
        findNavController(R.id.fragmentContainerView).navigate(R.id.action_wrapListFragment_to_imageIconFragment)
        if (generatedImageAndImageData != null) {
            viewModel.isLoadingFromData = true
            viewModel.setSymiData(generatedImageAndImageData)
        }

         /***********
        supportFragmentManager.beginTransaction().replace(id.container, mainFragment!!)
          //  .addToBackStack("mainFragment")
            .commit()
        *****************************/
    }

    enum class CurrentFragmet(val fragment:String){
        WRAP_LIST("wrap_list"),MAIN_EDIT("main"), BLANK("blank")
            ,ICON_DISPLAY("icon")
    }
    var currentFragment:CurrentFragmet? = null
    override fun onCloseMe() {
        findNavController(R.id.fragmentContainerView)
            .navigate(/*R.id.action_mainActivityFragment_to_editSymiFragment*/  R.id.action_mainActivityFragment_to_wrapListFragment)
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


}