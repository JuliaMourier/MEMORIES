package com.example.memories

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.os.Bundle
<<<<<<< Updated upstream
=======
import android.os.PersistableBundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
>>>>>>> Stashed changes
import android.view.View
import com.example.memories.R
import android.view.ViewGroup
import android.widget.*
import com.example.memories.R.drawable.*

class MainActivity : AppCompatActivity() {
    private var gamePiece: GridLayout? = null
<<<<<<< Updated upstream
    private var nbImg: Int? = null
=======
    private var popUpView: ConstraintLayout? = null

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

>>>>>>> Stashed changes


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_piece)
        gamePiece = findViewById<View>(R.id.MyDynamicLayout) as GridLayout

        nbImg = 4

        val images: MutableList<Int> = mutableListOf(plus, minus, plus, minus)

        var buttonsArray = arrayOf<Button>()

        images.shuffle()

        for (i in 0 until nbImg!!) {
            val btn_img = Button(this)
            val params: ViewGroup.LayoutParams = ActionBar.LayoutParams(100,100)
            btn_img.layoutParams=params
            btn_img.setBackgroundResource(questionpoint)

            buttonsArray = append(buttonsArray,btn_img)

            gamePiece!!.addView(btn_img)
        }
        val cardBack = questionpoint
        var clicked = 0
        var turnOver = false
        var lastClicked = -1

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
                        createBigImgView(buttonsArray[i])

                    }
                } else if (clicked == 0) {
                    turnOver = false
                }
            }

        }


    }

    private fun createBigImgView(btn_img: Button) {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val big_image_view = inflater.inflate(R.layout.image_view_game,null)

        popUpView = findViewById<View>(R.id.image_big_view) as ConstraintLayout
        val params: ViewGroup.LayoutParams = ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        btn_img.layoutParams=params

        popUpView!!.addView(btn_img)

        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            big_image_view, // Custom view to show in popup window
            LinearLayout.LayoutParams.MATCH_PARENT, // Width of popup window
            LinearLayout.LayoutParams.MATCH_PARENT// Window height
        )

        popupWindow.elevation = 10.0F

        val buttonPopup = popUpView.findViewById<Button>(R.id.button_popup)

        // Set a click listener for popup's button widget
        buttonPopup.setOnClickListener{
            // Dismiss the popup window
            popupWindow.dismiss()
        }

        // Set a dismiss listener for popup window
        popupWindow.setOnDismissListener {
            Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
        }

        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(gamePiece)
        popupWindow.showAtLocation(
            gamePiece, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )


    }

    fun append(arr: Array<Button>, element: Button): Array<Button> {
        val list: MutableList<Button> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

}