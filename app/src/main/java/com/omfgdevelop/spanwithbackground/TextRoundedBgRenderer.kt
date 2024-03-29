/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.omfgdevelop.spanwithbackground

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Base class for single and multi line rounded background renderers.
 *
// * @param horizontalPadding the padding to be applied to left & right of the background
// * @param verticalPadding the padding to be applied to top & bottom of the background
 */
internal abstract class TextRoundedBgRenderer(
//    val horizontalPadding: Int,
//    val verticalPadding: Int,
    private val fontSize: Float,
    private val context: Context
) {

    companion object val extraPadding = dp(2)

    private fun dp(int: Int): Float {
        return context.resources.displayMetrics.density* int
    }
    /**
     * Draw the background that starts at the {@code startOffset} and ends at {@code endOffset}.
     *
     * @param canvas Canvas to draw onto
     * @param layout Layout that contains the text
     * @param startLine the start line for the background
     * @param endLine the end line for the background
     * @param startOffset the character offset that the background should start at
     * @param endOffset the character offset that the background should end at
     */
    abstract fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        bgrColor: Int,
        extraSpace: Float
    )

    /**
     * Get the top offset of the line and add padding into account so that there is a gap between
     * top of the background and top of the text.
     *
     * @param layout Layout object that contains the text
     * @param line line number
     */
    protected fun getLineTop(layout: Layout, line: Int): Int {
        return layout.getLineTopWithoutPadding(line)  + ((fontSize * 2) / 3).roundToInt()
//        return layout.getLineTopWithoutPadding(line) - verticalPadding + ((fontSize * 2) / 3).roundToInt()
        //        return layout.getLineTopWithoutPadding(line) - verticalPadding
    }

    /**
     * Get the bottom offset of the line and add padding into account so that there is a gap between
     * bottom of the background and bottom of the text.
     *
     * @param layout Layout object that contains the text
     * @param line line number
     */
    protected fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottomWithoutPadding(line)
    }
}

/**
 * Draws the background for text that starts and ends on the same line.
 *
// * @param horizontalPadding the padding to be applied to left & right of the background
// * @param verticalPadding the padding to be applied to top & bottom of the background
// * @param drawable the drawable used to draw the background
 */
internal class SingleLineRenderer(
//    horizontalPadding: Int,
//    verticalPadding: Int,
//    val drawable: Drawable,
    fontSize: Float,
    context: Context
) : TextRoundedBgRenderer( fontSize,context) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        bgrColor: Int,
        extraSpace: Float


    ) {

        val lineTop = getLineTop(layout, startLine)
        val lineBottom = getLineBottom(layout, startLine)
        // get min of start/end for left, and max of start/end for right since we don't
        // the language direction
        val left = min(startOffset, endOffset)
        val right = max(startOffset, endOffset)
        val paint = Paint()
        paint.color = bgrColor
        canvas.drawRect(Rect(
            (left-extraPadding).toInt(), lineTop,
            (right+extraPadding+extraSpace).toInt(), lineBottom), paint)
    }
}

/**
 * Draws the background for text that starts and ends on different lines.
 *
// * @param horizontalPadding the padding to be applied to left & right of the background
// * @param verticalPadding the padding to be applied to top & bottom of the background
// * @param drawableLeft the drawable used to draw left edge of the background
// * @param drawableMid the drawable used to draw for whole line
// * @param drawableRight the drawable used to draw right edge of the background
 */
internal class MultiLineRenderer(


//    val drawableLeft: Drawable,
//    val drawableMid: Drawable,
//    val drawableRight: Drawable,
    fontSize: Float,
    context: Context
) : TextRoundedBgRenderer(  fontSize,context) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        bgrColor: Int,
        extraSpace: Float
    ) {
        // draw the first line
        val paragDir = layout.getParagraphDirection(startLine)
        val lineEndOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineLeft(startLine)
        } else {
            layout.getLineRight(startLine)
        }.toInt()
        var lineBottom = getLineBottom(layout, startLine)
        var lineTop = getLineTop(layout, startLine)
        drawStart(canvas, startOffset, lineTop, lineEndOffset, lineBottom, bgrColor)

        // for the lines in the middle draw the mid drawable
        for (line in startLine + 1 until endLine) {
            lineTop = getLineTop(layout, line)
            lineBottom = getLineBottom(layout, line)
            val paint = Paint()
            paint.color = bgrColor
            canvas.drawRect(Rect(layout.getLineLeft(line).toInt() , lineTop, layout.getLineRight(line).toInt(), lineBottom), paint)
        }
        val lineStartOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineRight(startLine)
        } else {
            layout.getLineLeft(startLine)
        }.toInt()
        // draw the last line
        lineBottom = getLineBottom(layout, endLine)
        lineTop = getLineTop(layout, endLine)
        drawEnd(canvas, lineStartOffset, lineTop, endOffset, lineBottom, bgrColor)

    }

    /**
     * Draw the first line of a multiline annotation. Handles LTR/RTL.
     *
     * @param canvas Canvas to draw onto
     * @param start start coordinate for the background
     * @param top top coordinate for the background
     * @param end end coordinate for the background
     * @param bottom bottom coordinate for the background
     */
    private fun drawStart(
        canvas: Canvas,
        start: Int,
        top: Int,
        end: Int,
        bottom: Int,
        bgrColor: Int
    ) {
        val paint = Paint()
        paint.color = bgrColor
        canvas.drawRect(Rect((start+extraPadding).toInt(), top, end, bottom), paint)
    }

    /**
     * Draw the last line of a multiline annotation. Handles LTR/RTL.
     *
     * @param canvas Canvas to draw onto
     * @param start start coordinate for the background
     * @param top top position for the background
     * @param end end coordinate for the background
     * @param bottom bottom coordinate for the background
     */
    private fun drawEnd(
        canvas: Canvas,
        start: Int,
        top: Int,
        end: Int,
        bottom: Int,
        bgrColor: Int
    ) {
        val paint = Paint()
        paint.color = bgrColor
        canvas.drawRect(Rect(start, top, (end+extraPadding).toInt(), bottom), paint)
    }


}