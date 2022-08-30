package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater

import com.drokka.emu.symicon.generateicon.R



/**
 * A simple [Fragment] subclass.
 * Use the [MainActivityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainActivityFragment : Fragment() {
     private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

       val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)

    }

    interface Callbacks{
        fun onCloseMe(b: Boolean)
    }
    private var callbacks: MainActivityFragment.Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val  view =  inflater.inflate(R.layout.fragment_main_activity, container, false)
        imageView = view.findViewById(R.id.imageButton2)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // runBlocking { delay(22000) }
       // callbacks?.onCloseMe()
       counter(false)
        imageView.setOnClickListener {
            counter(true)
        }
    }

    override fun onResume() {
        super.onResume()

        counter(false)
    }
    private fun counter(b: Boolean) {
        object : CountDownTimer(500, 500) {
            override fun onTick(millisUntilFinished: Long) {

            }


            override fun onFinish() {
                callbacks?.onCloseMe(b)
            }
        }.start()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainActivityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainActivityFragment().apply {

            }
    }
}