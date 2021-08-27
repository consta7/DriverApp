package com.consta7.driversapp.view

import android.os.Bundle
import android.util.Log
import com.google.zxing.Result
import com.consta7.driversapp.Global
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : Global(), ZXingScannerView.ResultHandler {

    private var mScannerView: ZXingScannerView? = null
    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        setContentView(mScannerView)                // Set the scanner view as the content view
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.flash = true
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        // Do something with the result here
        if (rawResult.text.length != 13) {
            LoginViewImp.barcode = null
          //  onBackPressed()
        } else {
            Log.v("tag", rawResult.text) // Prints scan results
            Log.v("tag", rawResult.barcodeFormat.toString()) // Prints the scan format (qrcode, pdf417 etc.)
            LoginViewImp.barcode = rawResult.text
        }

        onBackPressed()
    }
}