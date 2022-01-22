package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.drokka.emu.symicon.generateicon.R
import com.drokka.emu.symicon.generateicon.SymiRepo

import com.drokka.emu.symicon.generateicon.data.GeneratedImage
import com.drokka.emu.symicon.generateicon.getBitmap
import kotlinx.coroutines.Deferred

class ImageIconFragment() : Fragment() {

 //  lateinit var generatedImage:GeneratedImage

    companion object {
            /** Use Bundle arguments member of Fragment **********/
        fun newInstance(): ImageIconFragment {
                /*********
            val args = Bundle().apply {
              //   putSerializable("myImageFileName", generatedImage.iconImageFileName)
              //  putSerializable("myIconDef", generatedImage.generatedIcon.definition)
               // putSerializable("myDataFileName", generatedImage.generatedIcon.generatedDataFileName)
                putSerializable("myGeneratedImage", generatedImage)
            }
                ************/
            return ImageIconFragment().apply {
    //            generatedImage = generatedImageVal
            }
        }
    }

    interface Callbacks {
        fun onViewImageButtonSelected(generatedImage: GeneratedImage, context: Context)
        fun onSaveImageDataButtonSelected(button: Button)
        fun generateLargeIcon(requireContext: Context):Deferred<Unit>
        fun showBigImage()
    }
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


    private val viewModel:MainViewModel by activityViewModels()

    private lateinit var displayImageIconView: ImageView
private lateinit var viewImage:Button

    private lateinit var saveImageDataButton: Button
    private lateinit var goBigButton:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.image_icon_fragment, container, false)
        displayImageIconView = view.findViewById(R.id.displayImageIconView)!!
        viewImage = view.findViewById(R.id.viewImageButton)
        saveImageDataButton = view.findViewById(R.id.saveImageDataButton)
        goBigButton = view.findViewById(R.id.goBigButton)
        return view
    }

    override fun onViewCreated(view:View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  context?.let { SymiRepo.initialize(it) }

       // var genDef = arguments?.getSerializable("myIconDef") as GeneratorDef
        //val dataFile = arguments?.getSerializable("myDataFileName") as String
        //val imFile = arguments?.getSerializable("myImageFileName") as String
        //var generatedIcon = GeneratedIcon(genDef,"",dataFile)
        //viewModel.generatedImage = GeneratedImage( generatedIcon,null,0,imFile)
        //viewModel.generatedImage.byteArray =

 //       if(savedInstanceState !=null) {
         //   val generatedImage =
        //        arguments?.getSerializable("myGeneratedImage") as GeneratedImage?
        //NEED to ensure MainViewModel generatedImage is current
        var bitmap = viewModel.medIm
        if(bitmap== null){
            bitmap = context?.let { viewModel.generatedMedImage?.getBitmap(it) }
        }
            if (bitmap != null) {
                displayImageIconView.setImageBitmap(bitmap)
                viewImage.setOnClickListener { viewImageFun(viewModel.generatedMedImage!!, context) }
            }
        saveImageDataButton.setOnClickListener {

                callbacks?.onSaveImageDataButtonSelected(it as Button)

        }
        //Generate LARGE symi for the definition
        goBigButton.setOnClickListener {
            //Do wait UI
            val job = callbacks?.generateLargeIcon(requireContext())

            job?.invokeOnCompletion {
                callbacks?.showBigImage()
            }
        }
        }

     private fun viewImageFun(generatedImage: GeneratedImage, context: Context?) {
        if (context != null) {
            callbacks?.onViewImageButtonSelected(generatedImage,context)
        }
    }


/***
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle:Bundle? = null
        if(savedInstanceState !=null) {
             bundle = savedInstanceState
        }else {
            bundle = arguments
        }
            viewModel.generatedImage =
                bundle?.getSerializable("myGeneratedImage") as GeneratedImage?
            val bitmap = viewModel.generatedImage?.getBitmap()
            if (bitmap != null) {
                displayImageIconView.setImageBitmap(bitmap)
                viewImage.setOnClickListener { viewImageFun(viewModel.generatedImage!!, context) }
            }
    }
*****************************/
}