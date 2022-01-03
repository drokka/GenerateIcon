package com.drokka.emu.widgets

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [ParameterListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ParameterListFragment : Fragment() {

    var parameterNames:String? = "lambda, alpha, beta, gamma, omega, ma"
    var parameterList: List<Pair<String, Float>> =
        arrayListOf(Pair("lambda",0.5f), Pair("alpha",0.5f), Pair("beta",0.5f))
    //        , Pair("gamma",0.5f),
    //        Pair("omega",0.5f), Pair("ma",0.5f))

    private lateinit var parameterRecyclerView: RecyclerView

    private inner class ParameterHolder(view: FloatInView)
        : RecyclerView.ViewHolder(view) {
        val myView = view
    }

    private inner class ParameterAdapter(var params: List<Pair<String, Float>>)
        : RecyclerView.Adapter<ParameterHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : ParameterHolder {
            val view = layoutInflater.inflate(R.layout.parameter_item_view, parent, false)
            return ParameterHolder(view as FloatInView)
        }

        override fun getItemCount() = params.size

        override fun onBindViewHolder(holder: ParameterHolder, position: Int) {
            val param = params.get(position)
            holder.apply {
                myView.parameterName = param.first
                myView.selectedValue = param.second.toDouble()
            }
        }
    }

    private var adapter: ParameterAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.parameter_list_fragment, container, false)
         parameterRecyclerView = view.findViewById(R.id.parameter_recycler_view)
        parameterRecyclerView.layoutManager = LinearLayoutManager(context)



        adapter = ParameterAdapter(parameterList )
        parameterRecyclerView.adapter = adapter

        return view
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
 //       val a = context?.obtainStyledAttributes(
  //          attrs, R.styleable.ParameterListFragment, 0, 0
 //       )
  //      Log.i("onInflate", "attrs: "+attrs.styleAttribute.toString())
 /*********       parameterNames = a.getString(R.styleable.ParameterListFragment_parameterNames)
        if(!parameterNames.isNullOrEmpty()){
            var names = parameterNames!!.splitToSequence(",")
            parameterList = emptyList()
            for(name in names) {
               parameterList.plus(  Pair(name, 0.5f))

            }
        } **********************************************************************/
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ParameterListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ParameterListFragment().apply {
                arguments = Bundle().apply {
                   // parameterList = params
                }
            }
    }
}