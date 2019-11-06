package com.omfgdevelop.spanwithbackground

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stringBuilder: SpannableStringBuilder
        val xmlParser = XmlParser(this,tv_span.textSize, linkColor = Color.parseColor("#3263a8"))
       stringBuilder = xmlParser.parse("<data>" +
               "    Системы регуляции" +" <em color=\"#32a852\">организма</em>" +" работают " +"<s>нормально</s>. Напряжены, но все под контролем. Нужно постараться, что бы " +
               "<a href=\"https://google.com\">довести</a>"+"<br/> себя до хранического "+"<b>стресса</b> или простуды из такого состояния."+
               "</data>")!!
        tv_span.setText(stringBuilder, TextView.BufferType.SPANNABLE)
        tv_span.movementMethod = LinkMovementMethod.getInstance()

    }
}
