package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.graphics.Color
import android.graphics.Color.argb
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.drokka.emu.symicon.generateicon.R
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlin.math.absoluteValue


class PickColourFragment : Fragment() {

    companion object {
        fun newInstance(bgClrArray: IntArray, minClrArray: IntArray, maxClrArray: IntArray) = PickColourFragment().apply {
            this.bgClrArray = bgClrArray.clone()
            this.minClrArray = minClrArray.clone()
            this.maxClrArray = maxClrArray.clone()

            rgbValueInt = bgClrArray.clone()
        }
    }
interface Callbacks{
    fun pickedColours(
        context: Context,
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray,
        clrFunction: String,
        clrFunExp: Double
    ) : Deferred<Unit?>
 //   fun cancelPickColours()
    fun redisplayMedImage()
    fun doQuickReColour(
        context: Context,
        imageView: ImageView,
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray,
        clrFunction: String,
        clrFunExp:Double
    ) : Job //, bgClrIntArray: IntArray, minClrIntArray: IntArray, maxClrIntArray: IntArray ): Job
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

private val viewModel:MainViewModel by activityViewModels()
private lateinit var redSeekBar: SeekBar
private lateinit var greenSeekBar: SeekBar
private lateinit var blueSeekBar: SeekBar
    private lateinit var alphaSeekBar: SeekBar
private lateinit var colourDisplay: View
private lateinit var bgColourView: View
private lateinit var minColourView: View
private lateinit var maxColourView: View
private lateinit var okButton: Button
//private lateinit var cancelButton: Button
private lateinit var imageView: ImageView

private lateinit var textView3: TextView
    private lateinit var textView4:TextView
    private lateinit var textView5:TextView

    private lateinit var clrFunEditText:EditText


    private var bgClrArray = IntArray(4)
    private var minClrArray = IntArray(4)
    private var maxClrArray = IntArray(4)
    private var rgbValueInt = IntArray(4)

    private var clrFun = 1.0
    private lateinit var colourRecyclerView:RecyclerView

    enum class Mixin {BG_CLR, MIN_CLR, MAX_CLR}
    private var mixin = Mixin.BG_CLR
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view = inflater.inflate(R.layout.fragment_pick_colour, container, false)
    redSeekBar = view.findViewById(R.id.redSeekBar)
    blueSeekBar = view.findViewById(R.id.blueSeekBar)
    greenSeekBar = view.findViewById(R.id.greenSeekBar)
    alphaSeekBar = view.findViewById(R.id.alphaSeekBar)
    colourDisplay = view.findViewById(R.id.colourDisplayView)
    bgColourView = view.findViewById(R.id.viewBgClr)
    minColourView = view.findViewById(R.id.viewMinClr)
    maxColourView = view.findViewById(R.id.viewMaxClr)
    okButton = view.findViewById(R.id.okClrButton)
  //  cancelButton = view.findViewById(R.id.cancelClrButton)
    imageView = view.findViewById(R.id.pickClrImageView)

    textView3 = view.findViewById(R.id.textView3)
    textView4 = view.findViewById(R.id.textView4)
    textView5 = view.findViewById(R.id.textView5)
    colourRecyclerView = view.findViewById(R.id.idColourRecyclerView)
    clrFunEditText = view.findViewById(R.id.editTextNumberClrFunction)
    return view
}
private fun setClr(clrArray: IntArray){
    redSeekBar.progress = clrArray[0]
    blueSeekBar.progress = clrArray[2]
    greenSeekBar.progress = clrArray[1]
    alphaSeekBar.progress = clrArray[3]
    rgbValueInt[0] = clrArray[0]
    rgbValueInt[1] = clrArray[1]
    rgbValueInt[2] = clrArray[2]
    rgbValueInt[3] = clrArray[3]

}

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
      Log.d("pick colour fragment onViewCreated", "viewModel.bgClrInt[0] " + viewModel.bgClrInt[0])
    bgClrArray = viewModel.bgClrInt.clone()
    minClrArray = viewModel.minClrInt.clone()
    maxClrArray = viewModel.maxClrInt.clone()
    rgbValueInt = viewModel.bgClrInt.clone()
    clrFun = viewModel.clrFunctionExp
    setClr(bgClrArray)

    setMixin( Mixin.BG_CLR)


   // setColourDisplay()


    imageView.setImageBitmap(viewModel.tinyIm)
    redSeekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            rgbValueInt[0] =i
            setColourDisplay()
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
            //       viewModel.rgbValue[0] = p0.get
        }
    }
    )

    greenSeekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            rgbValueInt[1] =i
            setColourDisplay()
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    }
    )

    blueSeekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            rgbValueInt[2] =i
            setColourDisplay()
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    }
    )

    alphaSeekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            rgbValueInt[3] =i
            setColourDisplay()
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    }
    )

    bgColourView.setOnClickListener { setMixin( Mixin.BG_CLR)

     //   textView3.setStrokeColor(resources.getColorStateList(R.color.colour_button_colours))
     //   textView3.strokeWidth = 4
    }
    textView3.setOnClickListener { setMixin(Mixin.BG_CLR) } // setBgClr(); setColourDisplay(bgColourView) }

    minColourView.setOnClickListener {  setMixin( Mixin.MIN_CLR )}
    textView4.setOnClickListener { setMixin( Mixin.MIN_CLR )}

    maxColourView.setOnClickListener { setMixin( Mixin.MAX_CLR )}
    textView5.setOnClickListener { setMixin( Mixin.MAX_CLR )}

    clrFunEditText.doAfterTextChanged { text -> try {

        clrFun = text.toString().toDouble().absoluteValue
    }catch (xx:java.lang.NumberFormatException){ /* just skip */}
    }
    okButton.setOnClickListener{
         val job =  callbacks?.pickedColours(requireContext(), bgClrArray, minClrArray, maxClrArray, "default", clrFun)

        job?.invokeOnCompletion{

             //   viewModel.saveSymi()

            viewModel.saveMedSymImage(context)
            callbacks?.redisplayMedImage()
        }
    }
    //cancelButton.setOnClickListener { callbacks?.cancelPickColours() }

    colourRecyclerView.adapter = ColourRecyclerViewAdapter(viewModel, callbacks)
}

    private fun setMixin(mixinIn: Mixin){
        mixin = mixinIn
        val selectedClr = argb(255,255,255,255)
        val unSelectedClr = argb(255,0,255,100)
        when(mixin){
            Mixin.BG_CLR -> {
                textView3.setTextColor(selectedClr)
                textView4.setTextColor(unSelectedClr)
                textView5.setTextColor(unSelectedClr)

            }

            Mixin.MIN_CLR -> {
                textView4.setTextColor(selectedClr)
                textView3.setTextColor(unSelectedClr)
                textView5.setTextColor(unSelectedClr)
            }
            Mixin.MAX_CLR -> {
                textView5.setTextColor(selectedClr)
                textView4.setTextColor(unSelectedClr)
                textView3.setTextColor(unSelectedClr)
            }
        }
        setColourDisplay()
    }
    fun setBgClr(){
        rgbValueInt.forEachIndexed{ i, vv -> bgClrArray[i] = vv}
        //refreshImageView()
    }
    fun setMinClr(){
        rgbValueInt.forEachIndexed{ i, vv -> minClrArray[i] = vv}
       // refreshImageView()

    }
    fun setmaxClr(){
        rgbValueInt.forEachIndexed{ i, vv -> maxClrArray[i] = vv}
       // refreshImageView()

    }

fun setColourDisplay() {
 //   viewModel.let {
    //    val intArray = viewModel.rgbValueInt
    val btnView = when(mixin){
        Mixin.BG_CLR -> bgColourView
        Mixin.MIN_CLR -> minColourView
        Mixin.MAX_CLR -> maxColourView
    }
    val clr = argb(rgbValueInt[3], rgbValueInt[0],rgbValueInt[1], rgbValueInt[2])
    btnView.setBackgroundColor(clr)
    btnView.invalidate()

    colourDisplay.setBackgroundColor(clr)
    colourDisplay.invalidate()

        Log.d("setColourDisplay",String.format("Called, rgb is %d %d %d" ,rgbValueInt[0], rgbValueInt[1], rgbValueInt[2] ))
    when(mixin){
        Mixin.BG_CLR -> setBgClr()
        Mixin.MIN_CLR -> setMinClr()
        Mixin.MAX_CLR -> setmaxClr()
    }
    refreshImageView()
}
    private fun refreshImageView(){
             var generateJob =  callbacks!!.doQuickReColour(requireContext(), imageView,
                bgClrArray, minClrArray, maxClrArray,"default", clrFun)

            generateJob.invokeOnCompletion {
                imageView.invalidate()
            }
      //          imageView.setImageBitmap(context?.let { it1 ->
        //            viewModel.tinyIm
          //      })
           // }
    }

}

