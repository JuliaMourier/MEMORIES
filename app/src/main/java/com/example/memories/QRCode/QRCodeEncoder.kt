package com.example.memories.QRCode

import android.R.attr
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memories.R
import com.google.zxing.WriterException

import android.R.attr.bitmap
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.Settings
import android.util.Log
import android.widget.ImageView

import androidmads.library.qrgenearator.QRGContents

import androidmads.library.qrgenearator.QRGEncoder
import com.google.zxing.Dimension
import android.util.DisplayMetrics





class QRCodeEncoder : AppCompatActivity() {
    //THIS CODE COMES FROM
    // https://github.com/androidmads/QRGenerator
    // https://www.tutorialspoint.com/how-can-i-get-android-device-screen-height-width
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_encoder)
        var userid : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase()
        encode(userid)
    }

    private fun encode(inputValue : String){
        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight: Int = displayMetrics.heightPixels
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        val qrgEncoder = QRGEncoder(inputValue, null, QRGContents.Type.TEXT, (0.9*screenHeight).toInt())
        qrgEncoder.colorBlack = Color.BLACK
        qrgEncoder.colorWhite = Color.WHITE
        val qrImage:ImageView = findViewById(R.id.qrImage)
        try {
            // Getting QR-Code as Bitmap
            var bitmap : Bitmap = qrgEncoder.bitmap
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.v(TAG, e.toString())
        }
    }
}