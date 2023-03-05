package com.example.mycustomclock

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class MyClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var padding = 0
    private var centreX = 0f
    private var centreY = 0f
    private var minDimension = 0f
    private var radius = 0f
    private var angle = (Math.PI / 30) - (Math.PI / 2)

    private val paint = Paint()
    private val rect = Rect()

    private var hourHandSize = 0f
    private var handSize = 0f
    private val numbers: IntArray = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

    private var hours = 0
    private var minutes = 0
    private var seconds = 0
    private var scale = 1f

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        centreX = width / 2f
        centreY = height / 2f
        minDimension = centreX.coerceAtMost(centreY)

        padding = (minDimension / 6).toInt()
        radius = minDimension - padding
        hourHandSize = radius - radius / 2
        handSize = radius - radius / 4
        scale = minDimension / 394
//        setLayerType(LAYER_TYPE_SOFTWARE, mPaint)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        drawCircle(canvas)
        drawHands(canvas)
        drawNumerals(canvas)
        postInvalidateDelayed(1 / 60 * 10)
    }

    private fun drawCircle(canvas: Canvas?) {
        paint.reset()
//        mPaint.setShadowLayer(120f, 0f, 0f, Color.BLACK)
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, (40 * scale).toInt())
        canvas?.drawCircle(centreX, centreY, radius, paint)
    }

    private fun drawHands(canvas: Canvas) {
        val calendar: Calendar = Calendar.getInstance()
        hours = calendar.get(Calendar.HOUR_OF_DAY)
        hours = if (hours > 12) hours - 12 else hours
        minutes = calendar.get(Calendar.MINUTE)
        seconds = calendar.get(Calendar.SECOND)
        drawHourHand(canvas, (hours + minutes / 60.0) * 5f)
        drawMinuteHand(canvas, minutes)
        drawSecondsHand(canvas, seconds)
        drawDots(canvas)
    }

    private fun drawHourHand(canvas: Canvas, loc: Double) {
        paint.reset()
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, (30 * scale).toInt())
        angle = Math.PI * loc / 30 - Math.PI / 2
        canvas.drawLine(
            (centreX - cos(angle) * hourHandSize / 3).toFloat(),
            (centreY - sin(angle) * hourHandSize / 3).toFloat(),
            (centreX + cos(angle) * hourHandSize).toFloat(),
            (centreY + sin(angle) * hourHandSize).toFloat(),
            paint
        )
    }

    private fun drawMinuteHand(canvas: Canvas, loc: Int) {
        paint.reset();
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, (15 * scale).toInt());
        angle = Math.PI * loc / 30.0 - Math.PI / 2;
        canvas.drawLine(
            (centreX - cos(angle) * handSize / 3).toFloat(),
            (centreY - sin(angle) * handSize / 3).toFloat(),
            (centreX + cos(angle) * handSize).toFloat(),
            (centreY + sin(angle) * handSize).toFloat(),
            paint
        );
    }

    private fun drawSecondsHand(canvas: Canvas, loc: Int) {
        paint.reset()
        setPaintAttributes(Color.RED, Paint.Style.STROKE, (5 * scale).toInt())
        angle = Math.PI * loc / 30.0 - Math.PI / 2
        canvas.drawLine(
            (centreX - cos(angle) * handSize / 3).toFloat(),
            (centreY - sin(angle) * handSize / 3).toFloat(),
            (centreX + cos(angle) * handSize).toFloat(),
            (centreY + sin(angle) * handSize).toFloat(),
            paint
        )
    }

    private fun drawDots(canvas: Canvas) {
        paint.reset()
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, (5 * scale).toInt())
        for (loc in 0..59) {
            angle = Math.PI * loc / 30.0 - Math.PI / 2
            if (loc % 5 == 0) paint.strokeWidth = 10f * scale
            canvas.drawPoint(
                (centreX + cos(angle) * handSize / 0.82).toFloat(),
                (centreY + sin(angle) * handSize / 0.82).toFloat(),
                paint
            )
            paint.strokeWidth = 5f * scale
        }
        canvas.drawCircle(centreX, centreY, radius / 100, paint.apply { color = Color.RED })
    }

    private fun drawNumerals(canvas: Canvas) {
        paint.reset()
        paint.textSize = 90F * scale //mFontSize
        paint.isElegantTextHeight
        for (number in numbers) {
            val num = number.toString()
            paint.getTextBounds(num, 0, num.length, rect)
            val angle = Math.PI / 6 * (number - 3)
            val x = (centreX + cos(angle) * radius * 0.75 - rect.width() / 2).toInt()
            val y = (centreY + sin(angle) * radius * 0.75 + rect.height() / 2).toInt()
            canvas.drawText(num, x.toFloat(), y.toFloat(), paint)
        }
    }

    private fun setPaintAttributes(colour: Int, stroke: Paint.Style, strokeWidth: Int) {
        paint.reset()
        paint.color = colour
        paint.style = stroke
        paint.strokeWidth = strokeWidth.toFloat()
        paint.isAntiAlias = true
    }
}