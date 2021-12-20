package com.example.memories

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class NumberCardActivity : AppCompatActivity() {

    private var nbCards: Int? = 8


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choicenumbercard)


        var minusButton : ImageButton = findViewById(R.id.choice_number_menu_minus_b)
        var plusButton : ImageButton = findViewById(R.id.choice_number_menu_plus_b)
        var nbCardsView : TextView = findViewById(R.id.choice_number_menu_number_n)
        var validationButton : Button = findViewById(R.id.choice_number_menu_validate_b)
        nbCardsView.setText(nbCards.toString());

        val intent = Intent(this@NumberCardActivity, ShowFoldersActivity::class.java)




        minusButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (nbCards == 8){
                    Toast.makeText(applicationContext,"Nombre de cartes minimal atteint",Toast.LENGTH_SHORT).show()
                }else {
                    nbCards = nbCards?.minus(2)
                    nbCardsView.setText(nbCards.toString());
                }
            }})

        plusButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (nbCards == 18){
                    Toast.makeText(applicationContext,"Nombre de cartes maximal atteint",Toast.LENGTH_SHORT).show()
                }else {
                    nbCards = nbCards?.plus(2)
                    nbCardsView.setText(nbCards.toString());
                }
            }})

        validationButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                intent.putExtra("nbCards", nbCards)
                intent.putExtra("selectionMode",true);
                startActivity(intent)
            }})


    }
}