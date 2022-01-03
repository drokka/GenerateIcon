package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.compose.ui.graphics.asImageBitmap
import com.drokka.emu.symicon.generateicon.R
//import com.drokka.emu.symicon.generateicon.databinding.FragmentEditSymiBinding
import kotlinx.coroutines.Job

class EditSymiFragment : Fragment() {

    companion object {
        fun newInstance() = EditSymiFragment()
    }

    private lateinit var viewModel: EditSymiViewModel
    //private lateinit var binder:FragmentEditSymiBinding

    private lateinit var quickDrawImageButton: ImageButton

    fun alphaChanged(oldie:Double,newie:Double){ viewModel.setAlpha(newie);doQuickDraw()}
    fun betaChanged(oldie:Double,newie:Double){ viewModel.setBeta(newie);doQuickDraw()}
    fun gammaChanged(oldie:Double,newie:Double){ viewModel.setGamma(newie);doQuickDraw()}
    fun omegaChanged(oldie:Double,newie:Double){ viewModel.setOmega(newie);doQuickDraw()}
    fun maChanged(oldie:Double,newie:Double){ viewModel.setMa(newie);doQuickDraw()}

    interface Callbacks {

        fun onImageIconSelected()
        fun onGenerateClicked(context: Context): Job?  //Hopefully to run in viewmodel scope lifecycle
        fun doQuickDraw(context: Context):Job?
    }

    var callbacks:Callbacks? = null

    private fun doQuickDraw() {
        if(viewModel.isLoading) return
        var generateJob: Job? = null
        context?.let { it1 -> generateJob = callbacks!!.doQuickDraw(it1) }
        generateJob?.invokeOnCompletion {
           // quickDrawImageButton.setImageBitmap(viewModel.generatedTinyImage?.getBitmap())
            viewModel.tinyImageBitmap?.value =  viewModel.generatedTinyImage?.getBitmap()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // binder = FragmentEditSymiBinding.inflate(inflater,container,false)
      //  binder.lifecycleOwner = this.viewLifecycleOwner

        return inflater.inflate(R.layout.fragment_edit_symi, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditSymiViewModel::class.java)
      //  binder.viewModel = viewModel
    }

}