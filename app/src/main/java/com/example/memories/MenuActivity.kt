package com.example.memories

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.memories.Database.GetFirebaseDataActivity
import com.example.memories.QRCode.QRCodeEncoder
import com.example.memories.QRCode.QRCodeScanner


class MenuActivity : AppCompatActivity() {
//A part of this code have been taken from : https://developer.android.com/training/notify-user/build-notification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainmenu)

        //Notifications
        createNotificationChannel()
        declareReceiver()

        var playButton : Button = findViewById(R.id.main_menu_play_b)
        var photosButton : Button = findViewById(R.id.main_menu_photos_b)

        //Launch Game Activity
        playButton.setOnClickListener(View.OnClickListener { view ->
            var intent : Intent = Intent(this, NumberCardActivity::class.java)
            startActivity(intent)
        })

        //Launch photoManagement activity
        photosButton.setOnClickListener(View.OnClickListener { view ->
            var intent : Intent = Intent(this, ShowFoldersActivity::class.java)
            startActivity(intent)
        })
    }

    //Inflate the menu which allow to scan the QRCode
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater : MenuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.menu_result, menu)
        return true
    }

    //On item click => Change activity via intent
    //If we want to add item in menu this is the function to change to manage their onClick
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1 -> {
                var intent : Intent = Intent(this, GetFirebaseDataActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.item2 ->{
                var intent : Intent = Intent(this, QRCodeEncoder::class.java)
                startActivity(intent)
                return true
            }
            R.id.item3 -> {
                var intent : Intent = Intent(this, QRCodeScanner::class.java)
                startActivity(intent)
                return true
            }

        }


        return super.onOptionsItemSelected(item)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notif Channel"
            val descriptionText = "Channel use for notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("NOTIFICATION_REMINDER_TO_PLAY", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun declareReceiver(){
        IntentFilter(Intent.ACTION_SCREEN_ON).also {
            var receiver = RebootBroadcastReceiver()
            registerReceiver(receiver, it)
        }
    }

}