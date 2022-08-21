package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.drokka.emu.symicon.generateicon.R
import com.google.android.material.snackbar.Snackbar
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [BigImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BigImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var bigIconImageBitmap:Bitmap? = null
    var imFileName: String? = null

    lateinit var bigImageView: ImageView
    lateinit var saveBigToGalleryBtn: Button
    val viewModel:MainViewModel by activityViewModels()

    interface Callbacks {
        fun saveImageToGallery(imFN:String, context: Context?)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
     //       bigIconImageBitmap =bigIconImageBitmap
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

       val  view = inflater.inflate(R.layout.fragment_big_image, container, false)
        bigImageView = view.findViewById(R.id.bigImageView)
        saveBigToGalleryBtn = view.findViewById(R.id.saveBigToGalleryButton)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveBigToGalleryBtn.setOnClickListener{
            imFileName?.let { it1 -> this.callbacks?.saveImageToGallery(imFN = it1, context = requireContext()) }
            Snackbar.make(view, "Image saved to media store",Snackbar.LENGTH_SHORT).show()
         }

        if(bigIconImageBitmap != null) {
            bigImageView.setImageBitmap(bigIconImageBitmap)
        }
        else{
            bigImageView.setImageDrawable( resources.getDrawable(R.drawable.symi_100px))
        }
        view.isDirty
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BigImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(bitmap: Bitmap?, imFileIn: String?) =
            BigImageFragment().apply {
                arguments = Bundle().apply {
                   bigIconImageBitmap = bitmap
                    imFileName = imFileIn
               }
            }
    }


}