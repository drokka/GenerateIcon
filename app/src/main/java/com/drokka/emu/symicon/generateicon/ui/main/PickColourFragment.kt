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
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.drokka.emu.symicon.generateicon.R
import kotlinx.coroutines.Deferred


class PickColourFragment : Fragment() {

    companion object {
        fun newInstance() = PickColourFragment()
    }
interface Callbacks{
    fun pickedColours(context: Context): Deferred<Unit?>
    fun cancelPickColours()
    fun redisplayMedImage()
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
    return view
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    redSeekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            viewModel.rgbValueInt[0] =i
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
            viewModel.rgbValueInt[1] =i
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
            viewModel.rgbValueInt[2] =i
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
        val job = callbacks?.pickedColours(requireContext())

        job?.invokeOnCompletion{
            callbacks?.redisplayMedImage()
        }
    }
    cancelButton.setOnClickListener { callbacks?.cancelPickColours() }
}

    fun setBgClr(){
        viewModel.rgbValueInt.forEachIndexed{ i, vv -> viewModel.bgClrInt[i] = vv}
    }
    fun setMinClr(){
        viewModel.rgbValueInt.forEachIndexed{ i, vv -> viewModel.minClrInt[i] = vv}

    }
    fun setmaxClr(){
        viewModel.rgbValueInt.forEachIndexed{ i, vv -> viewModel.maxClrInt[i] = vv}
    }

fun setColourDisplay(view: View){
    viewModel.let {
        val intArray = viewModel.rgbValueInt
        view.setBackgroundColor(
            Color.argb(intArray[3],
            intArray[0],intArray[1], intArray[2]))
        Log.d("setColourDisplay",String.format("Called, rgb is %d %d %d" ,intArray[0], intArray[1], intArray[2] ))
    }
}

}