package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.drokka.emu.symicon.generateicon.data.GeneratedIconWithAllImageData
import com.drokka.emu.symicon.generateicon.data.SymIcon
import com.drokka.emu.symicon.generateicon.databinding.FragmentSymIconListBinding
import com.drokka.emu.symicon.generateicon.getBitmap
import kotlinx.coroutines.runBlocking

/**
 * [RecyclerView.Adapter] that can display a [SymIcon].
 */
class SymIconRecyclerViewAdapter(
    internal val viewModel: MainViewModel //MutableLiveData<List<GeneratedIconWithAllImageData>>
) : RecyclerView.Adapter<SymIconRecyclerViewAdapter.ViewHolder>() {

    init {
      //  viewModel.symImageListAll.observe(this, )
      //  runBlocking { (Runnable {viewModel.symImageListAll.observeForever{ }}) }
      //  viewModel.symImageListAll.observeForever{}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

      //  parent.findViewTreeLifecycleOwner()?.let { viewModel.symImageListAll.observe( it ,this)}
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
        values.let {
            if (!values.value.isNullOrEmpty()) {
                val item = values!!.value?.get(position)
                item?.let {
                    //   holder.idView.text = position.toString()   //item.generatedIcon.generatedDataFileName   //symIcon.label
                    holder.contentView.text =
                        item.label // .iterations.toString()

                    val genIcon = viewModel.getGeneratedIcon(it)
                    val genImData = viewModel.getGeneratedImage(genIcon,it)

                    //        val byteArray = viewModel.getIconBitmap( holder.context ,item.generatedImageDataId )  //item.byteArray
                    //        val len = byteArray?.size
                    //       if ((byteArray != null) && (len == item.len)) {
                    //            len?.let {
                    holder.bitMap = genImData.getBitmap(holder.context)        //BitmapFactory.decodeByteArray(byteArray, 0, it)
                    Log.d("onBindViewHolder SymIcon recycler view adapter", "got bitMap")
                    holder.iconImageView.setImageBitmap(holder.bitMap)
                    //      }
                    Log.d(
                        "onBindViewHolder SymIcon recycler view adapter",
                        "After len?let block"
                    )
                    //   }
                }
            }
        }
    }

    override fun getItemCount(): Int = viewModel.symImageListAll?.value?.size ?: 0

    inner class ViewHolder(binding: FragmentSymIconListBinding) :
        RecyclerView.ViewHolder(binding.root) , View.OnClickListener, View.OnLongClickListener {
       // val idView: TextView = binding. itemNumber
        val context: Context = binding.root.context
        val contentView: TextView = binding.content
        var iconImageView:ImageView = binding.iconImageView
        var bitMap: Bitmap? = null
         //   var byteArray:ByteArray? = null
        val res = binding.also {
             this.itemView.setOnClickListener {
                 val callbacks = it?.context as SymIconListFragment.Callbacks
                 if (!viewModel.symImageListAll.value.isNullOrEmpty()) {
                     callbacks.onSymIconItemSelected(
                         (viewModel.symImageListAll.value as
                                 List<GeneratedIconWithAllImageData>)!![absoluteAdapterPosition]
                     )
                 }
             }
             this.itemView.setOnLongClickListener {
                 val callbacks = it?.context as SymIconListFragment.Callbacks
                     val builder = AlertDialog.Builder(it.context)
                     builder.setMessage("Are you sure?")
                         .setTitle("Delete")
                     builder.setPositiveButton(
                         "Yes"
                     ) { dialog, which ->
                         Log.d("long click listener", "yes")
                         var generatedIconWithAllImageData:GeneratedIconWithAllImageData? = null
                         try {
                             generatedIconWithAllImageData = (viewModel.symImageListAll.value as
                                     List<GeneratedIconWithAllImageData>)[absoluteAdapterPosition]
                         }catch (err:Exception){
                             Log.e("Adapter delete", "Adapter list index error: " +err.message)
                             return@setPositiveButton
                         }
                         callbacks.deleteSymIcon(it.context, generatedIconWithAllImageData as GeneratedIconWithAllImageData)
                         //  viewModel.symImageListAll
                         //Hack needed because the notifies happen too quickly I think.
                          (viewModel.symImageListAll.value as MutableList<GeneratedIconWithAllImageData>)
                              .removeAt(absoluteAdapterPosition)
                         notifyItemRemoved(this.absoluteAdapterPosition)
                         //  notifyItemRangeChanged(absoluteAdapterPosition, itemCount-absoluteAdapterPosition)
                         notifyDataSetChanged()
                       //  bindingAdapter?.notifyItemRangeChanged(absoluteAdapterPosition, itemCount-absoluteAdapterPosition)

                     }
                         .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                             Log.d("long click listener", "no")
                         })
                     builder.create()
                     builder.show()
                     return@setOnLongClickListener true

                 }
             }







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

        override fun onLongClick(v: View?): Boolean {
            Log.d("onLongClick in binder", "called, itemId is " + absoluteAdapterPosition)
            return true
        }


    }

}