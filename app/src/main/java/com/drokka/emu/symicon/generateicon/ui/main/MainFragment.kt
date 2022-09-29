package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.storage.StorageManager
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.drokka.emu.symicon.generateicon.R
import com.drokka.emu.symicon.generateicon.data.*
import com.google.android.material.slider.Slider
import kotlinx.coroutines.*
import java.lang.Math.random
import java.util.*

class MainFragment() : Fragment(),  AdapterView.OnItemSelectedListener /*icon type spinner*/ {

     //  var generatedIconAndImageData:GeneratedIconAndImageData? = null

    companion object {

        fun newInstance() = MainFragment().also{
         //   it.generatedIconAndImageData = generatedIconAndImageDataVal
        }
    }
    private val viewModel:MainViewModel by activityViewModels()

  //  private lateinit var greyOverlay:View
 //   private lateinit var imageButton: ImageView
 //   private lateinit var selectSquareButton:RadioButton
 //   private lateinit var selectHexButton:RadioButton
  //  private lateinit var selectFractalButton:RadioButton
    private lateinit var selectTypeSpinner:Spinner

  //  private lateinit var iterTextView: EditText

  //  private lateinit var sizeText: EditText

    private lateinit var lambdaText:Slider // FloatInView
    private lateinit var alphaText:Slider // FloatInView
    private lateinit var betaText:Slider // FloatInView
    private lateinit var gammaText:Slider // FloatInView
    private lateinit var omegaText:Slider // FloatInView
    private lateinit var maText: Slider //SeekBar //FloatInView
    private lateinit var degSymText: EditText

    private lateinit var quickDrawImageButton: ImageButton
    private lateinit var busyBar: ProgressBar
    private  lateinit var labelTextView: TextView

    private lateinit var textViewDegreeSymLabel: TextView
    /*** callback interface for fragment management by the hosting activity *********/
    interface Callbacks {

        fun onImageIconSelected()
        fun onGenerateClicked(context: Context): Job?  //Hopefully to run in viewmodel scope lifecycle
        fun doQuickDraw(context: Context):Job?
    }

    private var callbacks:Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    fun displayImageIcon(){
        callbacks?.onImageIconSelected()
    }

    /*******************************************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.main_fragment, container, false)
       // imageButton = view.findViewById(R.id.imageButton)
     /**   selectFractalButton = view.findViewById(R.id.selectFractal)
        selectHexButton = view.findViewById(R.id.selectHex)
        selectSquareButton = view.findViewById(R.id.selectSquare)  **********/
        selectTypeSpinner = view.findViewById(R.id.iconTypeSpinner)
      //  iterTextView = view.findViewById(R.id.editNumberIters)

       // sizeText = view.findViewById(R.id.editSize)
        lambdaText = view.findViewById(R.id.editTextLamda)
        alphaText = view.findViewById(R.id.editTextAlpha)
        betaText = view.findViewById(R.id.editTextBeta)
        gammaText = view.findViewById(R.id.editTextGamma)
        omegaText = view.findViewById(R.id.editTextOmega)
        maText = view.findViewById(R.id.editTextMa)

        degSymText = view.findViewById(R.id.editTextDegSym)
        quickDrawImageButton = view.findViewById(R.id.imageButtonQuickDraw)
        busyBar = view.findViewById(R.id.progressBar)

        labelTextView = view.findViewById(R.id.labelTextView)
      //  greyOverlay = view.findViewById(R.id.waitOverlay)
       // greyOverlay.alpha = 0.05f
      //  greyOverlay.visibility = View.VISIBLE
      //  val types = resources.getStringArray(R.array.IconTypes)
        val arrayAdapter:ArrayAdapter<CharSequence> =
            context?.let {
                ArrayAdapter.createFromResource(
                    it,R.array.IconTypes,R.layout.custom_spinner)
            } as ArrayAdapter<CharSequence>

        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_item ) //android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        selectTypeSpinner.adapter = arrayAdapter

        //restoreState(savedInstanceState)
        textViewDegreeSymLabel = view.findViewById(R.id.textViewDegreeSymLabel)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        busyBar.isIndeterminate

        // greyOverlay.alpha = 0f
        // GENERATE button - make a bigger icon with more iterations
        quickDrawImageButton.setOnClickListener {
            if (viewModel.tinyIm == null) return@setOnClickListener

            busyBar.visibility = View.VISIBLE
            view.refreshDrawableState()
            //    it.parent.refreshDrawableState()
            //  context?.let { it1 ->
            // busyBar.isShown = View.


                val job = callbacks?.onGenerateClicked(requireContext())


                // FIX this generated image will be null now
                job?.invokeOnCompletion {
                    viewModel.saveSymi()
                    displayImageIcon()
                }

                //   busyBar.visibility = View.INVISIBLE

            }

        /*********** needed? *****************
        quickDrawImageButton.setOnClickListener{ context?.let { it1 ->
            run {
                viewModel.runSymiExample(it1, SMALL, GO)
              //  viewModel.runSymiExample(it1, imageButton, SMALL, QUICK_LOOK)
                this.displayImageIcon(viewModel.generatedImage)
            }
        } *************************/

       selectTypeSpinner.setOnItemSelectedListener(this)
      //  selectTypeSpinner.selectedView.get


         //   iterTextView.doAfterTextChanged{  viewModel.setNumIterations(iterTextView.text)          }

       // sizeText.doAfterTextChanged{  viewModel.setSize(sizeText.text)}
  /*      lambdaText.onSelectedValueChanged = {oldie,newie ->   viewModel.setLambda(newie)
        doQuickDraw()}

        alphaText.onSelectedValueChanged = {oldie,newie -> viewModel.setAlpha(newie);doQuickDraw()}
        betaText.onSelectedValueChanged = {oldie,newie -> viewModel.setBeta(newie);doQuickDraw()}
        gammaText.onSelectedValueChanged = {oldie,newie -> viewModel.setGamma(newie);doQuickDraw()}
        omegaText.onSelectedValueChanged = {oldie,newie -> viewModel.setOmega(newie);doQuickDraw()}

  */
     //   maText.onSelectedValueChanged = {oldie,newie -> viewModel.setMa(newie);doQuickDraw()}
        maText.addOnChangeListener { slider, value, fromUser ->
            viewModel.setMa(jitter(value.toDouble()))
            doQuickDraw()
        }
        lambdaText.addOnChangeListener { slider, value, fromUser ->
            viewModel.setLambda(jitter(value.toDouble()))
            doQuickDraw()
        }
        alphaText.addOnChangeListener { slider, value, fromUser ->
            viewModel.setAlpha(jitter(value.toDouble()))
            doQuickDraw()
        }
        betaText.addOnChangeListener { slider, value, fromUser ->
            viewModel.setBeta(jitter(value.toDouble()))
            doQuickDraw()
        }
        gammaText.addOnChangeListener { slider, value, fromUser ->
            viewModel.setGamma(jitter(value.toDouble()))
            doQuickDraw()
        }
        omegaText.addOnChangeListener { slider, value, fromUser ->
            viewModel.setOmega(jitter(value.toDouble()))
            doQuickDraw()
        }

        /*
        maText.onChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    viewModel.setMa(i.toDouble()/maText.max)
                    doQuickDraw()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

         */


        degSymText.doAfterTextChanged { viewModel.setDegSym(degSymText.text);doQuickDraw()}

        labelTextView.doAfterTextChanged { viewModel.symIcon.label = labelTextView.text.toString() }

        //  retainInstance = true
      //  restoreState(savedInstanceState)
        //imageButton.setImageDrawable(drawable)

        checkStorage()
    }

    private fun jitter(x:Double):Double{
        val xj = Random().nextDouble()*(1.0 - x)*0.0001 + x
        return xj
    }

    private fun doQuickDraw() {
        if(viewModel.isLoading) return
        var generateJob:Job? = null
        context?.let { it1 -> generateJob = callbacks!!.doQuickDraw(it1) }
        generateJob?.invokeOnCompletion {
            if(viewModel.tinyIm != null) {
                quickDrawImageButton.setImageBitmap(context?.let { it1 ->
                    viewModel.tinyIm
                })
            }else {
                quickDrawImageButton.setImageResource(R.drawable.range_error3)
                }
        }
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val itemType = parent?.getItemAtPosition(position).toString()
        when (itemType){
            "Fractal" -> {
                degSymText.visibility = View.VISIBLE
                textViewDegreeSymLabel.visibility = View.VISIBLE
                alphaText.value = kotlin.random.Random.nextDouble(-1.0, 1.0).toFloat()
                betaText.value = kotlin.random.Random.nextDouble(-1.0, 1.0).toFloat()
                gammaText.value = kotlin.random.Random.nextDouble(-1.0, 1.0).toFloat()
                alphaText.valueFrom = -1.0f
                alphaText.valueTo = 1.0f
                betaText.valueFrom = -1.0f
                betaText.valueTo = 1.0f
                gammaText.valueFrom = -1.0f
                gammaText.valueTo = 1.0f

            }
            else -> {
                degSymText.visibility = View.GONE
                textViewDegreeSymLabel.visibility = View.GONE

                alphaText.valueFrom = -20.0f
                alphaText.valueTo = 20.0f
                betaText.valueFrom = -20.0f
                betaText.valueTo = 20.0f
                gammaText.valueFrom = -20.0f
                gammaText.valueTo = 20.0f

            }
        }
        viewModel.setIconType(itemType)
        doQuickDraw()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
       // TODO("Not yet implemented")
    }

    /******
    override fun onPause() {
        super.onPause()
        var myBundle = this.arguments
        if(myBundle == null) {
            myBundle = Bundle()
        }
        saveMe(myBundle)
        this.arguments = myBundle
    }
    ***************************/

    private fun saveMe(outState: Bundle) {
        outState.putString("IconType", viewModel.iconDef.quiltType.label)
        outState.putDouble("omega", viewModel.iconDef.omega)
        outState.putDouble("alpha", viewModel.iconDef.alpha)
        outState.putDouble("beta", viewModel.iconDef.beta)
        outState.putDouble("gamma", viewModel.iconDef.gamma)
        outState.putDouble("lambda", viewModel.iconDef.lambda)
        outState.putDouble("ma", viewModel.iconDef.ma)
        outState.putInt("degreeSym", viewModel.iconDef.degreeSym)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val bundle = outState
        saveMe(bundle)
        super.onSaveInstanceState(outState)

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {

        restoreState(savedInstanceState)
        super.onViewStateRestored(savedInstanceState)

    }

    fun setWidgetValues() {
        // use the view model
        omegaText.value =
            viewModel.iconDef.omega.toFloat()
        alphaText.value =
            viewModel.iconDef.alpha.toFloat()
        betaText.value =
            viewModel.iconDef.beta.toFloat()
        gammaText.value =
            viewModel.iconDef.gamma.toFloat()
        lambdaText.value =
            viewModel.iconDef.lambda.toFloat()
        maText.value = viewModel.iconDef.ma.toFloat()
      //  maText.setProgress((viewModel.iconDef.ma * maText.max).toInt())

        val iconLabel = viewModel.iconDef.quiltType

        when (iconLabel) {
            QuiltType.SQUARE -> selectTypeSpinner.setSelection(0)
            QuiltType.HEX -> selectTypeSpinner.setSelection(1)
            QuiltType.FRACTAL -> selectTypeSpinner.setSelection(2)
            else -> selectTypeSpinner.setSelection(0)
        }
        degSymText.setText( ""+viewModel.iconDef.degreeSym)

    }

    fun setBusy(boolean: Boolean){
        when(boolean){
            true -> busyBar.visibility = View.VISIBLE
            false -> busyBar.visibility = View.INVISIBLE
        }
    }
fun restoreState(savedInstanceState: Bundle?) {
    if (savedInstanceState == null) {
        viewModel.isLoading = true
        setWidgetValues()
        viewModel.isLoading = false
    } else {

        var savedVal = savedInstanceState?.getDouble("alpha")?.toFloat()
        alphaText.value = savedVal

        savedVal = savedInstanceState?.getDouble("omega")?.toFloat()
        omegaText.value = savedVal

        savedVal = savedInstanceState?.getDouble("gamma")?.toFloat()
        gammaText.value = savedVal
        savedVal = savedInstanceState?.getDouble("lambda")?.toFloat()
        lambdaText.value = savedVal
        savedVal = savedInstanceState?.getDouble("ma")?.toFloat()
        maText.value = savedVal
      //  maText.setProgress((savedVal * maText.max).toInt())
        savedVal = savedInstanceState?.getDouble("beta")?.toFloat()
        betaText.value = savedVal
        var savedIntVal = savedInstanceState.getInt("degreeSym",3)
        degSymText.setText(savedIntVal)

    }
}

    // App needs 10 MB within internal storage.
    fun checkStorage() {
        if(viewModel.storageCheckDone) return
        val NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 30L;

        context?.let {
            val storageManager = it.getSystemService<StorageManager>()!!
            val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(it.filesDir)
            val availableBytes: Long =
                storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
            if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
                storageManager.allocateBytes(
                    appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP
                )
            } else {

                val builder = AlertDialog.Builder(it)
                builder.setMessage("Run storage manager?")
                    .setTitle("App needs more data storage")
                builder.setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { dialog, which ->
                        Log.d("storage", "yes storage manager")
                        val storageIntent = Intent().apply {
                            // To request that the user remove all app cache files instead, set
                            // "action" to ACTION_CLEAR_APP_CACHE.
                            action = ACTION_MANAGE_STORAGE
                        }.also {
                            startActivity(it)
                        }
                 })
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                        Log.d("storage", "no do not run storage manager")
                        // do nothing
                    })
                builder.create()
                builder.show()


            }
        }
        viewModel.storageCheckDone = true
    }

}




/**************
lambdaText.addTextChangedListener(object :TextWatcher{
override fun afterTextChanged(s: Editable?) {
viewModel.setLambda(lambdaText.text)
}

override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
TODO("Not yet implemented")
}

override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
TODO("Not yet implemented")
}
})
*************************************/