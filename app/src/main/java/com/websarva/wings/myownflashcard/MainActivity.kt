package com.websarva.wings.myownflashcard

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

var intBackgroundColor = 0

class MainActivity : AppCompatActivity() {

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ボタンのクリック処理
        buttonEdit.setOnClickListener {
            val intent = Intent(this@MainActivity, WordListActivity::class.java)
            startActivity(intent)
        }

        //「色」ボタンを押した場合
        //画面の背景色と文字色をボタンの色に設定
        button01.setOnClickListener {
            intBackgroundColor = R.color.color01
            ConstraintLayoutMain.setBackgroundResource(intBackgroundColor)

            TextColor.setColor(R.color.color06)
            val textView = findViewById<TextView>(R.id.textView)
            textView.setTextColor(ContextCompat.getColor(applicationContext,TextColor.getColor()))
            radioButton.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
            radioButton2.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
        }
        button02.setOnClickListener {
            intBackgroundColor = R.color.color02
            ConstraintLayoutMain.setBackgroundResource(intBackgroundColor)

            TextColor.setColor(R.color.color06)
            val textView = findViewById<TextView>(R.id.textView)
            textView.setTextColor(ContextCompat.getColor(applicationContext,TextColor.getColor()))
            radioButton.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
            radioButton2.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
        }
        button03.setOnClickListener {
            intBackgroundColor  = R.color.color03
            ConstraintLayoutMain.setBackgroundResource(intBackgroundColor)

            TextColor.setColor(R.color.color06)
            val textView = findViewById<TextView>(R.id.textView)
            textView.setTextColor(ContextCompat.getColor(applicationContext,TextColor.getColor()))
            radioButton.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
            radioButton2.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
        }
        button04.setOnClickListener {
            intBackgroundColor  = R.color.color04
            ConstraintLayoutMain.setBackgroundResource(intBackgroundColor)

            TextColor.setColor(R.color.color06)
            val textView = findViewById<TextView>(R.id.textView)
            textView.setTextColor(ContextCompat.getColor(applicationContext,TextColor.getColor()))
            radioButton.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
            radioButton2.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
        }
        button05.setOnClickListener {
            intBackgroundColor  = R.color.color05
            ConstraintLayoutMain.setBackgroundResource(intBackgroundColor)

            TextColor.setColor(R.color.color06)
            val textView = findViewById<TextView>(R.id.textView)
            textView.setTextColor(ContextCompat.getColor(applicationContext,TextColor.getColor()))
            radioButton.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
            radioButton2.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
        }
        button06.setOnClickListener {
            intBackgroundColor  = R.color.color06
            ConstraintLayoutMain.setBackgroundResource(intBackgroundColor)

            TextColor.setColor(R.color.color02)
            val textView = findViewById<TextView>(R.id.textView)
            textView.setTextColor(ContextCompat.getColor(applicationContext,TextColor.getColor()))
            radioButton.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
            radioButton2.setTextColor(ContextCompat.getColor(applicationContext, TextColor.getColor()))
        }

        //「かくにんテスト」ボタンを押した場合
        buttonTest.setOnClickListener {
            //テスト画面（TestActivity）へ
            //選択したテスト条件をIntentで渡す
            val intent = Intent(this@MainActivity, TestActivity::class.java)
            when (radioGroup.checkedRadioButtonId) {
                //暗記済の単語を除外する場合
                R.id.radioButton -> intent.putExtra(getString(R.string.intent_key_memory_frag), true)
                //暗記済の単語を含める（=除外しない）場合
                R.id.radioButton2 -> intent.putExtra(getString(R.string.intent_key_memory_frag), false)
            }
            startActivity(intent)
        }
    }
}
