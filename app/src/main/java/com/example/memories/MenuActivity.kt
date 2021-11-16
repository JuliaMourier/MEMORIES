package com.example.memories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.memories.Database.ResultsGameAdapter
import com.example.memories.R

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainmenu)

        var playButton : Button = findViewById(R.id.main_menu_play_b)
        var photosButton : Button = findViewById(R.id.main_menu_photos_b)
        var resultsButton : Button = findViewById(R.id.main_menu_result_b)

        playButton.setOnClickListener(View.OnClickListener { view ->
            var intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })

        photosButton.setOnClickListener(View.OnClickListener { view ->
            var intent : Intent = Intent(this, ShowFoldersActivity::class.java)
            startActivity(intent)
        })

        resultsButton.setOnClickListener(View.OnClickListener { view ->
            var intent : Intent = Intent(this, ResultsGameAdapter::class.java)
            startActivity(intent)
        })
    }

}