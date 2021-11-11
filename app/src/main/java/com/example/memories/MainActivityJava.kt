package com.example.memories

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.os.Bundle
import android.view.View
import com.example.memories.R
import android.widget.TextView
import android.view.ViewGroup
import android.widget.ImageView

class MainActivityJava : AppCompatActivity() {
    private var gamePiece: ConstraintLayout? = null
    private var nbImg: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_piece)
        gamePiece = findViewById<View>(R.id.MyDynamicLayout) as ConstraintLayout
        val text = TextView(this)
        text.text = "Trouvez les paires"
        text.textSize = 25f
        gamePiece!!.addView(text)
        nbImg = 4
        for (i in 0 until nbImg!!) {
            val image = ImageView(this)
            val params: ViewGroup.LayoutParams = ActionBar.LayoutParams(100, 100)
            image.layoutParams = params
            image.setBackgroundResource(R.drawable.plus)
            gamePiece!!.addView(image)
        }
    }
}