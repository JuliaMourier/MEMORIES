package com.example.memories.Database

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memories.NumberCardActivity
import com.example.memories.QRCode.QRCodeScanner
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

        //userId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
        // Get data from SharedPreferences
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        if(!sharedPreferences.contains("userID")){
            Toast.makeText(this, "Vous devez scanner le code du téléphone dont vous voulez récupérer les résultats", Toast.LENGTH_LONG).show()
            //Send to scan QRCODE activity :
            var intent : Intent = Intent(this, QRCodeScanner::class.java)
            startActivity(intent)
        }
        //set content to see graph of results
        setContentView(com.example.memories.R.layout.games_results)
        userId = sharedPreferences.getString("userID","").toString()
        Log.d("userid :", userId)
        Toast.makeText(this, userId,
            Toast.LENGTH_LONG).show()
        recoverListOfGames(userId)

    }

    fun recoverListOfGames(user : String){
        database.child("users").child(userId).child("games").get().addOnSuccessListener {

            for (gameData in it.children) {
                var go: GameObject = GameObject()

                for (info in gameData.children) {
                    if (info.key == "date") { //get the date
                        go.setMDate(info.value.toString())
                    }
                    if (info.key == "nbOfTries") { //Get the number of tries
                        go.setMNbTry(info.value.toString())
                    }
                    if (info.key == "duration") { //Get duration
                        go.setMDuration(info.value.toString())
                    }
                }
                gamesList.add(go) //Add the game to the list

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

            for (gameItem in gamesList) { //get through the list of games

                //add the data in the graph series
                if (gameItem.getMNbTry()
                        .toString() != null && (gameItem.getMDuration() != null)
                ) {
                    //Add the data of the number of try by filling a serie
                    series.appendData(
                        DataPoint(
                            i.toDouble(),
                            gameItem.getMNbTry().toString().toDouble()
                        ), true, 10
                    )
                    //Add the data of the duration by filling a serie

                    seriesduration.appendData(
                        DataPoint(
                            i++.toDouble(),
                            gameItem.getMDuration().toString().toDouble()
                        ), true, 10
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


