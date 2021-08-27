package com.consta7.driversapp.view

import android.content.*
import android.os.Bundle
import android.view.View
import com.consta7.driversapp.Global
import com.consta7.driversapp.R
import kotlinx.android.synthetic.main.activity_load.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class StartView : Global() {
    /** StartActivity.kt
     * It is load screen without logic and physic.
     * Purely for beauty!
     * */
    private suspend fun load() {
        text.text = (getString(R.string.app_name) + " " + getString(R.string.version) + " - " + getString(R.string.numV))
        progressBar.visibility = View.VISIBLE
        delay(3 * 1000L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)
        supportActionBar?.hide()

        GlobalScope.launch {
            load()
            val begin = Intent(this@StartView, LoginViewImp::class.java)
            startActivity(begin)
            finish()
        }
    }
}