package com.consta7.driversapp.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.consta7.driversapp.Global
import com.consta7.driversapp.R
import com.consta7.driversapp.presenter.LoginPresenter
import com.consta7.driversapp.view.interfaces.LoginView
import kotlinx.android.synthetic.main.activity_load.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginViewImp : Global(), LoginView {

    private var login : LoginPresenter = LoginPresenter()

    companion object {
        var barcode : String? = null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        try {
            login.checkPermission(this, this)
        } catch (e : Exception) {
            Toast.makeText(this, "Permission not access", Toast.LENGTH_SHORT).show()
        }

        handWrite.setOnClickListener {
            handWriteVisibility()
        }
        btnBack.setOnClickListener {
            loginVisibility()
        }
        btnWrite.setOnClickListener {
            if (editBarcode.text.toString().trim().length < 13) {
                Toast.makeText(this, "Введите штрих-код полностью!", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            barcode = editBarcode.text.toString().trim()
            val newMode = Intent(this, ListViewImp::class.java)
            startActivity(newMode)
            finish()
        }
        btnScan.setOnClickListener {
            val scanAct = Intent(this, ScanActivity::class.java)
            scanAct.putExtra("ParentForm","MainActivity")
            startActivity(scanAct)
        }

        loginVisibility()
    }

    override fun loginVisibility() {
        View.GONE.also {
            btnBack.visibility = it
            btnWrite.visibility = it
            editBarcode.visibility = it
            annotation.visibility = it
        }
        View.VISIBLE.also {
            btnScan.visibility = it
            hell.visibility = it
            handWrite.visibility = it
        }
    }

    override fun handWriteVisibility() {
        View.GONE.also {
            btnScan.visibility = it
            hell.visibility = it
            handWrite.visibility = it
        }
        View.VISIBLE.also {
            btnBack.visibility = it
            btnWrite.visibility = it
            editBarcode.visibility = it
            annotation.visibility = it
        }
    }

    override fun reactionBarcode(Barcode: String) : Boolean {
        val newMode = Intent(this, ListViewImp::class.java)
        newMode.putExtra("scanRes", Barcode)
        newMode.putExtra("mode", 0)
        startActivity(newMode)
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        claimScanner()
        onWindowFocusChanged(true)
        Log.d(tag, "onResume")
        if(barcode != null){
            try {
                reactionBarcode(barcode!!)
            }
            catch (e: Exception){
                val toast = Toast.makeText(
                    applicationContext, "Отсутствует соединение с базой!", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        releaseScanner()
        Log.d(tag, "onPause")
    }

    override fun onBackPressed() {
        loginVisibility()
    }

}