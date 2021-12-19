package com.example.memories.Database

import android.content.Context
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialisation of firebase
        Firebase.initialize(this)
        //If the user doesn't exits => Create one
        if(database.child("users").child(userID) == null){ //if user do
            writeNewUser(userID, userID)
        }
    }

    fun writeNewUser(userId: String, phoneId: String) {
        val user = User(phoneId)
        database.child("users").child(userId).setValue(user)
    }

    fun writeNewGame(gameId: String, userId: String, date: String, duration: String, nbOfTries: String) {
        val game = Game(date, duration, nbOfTries)
        database.child("users").child(userId).child("games").child(gameId).setValue(game)
    }

    fun writeNewGame(userId: String, date: String, duration: String, nbOfTries: String) {
        val game = Game(date, duration, nbOfTries)
        database.child("users").child(userId).child("games").child(date).setValue(game)
    }

    fun writeNewGameFromGameActivity(context: Context, duration: String, nbOfTries: String){
        var db = Firebase.database.reference
        Firebase.initialize(context)

        val userId : String = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
        if(database.child("users").child(userId) == null){ //if user do exists
            writeNewUser(userId, userId)
        }
        val date : String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) + " " + SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()); //get the date of the submitting game
        val game = Game(date, duration, nbOfTries)
        db.child("users").child(userId).child("games").child(date).setValue(game)
    }

}