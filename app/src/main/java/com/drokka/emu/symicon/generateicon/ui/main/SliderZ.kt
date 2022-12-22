package com.drokka.emu.symicon.generateicon.ui.main

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.slider.Slider
import kotlin.math.abs

/**
 * Add long press and short press response to Slider so that scale precision can be increased
 * and reverted. Directly coding the behaviour since can't see those actions.
 * Requires Build.VERSION_CODES.Q to work. API 29. Android 10.
 */
class SliderZ : Slider {

    private var lastDownTime: Long =0
    private var lastIsDown: Boolean = false
    private var zoomLevel = 1

    private var initialValueFrom = -1f
    private var initialValueTo = 1f

    private var lastDownValue = 0f
    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes

        initialValueTo = this.valueTo
        initialValueFrom = this. valueFrom

    }

    // NOTE overiding onLongKeyPress, performKEYPress etc seems to have no effect.
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onTouchEvent(event: MotionEvent): Boolean {
          Log.d("onTouchEvent", "touch event " + event.toString())

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                lastIsDown = true
                lastDownTime = System.currentTimeMillis()
                lastDownValue = value

            }
            MotionEvent.ACTION_UP -> {
                  Log.d("onTouchEvent", "ACTION_UP")

                val timePressed = System.currentTimeMillis() - lastDownTime
                if (lastIsDown &&  timePressed > 1000 && timePressed < 5000
                    && abs((value - lastDownValue).toDouble()) > (valueFrom - valueTo)/25) {
                      Log.d("onTouchEvent", "ACTION_UP doing zoom move diff is " + (value - lastDownValue))
                    doZoom()

                }
                else if (lastIsDown && timePressed < 700 && zoomLevel != 1){
                    resetZoom()
                }
                lastIsDown = false
            }
            MotionEvent.ACTION_MOVE -> {
                val diff = abs(value - lastDownValue)
                val grubble = abs(valueFrom - valueTo)/20
                  Log.d("onTouchEvent", "ACTION_MOVE $diff and $grubble" )
                if(diff > grubble){
                    lastIsDown = false
                      Log.d("onTouchEvent", "ACTION_MOVE  set lastIsDown to false $diff and $grubble" )

                }
                // else leave lastIsDown possibly true so long press processed with smallish change in thumb position ie value.
            }
            else -> {
                  Log.d("onTouchEvent", "event is ${event.action}")
                    lastIsDown = false
            }
        }
        return super.onTouchEvent(event)
    }


   private fun resetZoom() {
        zoomLevel = 1
        valueTo = initialValueTo
        valueFrom = initialValueFrom
        Toast.makeText(context, "Zoom level reset.", Toast.LENGTH_SHORT).show()

          Log.d("resetZoom", "resetZoom called")
    }

    private fun doZoom() {
        if(zoomLevel == 1000) return // do nothing
        zoomLevel = zoomLevel* 10
        val range = valueTo - valueFrom
        val currentValue = value
        valueTo = currentValue + range/(2*zoomLevel)

        valueFrom =  currentValue - range/(2*zoomLevel )// value stays in middle of zoom in range
        this.value = currentValue  // try to get thumb position to middle
        this.refreshDrawableState()
       // isActivated
          Log.d("onLongKeyPress" , "got it zoom = $zoomLevel")
        Toast.makeText(context, "Zoom level $zoomLevel", Toast.LENGTH_SHORT).show()
    }

}