package com.example.memories.QRCode

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.memories.Database.GetFirebaseDataActivity
import com.example.memories.NumberCardActivity
import com.example.memories.R

class QRCodeScanner : AppCompatActivity() {
    //THIS CODE HAS BEEN TAKEN FROM
    // https://github.com/yuriy-budiyev/code-scanner
    // https://www.geeksforgeeks.org/shared-preferences-in-android-with-examples/
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_scanner)
        val scannerView: CodeScannerView = findViewById(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Le QrCode a été correctement lu", Toast.LENGTH_LONG).show()
                // Storing data into SharedPreferences
                val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                // Creating an Editor object to edit(write to the file)
                val myEdit = sharedPreferences.edit()
                // Storing the key and its value as the data fetched from edittext
                myEdit.putString("userID",it.text)
                // Once the changes have been made,
                // we need to commit to apply those changes made,
                // otherwise, it will throw an error
                myEdit.commit()
                var intent : Intent = Intent(this, GetFirebaseDataActivity::class.java)
                startActivity(intent)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "S'il vous plaît, autoriser l'application à accéder à la caméra",
                    Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}