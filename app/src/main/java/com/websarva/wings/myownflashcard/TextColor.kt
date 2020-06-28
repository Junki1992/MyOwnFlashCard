package com.websarva.wings.myownflashcard

import android.app.Application

class TextColor: Application() {
    var color: Int = R.color.color01

    companion object {
        private var instance : TextColor? = null

        private fun getInstance(): TextColor {
            if (instance == null)
                instance = TextColor()

            return instance!!
        }

        fun getColor(): Int {
            return getInstance().color
        }

        fun setColor(color: Int) {
            getInstance().color = color
        }
    }
}