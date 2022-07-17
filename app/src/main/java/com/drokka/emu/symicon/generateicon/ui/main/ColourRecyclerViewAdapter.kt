package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drokka.emu.symicon.generateicon.data.ClrPalette
import com.drokka.emu.symicon.generateicon.database.SymiTypeConverters
import com.drokka.emu.symicon.generateicon.databinding.FragmentColourPaletteBinding

class ColourRecyclerViewAdapter(
    internal val viewModel: MainViewModel,
    internal val callbacks: PickColourFragment.Callbacks?
)
: RecyclerView.Adapter<ColourRecyclerViewAdapter.ViewHolder>() {

    var clrPaletteList: List<ClrPalette>? =null
    override fun getItemCount(): Int {
        clrPaletteList = MainViewModel.symiRepo.getClrPalleteList()
        return clrPaletteList?.size?:0  //viewModel.paletteList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           //viewModel.paletteList.value
        if(!clrPaletteList.isNullOrEmpty()){
            val bgClr = SymiTypeConverters.JSONArrayToDoubleArray(clrPaletteList!![position].bgClr)
            val minClr = SymiTypeConverters.JSONArrayToDoubleArray(clrPaletteList!![position].minClr)
            val maxClr = SymiTypeConverters.JSONArrayToDoubleArray(clrPaletteList!![position].maxClr)

            val bgClrInt = SymiTypeConverters.doubleArrayToClrInt(bgClr)
            val minClrInt = SymiTypeConverters.doubleArrayToClrInt(minClr)
            val maxClrInt = SymiTypeConverters.doubleArrayToClrInt(maxClr)

            holder.bgClrView.setBackgroundColor(bgClrInt)
            holder.minClrView.setBackgroundColor(minClrInt)
            holder.maxClrView.setBackgroundColor(maxClrInt)

            holder.bgClr = bgClrInt; holder.minClr = minClrInt; holder.maxClr = maxClrInt

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder= ViewHolder(
            FragmentColourPaletteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        return viewHolder    }

    inner class ViewHolder(binding: FragmentColourPaletteBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        val context: Context = binding.root.context
        val bgClrView = binding.bgClrmageView
        val minClrView = binding.minClrImageView
        val maxClrView = binding.maxClrImageView

        var bgClr: Int = Color.BLACK
        var minClr: Int = Color.GREEN

        var maxClr: Int = Color.RED

        val res = binding.also {
            this.bgClrView.setOnClickListener {
                this.onClick(it)
            }
            this.minClrView.setOnClickListener {
                this.onClick(it)
            }
            this.maxClrView.setOnClickListener {
                this.onClick(it)
            }
        }

    //    fun numberToByteArray(data: Int, size: Int = 4): ByteArray =
      //      ByteArray(size) { i -> (data.toLong() shr (i * 8)).toByte() }



        override fun onClick(v: View?) {
            val job = callbacks?.pickedColours(
                context,
                SymiTypeConverters.clrIntToIntArray(bgClr),
                SymiTypeConverters.clrIntToIntArray(minClr),
                SymiTypeConverters.clrIntToIntArray(maxClr)
            )
            job?.invokeOnCompletion {

                //   viewModel.saveSymi()
                viewModel.saveMedSymImage(context)
                callbacks?.redisplayMedImage()
            }

        }
    }
}