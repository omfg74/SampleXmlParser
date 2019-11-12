package com.omfgdevelop.spanwithbackground

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text1 =
            "<data>" +
                    "    Системы регуляции" + " <em color=\"#32a852\">организма</em>" + " <em color=\"#32a852\">щдщ</em>" + " работают " + "<s>нормально</s>. Напряжены, но все под контролем. Нужно постараться, что бы " +
                    "<a href=\"https://google.com\">довести</a>" + "<br/> себя до хранического " + " <em color=\"#32a852\">щдщ</em>" + "<b>стресса</b> или простуды из такого состояния." + "<em color=\"#32a852\"> В родительский профиль ja;f j;kafjsd ;kfjas;kf adsjf dasj;f ksd;a jfsd;la" +
                    " fsda';fkasd kf'lsd;af ';sd не подгружается аватар  ( в шапке сверху есть, в</em>" +
                    "</data>"
        val text2 = "<em color=\"#32a852\">OK</em>"
        tv_span2.setText(setSpan(text1), TextView.BufferType.SPANNABLE)
        tv_spanOk.setText(setSpan(text2), TextView.BufferType.SPANNABLE)
    }

    private fun setSpan(text: String): SpannableStringBuilder {
        val stringBuilder: SpannableStringBuilder
        val xmlParser = XmlParser(this, linkColor = Color.parseColor("#3263a8"))
        stringBuilder = xmlParser.parse(text)!!
        return stringBuilder

    }
}
