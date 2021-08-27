package com.consta7.driversapp.view

import android.os.Bundle
import android.webkit.WebView
import com.consta7.driversapp.*
import com.consta7.driversapp.presenter.RoutePresenter
import kotlinx.android.synthetic.main.activity_full_route.*

class FullRoute : Global() {
    private val route : RoutePresenter = RoutePresenter()
    private lateinit var openStreet : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_route)

        openStreet = findViewById(R.id.webView)
        route.showWebView(openStreet)

        backPutBack.setOnClickListener {
            finish()
        }
    }
}