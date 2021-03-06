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
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var alphaSeekBar: SeekBar
private lateinit var colourDisplay: View
private lateinit var bgColourView: View
private lateinit var minColourView: View
private lateinit var maxColourView: View
private lateinit var okButton: Button
private lateinit var cancelButton: Button
private lateinit var imageView: ImageView

private lateinit var textView3:TextView
    private lateinit var textView4:TextView
    private lateinit var textView5:TextView

    private var bgClrArray = intArrayOf(0,0,0,255)
    private var minClrArray = intArrayOf(0,0,0,255)
    private var maxClrArray = intArrayOf(0,0,0,255)
    private var rgbValueInt = intArrayOf(0,0,0,255)

    private lateinit var colourRecyclerView:RecyclerView
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
    cancelButton = view.findViewById(R.id.cancelClrButton)
    imageView = view.findViewById(R.id.pickClrImageView)

    textView3 = view.findViewById(R.id.textView3)
    textView4 = view.findViewById(R.id.textView4)
    textView5 = view.findViewById(R.id.textView5)
    colourRecyclerView = view.findViewById(R.id.idColourRecyclerView)
    return view
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bgClrArray = viewModel.bgClrInt
    minClrArray = viewModel.minClrInt
    maxClrArray = viewModel.maxClrInt

    setColourDisplay(colourDisplay)

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

    alphaSeekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            rgbValueInt[3] =i
            setColourDisplay(colourDisplay)
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    }
    )

    bgColourView.setOnClickListener {setBgClr(); setColourDisplay(it) }
    textView3.setOnClickListener {setBgClr(); setColourDisplay(bgColourView) }

    minColourView.setOnClickListener { setMinClr(); setColourDisplay(it) }
    textView4.setOnClickListener { setMinClr(); setColourDisplay(minColourView) }

    maxColourView.setOnClickListener { setmaxClr(); setColourDisplay(it) }
    textView5.setOnClickListener { setmaxClr(); setColourDisplay(maxColourView) }


    okButton.setOnClickListener{
        val job = callbacks?.pickedColours(requireContext(), bgClrArray, minClrArray, maxClrArray)

        job?.invokeOnCompletion{

             //   viewModel.saveSymi()
            viewModel.saveMedSymImage(context)
            callbacks?.redisplayMedImage()
        }
    }
    cancelButton.setOnClickListener { callbacks?.cancelPickColours() }

    colourRecyclerView.adapter = ColourRecyclerViewAdapter(viewModel, callbacks)
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
            Color.argb(rgbValueInt[3],
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

