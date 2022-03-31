package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import com.drokka.emu.symicon.generateicon.R
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job


class PickColourFragment : Fragment() {

    companion object {
        fun newInstance() = PickColourFragment()
    }
interface Callbacks{
    fun pickedColours(
        context: Context,
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray
    ): Deferred<Unit?>
    fun cancelPickColours()
    fun redisplayMedImage()
    fun doQuickReColour(
        context: Context,
        imageView: ImageView,
        bgClrArray: IntArray,
        minClrArray: IntArray,
        maxClrArray: IntArray
    ): Job //, bgClrIntArray: IntArray, minClrIntArray: IntArray, maxClrIntArray: IntArray ): Job
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
private lateinit var colourDisplay: View
private lateinit var bgColourView: View
private lateinit var minColourView: View
private lateinit var maxColourView: View
private lateinit var okButton: Button
private lateinit var cancelButton: Button
private lateinit var imageView: ImageView

    private var bgClrArray = intArrayOf(0,0,0,255)
    private var minClrArray = intArrayOf(0,0,0,255)
    private var maxClrArray = intArrayOf(0,0,0,255)
    private var rgbValueInt = intArrayOf(0,0,0,255)

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view = inflater.inflate(R.layout.fragment_pick_colour, container, false)
    redSeekBar = view.findViewById(R.id.redSeekBar)
    blueSeekBar = view.findViewById(R.id.blueSeekBar)
    greenSeekBar = view.findViewById(R.id.greenSeekBar)
    colourDisplay = view.findViewById(R.id.colourDisplayView)
    bgColourView = view.findViewById(R.id.viewBgClr)
    minColourView = view.findViewById(R.id.viewMinClr)
    maxColourView = view.findViewById(R.id.viewMaxClr)
    okButton = view.findViewById(R.id.okClrButton)
    cancelButton = view.findViewById(R.id.cancelClrButton)
    imageView = view.findViewById(R.id.pickClrImageView)
    return view
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    imageView.setImageBitmap(viewModel.tinyIm)
    redSeekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            rgbValueInt[0] =i
            setColourDisplay(colourDisplay)
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
            setColourDisplay(colourDisplay)
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
            setColourDisplay(colourDisplay)
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    }
    )
    bgColourView.setOnClickListener {setBgClr(); setColourDisplay(it) }
    minColourView.setOnClickListener { setMinClr(); setColourDisplay(it) }
    maxColourView.setOnClickListener { setmaxClr(); setColourDisplay(it) }

    okButton.setOnClickListener{
        val job = callbacks?.pickedColours(requireContext(), bgClrArray, minClrArray, maxClrArray)

        job?.invokeOnCompletion{
            callbacks?.redisplayMedImage()
        }
    }
    cancelButton.setOnClickListener { callbacks?.cancelPickColours() }
}

    fun setBgClr(){
        rgbValueInt.forEachIndexed{ i, vv -> bgClrArray[i] = vv}
        refreshImageView()
    }
    fun setMinClr(){
        rgbValueInt.forEachIndexed{ i, vv -> minClrArray[i] = vv}
        refreshImageView()

    }
    fun setmaxClr(){
        rgbValueInt.forEachIndexed{ i, vv -> maxClrArray[i] = vv}
        refreshImageView()

    }

fun setColourDisplay(myview: View){
 //   viewModel.let {
    //    val intArray = viewModel.rgbValueInt
        myview.setBackgroundColor(
            Color.argb(255,
            rgbValueInt[0],rgbValueInt[1], rgbValueInt[2]))
    myview.invalidate()
        Log.d("setColourDisplay",String.format("Called, rgb is %d %d %d" ,rgbValueInt[0], rgbValueInt[1], rgbValueInt[2] ))
   // }
}
    fun refreshImageView(){
            var generateJob = callbacks!!.doQuickReColour(requireContext(), imageView,  bgClrArray, minClrArray, maxClrArray)

            generateJob.invokeOnCompletion {
                imageView.invalidate()
            }
      //          imageView.setImageBitmap(context?.let { it1 ->
        //            viewModel.tinyIm
          //      })
           // }
    }

}