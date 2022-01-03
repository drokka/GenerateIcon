package com.drokka.emu.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.*
import kotlin.math.absoluteValue
import kotlin.properties.Delegates.observable

/**
 * widget to select a float value within a range. current default (0.0.. 1.0)
 * move/scroll up zooms in, move/scroll down zooms out (until back to 1)
 * Attempted GestureDetector, but that approach seems to require hosting activity to know too much
 * about the widgets's behaviour.
 */
class FloatInView : View{

    private lateinit var labelTextPaint: TextPaint
    private lateinit var  tapeSegment:TapeSegment

    private var motionStartTouchEventY: Float= 0f
    private var motionStartTouchEventX: Float = 0f
    private var motionTouchEventX: Float =0f
    private var motionTouchEventY: Float =0f

    /* make selectedValue observable */
    @JvmSuppressWildcards
        var onSelectedValueChanged: ((Double, Double) -> Unit)? = null

    var selectedValue: Double by observable(0.5){
        _,oldy,newy ->
        onSelectedValueChanged?.invoke(oldy,newy)

    }

   var parameterName:String ="Not set!!"

    private lateinit var paintSelected: Paint
private lateinit var valueTextPaint: TextPaint
    /* virtual position on a measure tape */

    private  var pxlPerFloat:Float = 100f // Primitive types have to be initialised. Here width=0

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

 private lateinit var paintUnselected: Paint

    var portWidth: Float = 1f


    private var _unselectedColor = Color.BLACK
    var unselectedColor: Int
        get() = _unselectedColor
        set(value) {
            _unselectedColor = value
        }
    private var _selectColor = Color.RED
    var selectColor: Int
        get() = _selectColor
        set(value) {
            _selectColor = value
        }

    private lateinit var textPaint: TextPaint
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    private  var displayHeight:Float =1000f
    private  var displayWidth:Float =1000f

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


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.FloatInView, defStyle, 0
        )

        val min = a.getFloat(R.styleable.FloatInView_min,0f)
        val max = a.getFloat(R.styleable.FloatInView_max,1f)
        val numSegments = a.getFloat(R.styleable.FloatInView_numberSegments, 5f)
        val zoomify = a.getFloat(R.styleable.FloatInView_zoomFactor, 2f)

        tapeSegment = TapeSegment(min, max, numSegments, zoomify)
        parameterName = a.getString(R.styleable.FloatInView_parameterName)!!
        a.recycle()

        displayHeight = Resources.getSystem().getDisplayMetrics().heightPixels.toFloat()
        displayWidth = Resources.getSystem().getDisplayMetrics().widthPixels.toFloat()

        selectedValue = tapeSegment.viewCentre.toDouble()
        pxlPerFloat = width/tapeSegment.virtyWidth

        background = resources.getDrawable(R.drawable.dial_oval_bg)
        paintUnselected = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = unselectedColor
            style = Paint.Style.STROKE
        }

        paintSelected = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = selectColor
        }

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG + Paint.FAKE_BOLD_TEXT_FLAG
            textAlign = Paint.Align.CENTER
            fontMetrics.bottom
            color = Color.GREEN
        }
        valueTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
            fontMetrics.bottom
            color = selectColor
        }

        labelTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG + Paint.FAKE_BOLD_TEXT_FLAG
            textAlign = Paint.Align.CENTER
            fontMetrics.bottom
            color = Color.BLUE
        }
    }

    /**
     * After viewCentre or zoom change reset the virtual gauge position.
     */

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

       val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        pxlPerFloat = width/tapeSegment.virtyWidth
       var  selectedValuePos = ((selectedValue - tapeSegment.portMin)/tapeSegment.virtyWidth)*contentWidth

        var gap = (contentWidth/tapeSegment.numSegments )as Float
        var heightF = contentHeight*1f

        textPaint.textSize = (0.15f * height)
        valueTextPaint.textSize = 0.25f*heightF

        labelTextPaint.textSize = 0.2f*heightF
        val rectLength = 1f*parameterName.length * labelTextPaint.textSize
        val rectHeight = (1.35f* labelTextPaint.textSize)

        canvas.drawRoundRect(RectF(paddingLeft*1f, paddingTop*1f,
            paddingLeft + rectLength,paddingTop +rectHeight  ),
            1f*rectLength ,1f*rectHeight,paintUnselected)
        canvas.drawText(parameterName,paddingLeft + 0.5f*rectLength, paddingTop + 0.8f*rectHeight, labelTextPaint)

        for (i in 1..(tapeSegment.numSegments-1).toInt()) {
            canvas.drawLine(paddingLeft + i*gap, paddingTop*1f +rectHeight, paddingLeft + i*gap, 0.5f*heightF, paintUnselected)

            (tapeSegment.portMin + i*tapeSegment.virtyWidth/tapeSegment.numSegments).let {
                // Draw the text.
                val ypos:Float = if(i%2 ==1) (paddingTop + 0.8f*heightF) else (paddingTop+0.65f*heightF)
                canvas.drawText(
                    "%.8f".format(it),
                    paddingLeft + i*gap,
                    ypos,
                    textPaint
                )
            }
        }
        canvas.drawLine(
            selectedValuePos.toFloat(), (.25*heightF).toFloat(), selectedValuePos.toFloat(),
            (.75*heightF).toFloat(), paintSelected)
        canvas.drawText(selectedValue.toString(),
            selectedValuePos.toFloat(), 0.4f*heightF,valueTextPaint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
           // MotionEvent.ACTION_MOVE -> {touchMove(event) }
            MotionEvent.ACTION_DOWN -> touchStart(event)
            MotionEvent.ACTION_UP -> touchUp(event)
          //  MotionEvent.ACTION_SCROLL -> touchScroll(event)
            else -> return false
        }
        return true
    }

    private fun touchScroll(event: MotionEvent) {
        Log.i("touchScroll","scrolly")
    }


    private fun touchUp(event: MotionEvent){
        if(motionStartTouchEventX == -1f || motionStartTouchEventY == -1f){
            return //not a proper touch DOWN / UP sequence
        }
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        val distanceX = event.x - motionStartTouchEventX
        val distanceY = event.y - motionStartTouchEventY

        val shiftOnly = distanceX.absoluteValue > distanceY.absoluteValue
        if(shiftOnly && (distanceX.absoluteValue > touchTolerance)) {
            selectedValue =
                (tapeSegment.portMin + motionTouchEventX * tapeSegment.virtyWidth / width).toDouble()
            //tapeSegment.shift(distanceX / pxlPerFloat)
        }else
            if(!shiftOnly && distanceY.absoluteValue > touchTolerance) {
                if (distanceY < 0f) {  /* up so zoom in */
                    tapeSegment.zoomIn (selectedValue)
                } else if (distanceY > 0f) {  /* down so zoom out */
                    tapeSegment.zoomOut(selectedValue)
                }
            }
        else {
                selectedValue =
                    (tapeSegment.portMin + motionTouchEventX * tapeSegment.virtyWidth / width).toDouble()
            }

        motionStartTouchEventX = -1f
        motionStartTouchEventY = -1f
        invalidate()
    }

    private fun touchMove(event: MotionEvent) {
        val distanceX = event.x - motionStartTouchEventX
        val distanceY = event.y - motionStartTouchEventY

        val shiftOnly = distanceX.absoluteValue > distanceY.absoluteValue
       if(shiftOnly && (distanceX.absoluteValue > touchTolerance)) {
           selectedValue =
               (tapeSegment.portMin + motionTouchEventX * tapeSegment.virtyWidth / width).toDouble()
         //  tapeSegment.shift(distanceX / pxlPerFloat)
         //  didShift=true
        }
        return
    }

    private fun touchStart(event: MotionEvent) {
        motionStartTouchEventX = event.x
        motionStartTouchEventY = event.y

        return
    }


}