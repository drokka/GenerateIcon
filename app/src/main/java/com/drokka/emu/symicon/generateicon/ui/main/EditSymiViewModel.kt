package com.drokka.emu.symicon.generateicon.ui.main

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drokka.emu.symicon.generateicon.data.GeneratedIconAndImageData
import com.drokka.emu.symicon.generateicon.data.GeneratedImage
import com.drokka.emu.symicon.generateicon.data.QuiltType
import com.drokka.emu.symicon.generateicon.data.SymImageDefinition

class EditSymiViewModel : ViewModel() {
    var symImageDefinition = SymImageDefinition.defaultSimyDef(QuiltType.SQUARE)


         val lambda: MutableLiveData<Double> = MutableLiveData(symImageDefinition.lambda)
         val alpha: MutableLiveData<Double> =MutableLiveData(symImageDefinition.alpha)
         val beta: MutableLiveData<Double> =MutableLiveData(symImageDefinition.beta)
         val gamma: MutableLiveData<Double> =MutableLiveData(symImageDefinition.gamma)
         val omega: MutableLiveData<Double> =MutableLiveData(symImageDefinition.omega)

    var generatedTinyIAD: GeneratedIconAndImageData? = null
    var generatedTinyImage: GeneratedImage? = null
    var generatedMedIAD: GeneratedIconAndImageData? = null
    var generatedMedImage: GeneratedImage? = null

    //Bitmap for button display
    val tinyImageBitmap:MutableLiveData<Bitmap>? = MutableLiveData(null)

    var isLoading:Boolean  = false

    fun setLambda(sz:Double){
        try {
            symImageDefinition.lambda = sz
        }catch(x:Exception){}

    }
    fun setAlpha(sz:Double){
        try {
            symImageDefinition.alpha = sz
        }catch(x:Exception){}
    }
    fun setBeta(sz:Double){
        try {
            symImageDefinition.beta = sz
        }catch(x:Exception){}
    }
    fun setGamma(sz:Double){
        try{
            symImageDefinition.gamma = sz
        }catch (x:Exception){}
    }
    fun setOmega(sz:Double){
        try{
            symImageDefinition.omega = sz
        }catch (x:Exception){}
    }
    fun setMa(sz: Double){
        try{
            symImageDefinition.ma = sz
        }catch (x:Exception){}
    }

    //   companion object {
        var callbackCalled = false

        fun onFivChangeVoid(v: View?) {
            Log.d("onFivChangeVoid", "callback called ma= " + symImageDefinition.ma.toString())
            callbackCalled = true
        }

        val onFivChange: (fl:Double, fl1:Double)->Unit =  { fl: Double, fl1: Double ->
            Log.d("onFivChange", "callback called")
            callbackCalled = true
             Log.i( "Changed Fiv parameter", "value is: " +fl1.toString())
        }
}