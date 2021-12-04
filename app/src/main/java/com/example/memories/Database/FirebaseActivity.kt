package com.example.memories.Database

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.memories.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.*


class FirebaseActivity : AppCompatActivity() {
    //var mRef : Firebase = Firebase.database("https://memories-8cc20-default-rtdb.firebaseio.com/")
    var database = Firebase.database.reference
    var userID = ""
    //val userID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    var duration: TextView? = null //Text view to get the duraction entry
    var numberOfTry: TextView? = null //and the number of try for the game
    var dateAndTime : String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) + " " + SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
        Date()
    ); //get the date of the submitting game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.idtest)
        userID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();

        val idtext: TextView = findViewById(R.id.idText)
        idtext.text = userID.toUpperCase()

        Firebase.initialize(this)
        if(database.child("users").child(userID) == null){ //if user do
            writeNewUser(userID, userID)
        }
        numberOfTry = findViewById(R.id.numberOfTry)
        duration = findViewById(R.id.timeText)
        var buttonSave : Button = findViewById(R.id.button)

        buttonSave.setOnClickListener(View.OnClickListener { view ->
            var tmpDate : String = dateAndTime.replace("/","-")
            writeNewGame(tmpDate, userID, dateAndTime, numberOfTry?.text.toString(),duration?.text.toString())
        })

        var resultButton: Button = findViewById(R.id.results)
        resultButton.setOnClickListener(View.OnClickListener { view ->
            var intent : Intent = Intent(this, GetFirebaseDataActivity::class.java)
            startActivity(intent)
        })

    }

    fun writeNewUser(userId: String, phoneId: String) {
        val user = User(phoneId)
        database.child("users").child(userId).setValue(user)
    }

    fun writeNewGame(gameId: String, userId: String, date: String, duration: String, nbOfTries: String) {
        val game = Game(date, duration, nbOfTries)
        database.child("users").child(userId).child("games").child(gameId).setValue(game)
    }

}