package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var radius = 0
    private var oval: RectF
    private var rectWidth = 0f
    private var fillAngle = 0f
    private var text = ""
    private var valueAnimator = ValueAnimator()
    private var downloadingText = context.getString(R.string.downloadingText)
    private var isComplete = false
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        style = Paint.Style.FILL
    }
    private val circleFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
        style = Paint.Style.FILL
        strokeWidth = 10f
    }
    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        textSize = 60f
        color = context.getColor(R.color.white)
        //typeface = Typeface.create("", Typeface.BOLD)
    }
    private val buttonPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        style = Paint.Style.FILL
    }
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                animateButton()
                valueAnimator.start()
                invalidate()
            }
            ButtonState.Clicked -> println("clicked")
            ButtonState.Completed -> {
                isComplete = false
                valueAnimator.cancel()
            }
        }
    }

    init {
        oval = RectF()
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            text = getString(R.styleable.LoadingButton_buttonText).toString()
            isComplete = getBoolean(R.styleable.LoadingButton_isComplete, false)
        }
    }

    fun startDownload() {
        buttonState = ButtonState.Loading
    }

    fun finishDownload() {
        isComplete = true
    }

    /* override fun performClick(): Boolean {
         super.performClick()
         buttonState = ButtonState.Clicked
         return true
     }*/

    fun animateButton() {
        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
            duration = 10000
            addUpdateListener { valueAnim ->
                val fraction = valueAnim.animatedFraction
                if (isComplete){
                    valueAnim.duration = duration - 200
                }
                rectWidth = valueAnim.animatedValue as Float
                fillAngle = rectWidth / widthSize * 360
                if(rectWidth == widthSize.toFloat()){
                    buttonState = ButtonState.Completed
                }
                invalidate()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        radius = 35
        widthSize = w
        heightSize = h
        oval.set(
            widthSize.toFloat() * 0.75f,
            heightSize.toFloat() / 2 - radius,
            widthSize.toFloat() * 0.75f + (2 * radius),
            heightSize.toFloat() / 2 + radius
        )
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (buttonState == ButtonState.Loading) {
            canvas?.let {
                it.drawRect(0f, 165f, rectWidth.toFloat(), 0f, buttonPaint)
                it.drawText(
                    downloadingText,
                    (widthSize / 2).toFloat(),
                    (heightSize / 2 * 1.2).toFloat(),
                    textPaint
                )
                it.drawArc(oval, 0f, fillAngle.toFloat(), true, circleFillPaint)
            }
        } else canvas?.drawText(
            text,
            (width / 2).toFloat(),
            (heightSize / 2 * 1.2).toFloat(),
            textPaint
        )
    }
}
