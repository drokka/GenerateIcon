package com.drokka.emu.symicon.generateicon.ui.main

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.drokka.emu.symicon.generateicon.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [BigImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BigImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
 //   var bigIconImageBitmap:Bitmap? = null


    lateinit var bigImageView: ImageView
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

  //      bigImageView.setImageBitmap(bigIconImageBitmap)
  //      view.isDirty
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
        fun newInstance(/*bitmap: Bitmap*/) =
            BigImageFragment().apply {
              //  arguments = Bundle().apply {
             //       bigIconImageBitmap = bitmap
              //  }
            }
    }
}