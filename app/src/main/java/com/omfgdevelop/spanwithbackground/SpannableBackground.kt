package com.omfgdevelop.spanwithbackground

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt


class SpannableBackground(
    private val backgroundColor: String,
    private val cornerRadius: Float,
    private val textColor: String,
    private val textSize: Float
) : ReplacementSpan() {
    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val width = paint.measureText(text, start, end).roundToInt()
        val metrics = paint.fontMetricsInt
        if (fm != null) {
            fm.top = metrics.top
            fm.ascent = metrics.ascent
            fm.descent = metrics.descent

            fm.bottom = metrics.bottom
        }
        return width
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {

        val rect =
            RectF(
                x,
                (textSize*3)/4+ top.toFloat(),
                x + measureText(paint, text, start, end),
                bottom.toFloat()
            )
        paint.color = Color.parseColor(backgroundColor)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        paint.color = Color.parseColor(textColor)
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }

    private fun measureText(paint: Paint, text: CharSequence, start: Int, end: Int): Float {
        return paint.measureText(text, start, end)
    }
}