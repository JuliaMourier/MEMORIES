package com.example.memories.Database

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.jjoe64.graphview.GraphView
import android.widget.Toast

import com.jjoe64.graphview.series.DataPoint

import com.jjoe64.graphview.series.LineGraphSeries
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import java.lang.IllegalArgumentException


class ResultsGameAdapter : AppCompatActivity(){
    var realm : Realm? = null; //realm (mongodb)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set content to see graph of results
        setContentView(com.example.memories.R.layout.games_results)

        Realm.init(this);//Initialisation to the realm
        //COnfiguration : name of the new data base
        var config : RealmConfiguration = RealmConfiguration.Builder().name("RealmDate.realm").deleteRealmIfMigrationNeeded().build() //.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(config)
        //get the instance of the realm
        realm = Realm.getDefaultInstance()

        //show the graph of results
        makeDataPretty()

        //Set the onclick listener to get back to the activity xhere to add data
        var addButton: Button = findViewById(com.example.memories.R.id.addbutton)
        addButton.setOnClickListener(View.OnClickListener { view ->
            var intent : Intent = Intent(this, DatabaseActivity::class.java)
            startActivity(intent)
        })
    }

    //Recover and display the data from the database
    fun makeDataPretty(){

        // Build the query looking at all users:
        val query = realm?.where<GamesData>()

        if (query != null) {
            var i = 0
            try { //get the graphView
                var graphView : GraphView = findViewById(com.example.memories.R.id.graph)
                //Create empty series
                val series: LineGraphSeries<DataPoint> = LineGraphSeries()

                for(game in query.findAll()){ //get through the database
                    //add the data in the graph series
                    series.appendData(DataPoint(i++.toDouble(), game.getGameData_nbTry().toString().toDouble()), true,100)
                }

                //COmplete the graph xith the series filled
                graphView.addSeries(series)
            } catch (e: IllegalArgumentException) {
                //If trouble toast the user
                Toast.makeText(this@ResultsGameAdapter, e.message, Toast.LENGTH_LONG).show()
            }


        }
    }

    //When activity stops : stop the realm
    override fun onStop() {
        super.onStop()
        realm?.close()

    }
}