package com.example.memories.Database

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.memories.R
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where


import java.text.SimpleDateFormat
import java.util.*

//Activity for the database
class DatabaseActivity : AppCompatActivity() {
    var realm : Realm? = null; //realm (mongodb)
    //Temporary
    var duration: TextView? = null //Text view to get the duraction entry
    var numberOfTry: TextView? = null //and the number of try for the game
    var dateAndTime : String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) + " " + SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()); //get the date of the submitting game
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Realm.init(this);//Initialisation to the realm
        //COnfiguration : name of the new data base
        var config : RealmConfiguration = RealmConfiguration.Builder().name("RealmDate.realm").deleteRealmIfMigrationNeeded().build() //.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(config)

        //Set the view to the get the data by hand (Temporary)
        setContentView(R.layout.idtest)

        //Text view xhere to display the unique id
        val idtext: TextView = findViewById(R.id.idText)

        //Get the unique ID of the user's phone & display it
        val android_id: String =
            Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        idtext.text = android_id.toUpperCase()

        //text view to get manually the number of try and the duration for the game (temporary)
        numberOfTry = findViewById(R.id.numberOfTry)
        duration = findViewById(R.id.timeText)

        //save button to launch the saving of the data
        var buttonSave : Button = findViewById(R.id.button)

        buttonSave.setOnClickListener(View.OnClickListener { view ->
            saveData(android_id);
        })

        //get the instance of the realm
        realm = Realm.getDefaultInstance()

        recoverData() //recover and display the data just saved

        var resultButton: Button = findViewById(R.id.results)
        resultButton.setOnClickListener(View.OnClickListener { view ->
            var intent : Intent = Intent(this, ResultsGameAdapter::class.java)
            startActivity(intent)
        })
    }

    //Save the game data into the database
    fun saveData(id :String){
        //to save asyncronisly
        realm!!.executeTransactionAsync({ bgRealm ->
            //Create the primary key for the database id of the game (id correspond to its index in the database)
            val maxId = bgRealm.where(GamesData::class.java).max("gameData_id")
            val newKey = if (maxId == null) 1 else maxId.toInt() + 1
            //Create the realm object
            val game = bgRealm.createObject(GamesData::class.java, newKey)

            //Complete their data : user_id, date & nb of try
            game.setGameData_user(id)
            game.setGameData_date(dateAndTime)
            game.setGameData_nbTry(numberOfTry?.text.toString())
            game.setGameData_duration(duration?.text.toString())

        }, {
            //If the operation is a success :
            Toast.makeText(this,"Operation Succeeded",Toast.LENGTH_LONG)// Transaction was a success
            //Recover the data from the database and display it
            recoverData()
        }, { error ->
            // Transaction failed and was automatically canceled
            Log.d("a probem occured", "oui")
            Log.d(error.message, "oui");

            Toast.makeText(this, "Operation Failed",Toast.LENGTH_LONG)
        })

    }

    //Recover and display the data from the database
    fun recoverData(){
        // Build the query looking at all users:
        val query = realm?.where<GamesData>()
        var gameText : String = "" //Init the text for data
        if (query != null) {
            val dataText : TextView = findViewById(R.id.datatext)
            for(game in query.findAll()){ //construct a pretty text with all the element of a data game
                gameText += "\nGame at "
                gameText += game.getGameData_Date()
                gameText += "\n   essais : " + game.getGameData_nbTry()
                gameText += "\n   durée : " + game.getGameDataDuration()
                gameText += "\n   par : " + game.getGameData_user()
            }
            //Display the text in the layout
            dataText.text = gameText
        }
    }

    //When activity stops : stop the realm
    override fun onStop() {
        super.onStop()
        realm?.close()

    }
}