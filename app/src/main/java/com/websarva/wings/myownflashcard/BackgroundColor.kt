package com.websarva.wings.myownflashcard

import android.app.Application

class BackgroundColor: Application() {
    var color: Int = R.color.color02

    companion object {
        private var instance : BackgroundColor? = null

        private fun getInstance(): BackgroundColor {
            if (instance == null)
                instance = BackgroundColor()

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