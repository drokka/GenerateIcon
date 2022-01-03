package com.drokka.emu.widgets

import java.lang.Float.max
import java.lang.Float.min

class TapeSegment {
    private val zoomFactor: Float

    /* initial values for restore */
    val min:Float

    val max:Float
    val baseWidth:Float

    val numSegments:Float

    constructor(minVal:Float, maxVal:Float, numSeg:Float, zoomi:Float){
        numSegments = numSeg
        zoomFactor = zoomi
        min = min(minVal, maxVal)
        max = max(maxVal, minVal)
        baseWidth =max - min
        _virtyWidth = baseWidth
        portMin = min
        portMax = max
    }

    private var _virtyWidth: Float = 1f
    var virtyWidth: Float = _virtyWidth
        get() = _virtyWidth

    var portMin: Float = 0f

    var portMax:Float = 1f


    var zoom:Float = 1f
    var viewCentre
        get() = portMin + 0.5f * (portMax - portMin)
        set(value) {
             portMin = value - 0.5f*virtyWidth
            portMax = value + 0.5f*virtyWidth
        }

    fun shift(fl: Float) {
        var shiftAmount = fl
        if(fl > 0){
            if (portMax + fl >max){
                shiftAmount = max - portMax
            }
        }
        if(fl<0){
            if(portMin +fl < min){
                shiftAmount = min - portMin
            }
        }
        portMin = portMin + shiftAmount
        portMax = portMax + shiftAmount
        _virtyWidth = portMax - portMin
        if(!(min <= portMin && portMax <= max))
        { throw Exception("out of bounds crazy")}
    }

    fun zoomIn(fl: Double) {
        zoom *= zoomFactor
        viewCentre = fl.toFloat()
        _virtyWidth = baseWidth/zoom
        portMin = max(viewCentre -0.5f*_virtyWidth, min)
        portMax = min(viewCentre + 0.5f*_virtyWidth, max)
        _virtyWidth = portMax - portMin


        //if(fl<1) zoomFactor*fl else zoomFactor
    }

    fun zoomOut(fl: Double) {
        zoom = if(zoom ==1f) 1f else zoom/zoomFactor
    viewCentre = fl.toFloat()
        _virtyWidth = baseWidth/zoom
        portMin = max(viewCentre -0.5f*_virtyWidth, min)
        portMax = min(viewCentre + 0.5f*_virtyWidth, max)
        _virtyWidth = portMax - portMin

        //if(fl>1) zoomFactor*fl else zoomFactor
    }

}