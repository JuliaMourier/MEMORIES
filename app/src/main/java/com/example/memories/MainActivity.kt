package com.example.memories

import android.app.ActionBar
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.memories.Database.FirebaseActivity
import com.example.memories.R.drawable.*
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


class MainActivity : AppCompatActivity() {
    private var gamePiece: GridLayout? = null

    var density = 0f
    var dpHeight:kotlin.Float = 0f
    var dpWidth:kotlin.Float = 0f
    var cardSize:Int = 0
    var columnCount:Int = 0

    val images: MutableList<Int> = mutableListOf(chat, chien, famille1, famille2,chat, chien, famille1, famille2)

    var buttonsArray = arrayOf<Button>()

    var clicked= 0
    var turnOver= false
    var lastClicked= -1

    //Chronometer and nb of tries
    var nbTries = 0
    private var seconds = 0 // number of seconds since launched
    private var running = false//is it running or not?

    //Value to verify win
    private var goodPairs = 0
    private var countOfCards = 0

    //Sound effect
    lateinit var mp : MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.game_piece)
        gamePiece = findViewById<View>(R.id.MyDynamicLayout) as GridLayout
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val nbCards = intent.getSerializableExtra("nbCards") as Int?
        countOfCards = nbCards!!

        //Media player
        mp = MediaPlayer.create(this, R.raw.winsound)
        density = resources.displayMetrics.density
        dpHeight = outMetrics.heightPixels / density
        dpWidth = outMetrics.widthPixels / density
        columnCount = 5

        cardSize = (outMetrics.widthPixels / columnCount) as Int
        val paramsFrameLayout: ViewGroup.LayoutParams =
            ActionBar.LayoutParams(cardSize, cardSize)


        var cardBack = questionpoint


        images.shuffle()

        for (i in 0 until nbCards!!) {
            val btn_img = Button(this)
            val params: ViewGroup.LayoutParams = ActionBar.LayoutParams(paramsFrameLayout)
            btn_img.layoutParams=params

            btn_img.setBackgroundResource(questionpoint)

            buttonsArray = append(buttonsArray,btn_img)

            gamePiece!!.addView(btn_img)
        }



        for (i in 0 until nbCards!!){
            buttonsArray[i].setBackgroundResource(cardBack)
            buttonsArray[i].text = "cardBack"
            buttonsArray[i].textSize = 0.0F
            buttonsArray[i].setOnClickListener {
                if (buttonsArray[i].text == "cardBack" && !turnOver) {
                    buttonsArray[i].setBackgroundResource(images[i])
                    buttonsArray[i].setText(images[i])
                    if (clicked == 0) {
                        lastClicked = i
                    }
                    clicked++
                } else if (buttonsArray[i].text !in "cardBack") {
                    buttonsArray[i].setBackgroundResource(cardBack)
                    buttonsArray[i].text = "cardBack"
                    clicked--
                }

                if (clicked == 2) {
                    //2 cards have been turned, add a try to the count of tries
                    nbTries++
                    turnOver = true
                    if (buttonsArray[i].text == buttonsArray[lastClicked].text) {
                        goodPairs++
                        buttonsArray[i].isClickable = false
                        buttonsArray[lastClicked].isClickable = false
                        turnOver = false
                        clicked = 0
                    }
                } else if (clicked == 0) {
                    turnOver = false
                }

                if(2*goodPairs == nbCards){
                    onGameWin()
                }
            }

        }
        //Launch the Chronometer
        runTimer()


    }

    fun append(arr: Array<Button>, element: Button): Array<Button> {
        val list: MutableList<Button> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putSerializable("buttonsArray", buttonsArray)
        savedInstanceState.putInt("clicked", clicked)
        savedInstanceState.putInt("lastClicked", lastClicked)
        savedInstanceState.putBoolean("turnover", turnOver)
        savedInstanceState.putInt("seconds", seconds)
        savedInstanceState.putBoolean("running", running)
        savedInstanceState.putInt("nbTries", nbTries)
        super.onSaveInstanceState(savedInstanceState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        clicked = savedInstanceState.getInt("clicked")
        lastClicked = savedInstanceState.getInt("lastClicked")
        turnOver = savedInstanceState.getBoolean("turnOver")
        buttonsArray = savedInstanceState.getSerializable("buttonsArray") as Array<Button>
        //Chronometer
        seconds = savedInstanceState.getInt("seconds")
        running = savedInstanceState.getBoolean("running")
        nbTries = savedInstanceState.getInt("nbTries")

    }

    // ------------ Chronometer ------------- //
    override fun onPause() { //if the game pauses, stop the chronometer
        super.onPause()
        running = false
    }

    override fun onResume() { //if the game resume, resume the chronometer
        super.onResume()
        running = true
    }

    private fun runTimer() {
        //java text view associated with the xml one
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {

                val t = SystemClock.elapsedRealtime();
                if (running) {
                    seconds++
                }
                handler.postDelayed(this, 1000)
            }
        })
    }


    private fun restartGame(){
        //Code here to relaunch a new game
        val text = findViewById<TextView>(R.id.findPairsText)
        text.text = "Trouvez les pairs"
        goodPairs = 0
        //Restart the chrono
        seconds = 0
        running = true
    }

    private fun onGameWin(){
        val text = findViewById<TextView>(R.id.findPairsText)
        text.text = "Bravo !!!"
        mp.start()
        //Stop Chronometer
        running = false

        //Add animation of winning
        val viewKonfetti = findViewById<KonfettiView>(R.id.viewKonfetti)
        viewKonfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED, Color.BLUE)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
            .streamFor(300, 5000L)

        //Send data to database
        runOnUiThread {
            FirebaseActivity().writeNewGameFromGameActivity(this,seconds.toString(), nbTries.toString())
        }
    }

}