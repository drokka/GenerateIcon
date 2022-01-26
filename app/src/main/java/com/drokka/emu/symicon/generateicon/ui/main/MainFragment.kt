package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.drokka.emu.symicon.generateicon.R
import com.drokka.emu.symicon.generateicon.data.*
import com.drokka.emu.symicon.generateicon.getBitmap
import com.drokka.emu.widgets.FloatInView
import kotlinx.coroutines.*

class MainFragment() : Fragment(),  AdapterView.OnItemSelectedListener /*icon type spinner*/ {

     //  var generatedIconAndImageData:GeneratedIconAndImageData? = null

    companion object {

        fun newInstance() = MainFragment().also{
         //   it.generatedIconAndImageData = generatedIconAndImageDataVal
        }
    }
    private val viewModel:MainViewModel by activityViewModels()

  //  private lateinit var greyOverlay:View
    private lateinit var imageButton: ImageView
 //   private lateinit var selectSquareButton:RadioButton
 //   private lateinit var selectHexButton:RadioButton
  //  private lateinit var selectFractalButton:RadioButton
    private lateinit var selectTypeSpinner:Spinner

    private lateinit var iterTextView: EditText

    private lateinit var sizeText: EditText

    private lateinit var lambdaText: FloatInView
    private lateinit var alphaText: FloatInView
    private lateinit var betaText: FloatInView
    private lateinit var gammaText: FloatInView
    private lateinit var omegaText: FloatInView
    private lateinit var maText: FloatInView

    private lateinit var quickDrawImageButton: ImageButton
    private lateinit var busyBar: ProgressBar

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
        imageButton = view.findViewById(R.id.imageButton)
     /**   selectFractalButton = view.findViewById(R.id.selectFractal)
        selectHexButton = view.findViewById(R.id.selectHex)
        selectSquareButton = view.findViewById(R.id.selectSquare)  **********/
        selectTypeSpinner = view.findViewById(R.id.iconTypeSpinner)
        iterTextView = view.findViewById(R.id.editNumberIters)

        sizeText = view.findViewById(R.id.editSize)
        lambdaText = view.findViewById(R.id.editTextLamda)
        alphaText = view.findViewById(R.id.editTextAlpha)
        betaText = view.findViewById(R.id.editTextBeta)
        gammaText = view.findViewById(R.id.editTextGamma)
        omegaText = view.findViewById(R.id.editTextOmega)
        maText = view.findViewById(R.id.editTextMa)

        quickDrawImageButton = view.findViewById(R.id.imageButtonQuickDraw)
        busyBar = view.findViewById(R.id.progressBar)
      //  greyOverlay = view.findViewById(R.id.waitOverlay)
       // greyOverlay.alpha = 0.05f
      //  greyOverlay.visibility = View.VISIBLE
        val types = resources.getStringArray(R.array.IconTypes)
        val arrayAdapter:ArrayAdapter<CharSequence> =
            context?.let {
                ArrayAdapter.createFromResource(
                    it,R.array.IconTypes,android.R.layout.simple_spinner_item)
            } as ArrayAdapter<CharSequence>;

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        selectTypeSpinner.setAdapter(arrayAdapter);

        //restoreState(savedInstanceState)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        busyBar.isIndeterminate

        // greyOverlay.alpha = 0f
        // GENERATE button - make a bigger icon with more iterations
        imageButton.setOnClickListener {
            busyBar.visibility = View.VISIBLE
            view.refreshDrawableState()
        //    it.parent.refreshDrawableState()
          //  context?.let { it1 ->
               // busyBar.isShown = View.

                    val job = callbacks?.onGenerateClicked(requireContext())


       // }

            // can't sleep prevents UI thread showing busy.
            // FIX this generated image will be null now
            job?.invokeOnCompletion {
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


            iterTextView.doAfterTextChanged{  viewModel.setNumIterations(iterTextView.text)          }

        sizeText.doAfterTextChanged{  viewModel.setSize(sizeText.text)}
        lambdaText.onSelectedValueChanged = {oldie,newie ->   viewModel.setLambda(newie)
        doQuickDraw()}

        alphaText.onSelectedValueChanged = {oldie,newie -> viewModel.setAlpha(newie);doQuickDraw()}
        betaText.onSelectedValueChanged = {oldie,newie -> viewModel.setBeta(newie);doQuickDraw()}
        gammaText.onSelectedValueChanged = {oldie,newie -> viewModel.setGamma(newie);doQuickDraw()}
        omegaText.onSelectedValueChanged = {oldie,newie -> viewModel.setOmega(newie);doQuickDraw()}
        maText.onSelectedValueChanged = {oldie,newie -> viewModel.setMa(newie);doQuickDraw()}

      //  retainInstance = true
      //  restoreState(savedInstanceState)
        //imageButton.setImageDrawable(drawable)
    }


    private fun doQuickDraw() {
        if(viewModel.isLoading) return
        var generateJob:Job? = null
        context?.let { it1 -> generateJob = callbacks!!.doQuickDraw(it1) }
        generateJob?.invokeOnCompletion {
            quickDrawImageButton.setImageBitmap(context?.let { it1 ->
                viewModel.tinyIm
            }) }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.setIconType(parent?.getItemAtPosition(position).toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
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
        omegaText.selectedValue =
            viewModel.iconDef.omega
        alphaText.selectedValue =
            viewModel.iconDef.alpha
        betaText.selectedValue =
            viewModel.iconDef.beta
        gammaText.selectedValue =
            viewModel.iconDef.gamma
        lambdaText.selectedValue =
            viewModel.iconDef.lambda
        maText.selectedValue =
            viewModel.iconDef.ma
        val iconLabel = viewModel.iconDef.quiltType

        when (iconLabel) {
            QuiltType.SQUARE -> selectTypeSpinner.setSelection(0)
            QuiltType.HEX -> selectTypeSpinner.setSelection(1)
            QuiltType.FRACTAL -> selectTypeSpinner.setSelection(2)
            else -> selectTypeSpinner.setSelection(0)
        }
    }

    fun setBusy(boolean: Boolean){
        when(boolean){
            true -> busyBar.visibility = View.VISIBLE
            false -> busyBar.visibility = View.INVISIBLE
        }
    }
fun restoreState(savedInstanceState: Bundle?) {
    if (savedInstanceState == null) {
        setWidgetValues()
    } else {

        var savedVal = savedInstanceState?.getDouble("alpha")?.toFloat()
        alphaText.selectedValue = savedVal.toDouble()

        savedVal = savedInstanceState?.getDouble("omega")?.toFloat()
        omegaText.selectedValue = savedVal.toDouble()

        savedVal = savedInstanceState?.getDouble("gamma")?.toFloat()
        gammaText.selectedValue = savedVal.toDouble()
        savedVal = savedInstanceState?.getDouble("lambda")?.toFloat()
        lambdaText.selectedValue = savedVal.toDouble()
        savedVal = savedInstanceState?.getDouble("ma")?.toFloat()
        maText.selectedValue = savedVal.toDouble()
        savedVal = savedInstanceState?.getDouble("beta")?.toFloat()
        betaText.selectedValue = savedVal.toDouble()
    }
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