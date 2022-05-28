package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.drokka.emu.symicon.generateicon.MainActivity
import com.drokka.emu.symicon.generateicon.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WrapListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WrapListFragment : Fragment() {
    // TODO: Rename and change types of parameters
   // private var symIconListFragment:SymIconListFragment? = null
    var floatingActionButton:FloatingActionButton? = null
    var viewBigsFloatingActionButton: FloatingActionButton? = null
    private val viewModel:MainViewModel by activityViewModels()

    interface Callbacks{
        fun onFloatingActionButtonClicked()
        fun viewBigsFloatingActionClicked()
    }
    private var callbacks: WrapListFragment.Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           // childFragmentManager
        }
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slidel)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.wrap_sym_icon_list, container, false)
        floatingActionButton = view?.findViewById(R.id.floatingActionButton)
        Log.d("WrapListFragment", "setting FAB cllback")
            floatingActionButton?.setOnClickListener {
                callbacks?.onFloatingActionButtonClicked()
          //  findNavController().navigate(R.id.action_wrapListFragment_to_mainFragment)
        }

        viewBigsFloatingActionButton = view?.findViewById(R.id.bigImagesFloatingActionButton)
        viewBigsFloatingActionButton?.setOnClickListener {
            callbacks?.viewBigsFloatingActionClicked()
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WrapListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            WrapListFragment().apply {
                arguments = Bundle().apply {
                   // putString(ARG_PARAM1, param1)
                   // putString(ARG_PARAM2, param2)
                }
            }
    }
}



