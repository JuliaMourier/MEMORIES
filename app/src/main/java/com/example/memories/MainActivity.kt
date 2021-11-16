package com.example.memories

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.os.Bundle
import android.os.PersistableBundle
import android.util.DisplayMetrics
import android.view.View
import com.example.memories.R
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginLeft
import com.example.memories.R.drawable.*

class MainActivity : AppCompatActivity() {
    private var gamePiece: GridLayout? = null
    private var nbImg: Int? = null

    var density = 0f
    var dpHeight:kotlin.Float = 0f
    var dpWidth:kotlin.Float = 0f
    var cardSize:Int = 0
    var columnCount:Int = 0

    val images: MutableList<Int> = mutableListOf(chat, chien, famille1, famille2,chat, chien, famille1, famille2)

    var buttonsArray = arrayOf<Button>()

    var clicked= 0
    var turnOver= false
    var lastClicked= -1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.game_piece)
        gamePiece = findViewById<View>(R.id.MyDynamicLayout) as GridLayout
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        density = resources.displayMetrics.density
        dpHeight = outMetrics.heightPixels / density
        dpWidth = outMetrics.widthPixels / density
        columnCount = 5

        cardSize = (outMetrics.widthPixels / columnCount) as Int
        val paramsFrameLayout: ViewGroup.LayoutParams =
            ActionBar.LayoutParams(cardSize, cardSize)

        nbImg = 8

        var cardBack = questionpoint


        images.shuffle()

        for (i in 0 until nbImg!!) {
            val btn_img = Button(this)
            val params: ViewGroup.LayoutParams = ActionBar.LayoutParams(paramsFrameLayout)
            btn_img.layoutParams=params

            btn_img.setBackgroundResource(questionpoint)

            buttonsArray = append(buttonsArray,btn_img)

            gamePiece!!.addView(btn_img)
        }



        for (i in 0 until nbImg!!){
            buttonsArray[i].setBackgroundResource(cardBack)
            buttonsArray[i].text = "cardBack"
            buttonsArray[i].textSize = 0.0F
            buttonsArray[i].setOnClickListener {
                if (buttonsArray[i].text == "cardBack" && !turnOver) {
                    buttonsArray[i].setBackgroundResource(images[i])
                    buttonsArray[i].setText(images[i])
                    if (clicked == 0) {
                        lastClicked = i
                    }
                    clicked++
                } else if (buttonsArray[i].text !in "cardBack") {
                    buttonsArray[i].setBackgroundResource(cardBack)
                    buttonsArray[i].text = "cardBack"
                    clicked--
                }

                if (clicked == 2) {
                    turnOver = true
                    if (buttonsArray[i].text == buttonsArray[lastClicked].text) {
                        buttonsArray[i].isClickable = false
                        buttonsArray[lastClicked].isClickable = false
                        turnOver = false
                        clicked = 0
                    }
                } else if (clicked == 0) {
                    turnOver = false
                }
            }

        }


    }

    fun append(arr: Array<Button>, element: Button): Array<Button> {
        val list: MutableList<Button> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putSerializable("buttonsArray", buttonsArray)
        savedInstanceState.putInt("clicked", clicked)
        savedInstanceState.putInt("lastClicked", lastClicked)
        savedInstanceState.putBoolean("turnover", turnOver)
        super.onSaveInstanceState(savedInstanceState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        clicked = savedInstanceState.getInt("clicked")
        lastClicked = savedInstanceState.getInt("lastClicked")
        turnOver = savedInstanceState.getBoolean("turnOver")
        buttonsArray = savedInstanceState.getSerializable("buttonsArray") as Array<Button>
    }


}