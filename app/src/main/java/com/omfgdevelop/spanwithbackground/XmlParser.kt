package com.omfgdevelop.spanwithbackground

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import android.util.Log
import android.view.View
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader


class XmlParser(
    private val context: Context,
    private val linkColor: Int
) {
    companion object {
        private const val SHORT_WORD = 3
    }

    fun parse(parsableText: String): SpannableStringBuilder? {


        val xmlPullParser = initParser(parsableText)

        val finalSpannableString = SpannableStringBuilder()
        val chunk = TextChunk()
        var tag = String()
        val attr: HashMap<String, String>? = HashMap()
        while (xmlPullParser.eventType != XmlPullParser.END_DOCUMENT) {

            when (xmlPullParser.eventType) {

                XmlPullParser.START_TAG -> {
                    tag = xmlPullParser.name
                    for (i in 0 until xmlPullParser.attributeCount) {

                        attr?.put(
                            xmlPullParser.getAttributeName(i),
                            xmlPullParser.getAttributeValue(i)
                        )
                    }
                }

                XmlPullParser.TEXT -> {
                    chunk.text = xmlPullParser.text
                    chunk.tag = tag
                    chunk.attr = attr
                    finalSpannableString.append(tagDefiner(chunk))
                }
                XmlPullParser.END_TAG -> {
                    if (tag == "br")
                        finalSpannableString.append("\n")
                    finalSpannableString.append(" ")
                    tag = ""
                }
                else -> {
                }
            }
            xmlPullParser.next()

        }
        return finalSpannableString

    }

    private fun initParser(parsableText: String): XmlPullParser {
        val factory = XmlPullParserFactory
            .newInstance()
        factory.isNamespaceAware = true
        val xmlPullParser = factory.newPullParser()

        xmlPullParser.setInput(StringReader(parsableText))

        return xmlPullParser
    }

    private fun tagDefiner(chunk: TextChunk): SpannableStringBuilder {

        var sb = SpannableStringBuilder()
        when (chunk.tag) {
            "em" -> {
                sb = emSpan(sb, chunk)
            }
            "br" -> {
                sb.append("\n")
            }
            "s" -> {
                sb = strikeSpan(sb, chunk)
            }
            "b" -> {
                sb = boldSpan(sb, chunk)
            }
            "a" -> {
                sb = underlineSpan(sb, chunk)
            }
            else -> {
                sb.append(chunk.text ?: "")
            }
        }

        return sb

    }

    private fun emSpan(
        sb: SpannableStringBuilder,
        chunk: TextChunk

    ): SpannableStringBuilder {
        if ((chunk.text?.length ?: 0) < SHORT_WORD) {
            chunk.text = " " + chunk.text + "  "
        }
        chunk.text = " " + chunk.text + " "
        sb.append(chunk.text)
        sb.setSpan(
            SpannableBackground(chunk.attr?.get("color") ?: "#FFFFFF"),
            0,
            (chunk.text?.length) ?: 0,
            Spanned.SPAN_INTERMEDIATE
        )
        return sb
    }

    private fun strikeSpan(sb: SpannableStringBuilder, chunk: TextChunk): SpannableStringBuilder {
        sb.append(chunk.text)
        sb.setSpan(StrikethroughSpan(), 0, chunk.text?.length ?: 0, Spanned.SPAN_INTERMEDIATE)
        return sb
    }

    private fun boldSpan(sb: SpannableStringBuilder, chunk: TextChunk): SpannableStringBuilder {
        sb.append(chunk.text)
        sb.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            chunk.text?.length ?: 0,
            Spanned.SPAN_INTERMEDIATE
        )
        return sb
    }

    private fun underlineSpan(
        sb: SpannableStringBuilder,
        chunk: TextChunk
    ): SpannableStringBuilder {
        sb.append(chunk.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(chunk.attr?.get("href")))
                context.startActivity(intent)
            }

        }
        sb.setSpan(
            ForegroundColorSpan(linkColor),
            0,
            chunk.text?.length ?: 0,
            Spannable.SPAN_INTERMEDIATE
        )
        sb.setSpan(
            UnderlineSpan(),
            0,
            chunk.text?.length ?: 0,
            Spanned.SPAN_INTERMEDIATE
        )
        sb.setSpan(clickableSpan, 0, chunk.text?.length ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    data class TextChunk(
        var tag: String? = null,
        var text: String? = null,
        var attr: HashMap<String, String>? = null
    )


}