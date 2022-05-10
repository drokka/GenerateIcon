package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.drokka.emu.symicon.generateicon.R
import com.drokka.emu.symicon.generateicon.data.GeneratedIconWithAllImageData

/**
 * A fragment representing a list of Items.
 */
class SymIconListFragment : Fragment() {

    interface Callbacks{
        fun onSymIconItemSelected(generatedImageAndImageData: GeneratedIconWithAllImageData)
        fun deleteSymIcon(context: Context, generatedIconWithAllImageData: GeneratedIconWithAllImageData)
    }

    private var callbacks:Callbacks? = null

    private var columnCount = 1

    private lateinit var viewAdapter :SymIconRecyclerViewAdapter
    private lateinit var recyclerView:View

    lateinit var symImageListAllObserver: Observer<List<GeneratedIconWithAllImageData>>
    private val viewModel:MainViewModel by activityViewModels()

  //  val  list = ConstraintSet.Transform

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        /******************
        symImageListAllObserver = Observer<List<GeneratedIconWithAllImageData>>{
                listy ->
            Log.i(DEBUG_PROPERTY_NAME, "symImageListAllObserver size is: " + listy.size)
   //       list =  listy
            viewAdapter = SymIconRecyclerViewAdapter(listy )
      //      recyclerView.updateLayoutParams(){  }


        } *************************************/

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.symImageListAll.observeForever{}

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_sym_icon_list_list, container, false)

       //   val view = inflater.inflate(R.layout.wrap_sym_icon_list, container, false)
        //    val myRecyclerView = view.findViewById<RecyclerView>(R.layout.fragment_sym_icon_list_list)
        // Set the adapter
/************
        viewModel.liveList.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                Log.i("Observing list via model", "Got this many symis ${it.size}")
                updateUI(it as List<GeneratedIconAndImageDataMerged>)
            }
        )
Put in MainActivity??****************************/

        if (view is RecyclerView) {

            recyclerView = view
            /**********************
            with(view) {
                layoutManager = LinearLayoutManager(context)/**** when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> **********/ //GridLayoutManager(context, columnCount)
               // }

            } *********************************************************/
        }
        Log.i("SymIconListFragment onCreate","life cycle owner is :" + viewLifecycleOwner.javaClass.typeName)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
   //     viewAdapter = SymIconRecyclerViewAdapter(viewModel)

        (recyclerView as RecyclerView).adapter = SymIconRecyclerViewAdapter(viewModel)

 //       runBlocking { (Runnable { (recyclerView as RecyclerView).adapter?.notifyDataSetChanged() }) }

//        viewModel.symImageListAll.observe(viewLifecycleOwner, symImageListAllObserver)


        /*****

        viewModel.liveList.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {

                Log.i("Observing list via model", "Got this many symis ${it.size}")
                updateUI(it as List<GeneratedIconAndImageDataMerged>)
            }
        )
         *********************************/


        /*************{ liveList ->
            liveList?.let {
                Log.i("Observing list via model", "Got this many symis ${liveList.size}")
                updateUI(liveList as List<GeneratedIconAndImageDataMerged>)
            }
        }
        ****************************/
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            SymIconListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}