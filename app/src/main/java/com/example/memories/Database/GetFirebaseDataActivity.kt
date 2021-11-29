package com.example.memories.Database

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import java.lang.IllegalArgumentException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase
import okhttp3.Response


class GetFirebaseDataActivity : AppCompatActivity() {
    var database : DatabaseReference = Firebase.database.reference
    var userId = ""
    var gamesList : ArrayList<GameObject> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set content to see graph of results
        setContentView(com.example.memories.R.layout.games_results)
        userId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();

        recoverListOfGames(userId)


        //Set the onclick listener to get back to the activity xhere to add data
        var addButton: Button = findViewById(com.example.memories.R.id.addbutton)
        addButton.setOnClickListener(View.OnClickListener { view ->
            var intent: Intent = Intent(
                this, FirebaseActivity
                ::class.java
            )
            startActivity(intent)
        })
    }

    fun recoverListOfGames(user : String){
        database.child("users").child(userId).child("games").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.childrenCount}")
            Log.d(it.children.toString(), "firebase ouiiii")
            var go: GameObject = GameObject()
            for (gameData in it.children) {
                for (info in gameData.children) {
                    if (info.key == "date") {
                        go.setMDate(info.value.toString())
                    }
                    if (info.key == "nbOfTries") {
                        go.setMNbTry(info.value.toString())
                    }
                    if (info.key == "duration") {
                        go.setMDuration(info.value.toString())
                    }
                    Log.d(info.toString(), "firebase children of children")
                }
                gamesList.add(go)

            }
            //show the graph of results
            makeDataPretty()
            // Registering the Kotlin module with the ObjectMpper instance
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }

    //Recover and display the data from the database
    fun makeDataPretty() {

        try { //get the graphViews
            var graphView: GraphView =
                findViewById(com.example.memories.R.id.graph) //for the number of tries
            var graphDuration: GraphView = findViewById(com.example.memories.R.id.graph2)

            //Create empty series
            val series: LineGraphSeries<DataPoint> = LineGraphSeries()
            val seriesduration: LineGraphSeries<DataPoint> = LineGraphSeries()
            var i = 0
            Log.d(gamesList.size.toString(), "firebase size")

            for (game in gamesList) { //get through the database*
                Log.d(game.toString(), "firebase children of children")

                //add the data in the graph series
                if (game.getMNbTry()
                        .toString() != null && (game.getMDuration() != null)
                ) {
                    series.appendData(
                        DataPoint(
                            i.toDouble(),
                            game.getMNbTry().toString().toDouble()
                        ), true, 100
                    )
                    seriesduration.appendData(
                        DataPoint(
                            i++.toDouble(),
                            game.getMDuration().toString().toDouble()
                        ), true, 100
                    )
                }
            }

            //Complete the graphs with the series filled
            graphView.addSeries(series)
            graphDuration.addSeries(seriesduration)
        } catch (e: IllegalArgumentException) {
            //If trouble toast the user
            Toast.makeText(this@GetFirebaseDataActivity, e.message, Toast.LENGTH_LONG).show()
        }


    }
}


