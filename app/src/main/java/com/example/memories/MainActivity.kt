package com.example.memories

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.os.Bundle
import android.view.View
import com.example.memories.R
import android.view.ViewGroup
import android.widget.*

class MainActivity : AppCompatActivity() {
    private var gamePiece: GridLayout? = null
    private var nbImg: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_piece)
        gamePiece = findViewById<View>(R.id.MyDynamicLayout) as GridLayout

        nbImg = 16



        for (i in 0 until nbImg!!) {
            val image = ImageView(this)
            val params: ViewGroup.LayoutParams = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            image.layoutParams=params
            image.setBackgroundResource(R.drawable.plus)
            gamePiece!!.addView(image)
        }


    }
}