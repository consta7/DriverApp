package com.consta7.driversapp.view.interfaces

interface LoginView {
    fun reactionBarcode(Barcode: String) : Boolean
    fun handWriteVisibility()
    fun loginVisibility()
}