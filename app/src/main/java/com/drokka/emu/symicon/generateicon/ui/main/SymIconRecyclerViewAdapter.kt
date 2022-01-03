package com.drokka.emu.symicon.generateicon.ui.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.drokka.emu.symicon.generateicon.data.GeneratedIconWithAllImageData
import com.drokka.emu.symicon.generateicon.data.SymIcon
import com.drokka.emu.symicon.generateicon.databinding.FragmentSymIconListBinding
import kotlinx.coroutines.runBlocking

/**
 * [RecyclerView.Adapter] that can display a [SymIcon].
 * TODO: Replace the implementation with code for your data type.
 */
class SymIconRecyclerViewAdapter(
    internal val viewModel: MainViewModel //MutableLiveData<List<GeneratedIconWithAllImageData>>
) : RecyclerView.Adapter<SymIconRecyclerViewAdapter.ViewHolder>() {

    init {
        runBlocking { (Runnable {viewModel.symImageListAll.observeForever{ }}) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val viewHolder= ViewHolder(
            FragmentSymIconListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("onBindViewHolder start", "ACTUALLY CALLLED!!!!!!!!!")
        val values = viewModel.symImageListAll as LiveData<List<GeneratedIconWithAllImageData>>
        values?.let {
            if (!values.value.isNullOrEmpty()) {
                val item = values!!.value?.get(position)
                item?.let {
                    //   holder.idView.text = position.toString()   //item.generatedIcon.generatedDataFileName   //symIcon.label
                    holder.contentView.text =
                        item.label // .iterations.toString()
                    val byteArray = item.byteArray
                    val len = byteArray?.size
                    if ((byteArray != null) && (len == item.len)) {
                        len?.let {
                            holder.bitMap = BitmapFactory.decodeByteArray(byteArray, 0, it)
                            Log.d("onBindViewHolder SymIcon recycler view adapter", "got bitMap")
                            holder.iconImageView.setImageBitmap(holder.bitMap)
                        }
                        Log.d(
                            "onBindViewHolder SymIcon recycler view adapter",
                            "After len?let block"
                        )
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = viewModel.symImageListAll?.value?.size ?: 0

    inner class ViewHolder(binding: FragmentSymIconListBinding) :
        RecyclerView.ViewHolder(binding.root) , View.OnClickListener {
       // val idView: TextView = binding. itemNumber
        val contentView: TextView = binding.content
        var iconImageView:ImageView = binding.iconImageView
        var bitMap: Bitmap? = null
         //   var byteArray:ByteArray? = null
        val res = binding.also { this.itemView.setOnClickListener {
             if(!viewModel.symImageListAll.value.isNullOrEmpty()) {
                 (it?.context as SymIconListFragment.Callbacks).onSymIconItemSelected((viewModel.symImageListAll.value as
                                List<GeneratedIconWithAllImageData>)!![absoluteAdapterPosition])
             }
        } }

        /*****
        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
        ***************/
   //     override fun bind()
        override fun onClick(view:View?){
           // FragmentManager.findFragment(view)
          //  view.State
           // values
//THIS has no effect. Setting onclick listener above does work
            Log.d("onClick in binder", "called, itemId is " + absoluteAdapterPosition)
         //  val pos:Int = absoluteAdapterPosition

          //  (view?.context as SymIconListFragment.Callbacks).onSymIconItemSelected(values[pos])
        }


    }

}