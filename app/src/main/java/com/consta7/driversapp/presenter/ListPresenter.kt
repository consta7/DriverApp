package com.consta7.driversapp.presenter

import android.content.Context
import com.consta7.driversapp.model.DataBaseImp
import com.consta7.driversapp.model.JsonFileConstructorImp
import com.consta7.driversapp.model.JsonParseImp
import com.consta7.driversapp.presenter.interfaces.ListAbstract
import com.consta7.driversapp.view.interfaces.ListViewInfo

class ListPresenter : ListAbstract {

    private var listView : ListViewInfo? = null
    private val data : DataBaseImp = DataBaseImp()
    private val json : JsonParseImp = JsonParseImp()
    private val construct : JsonFileConstructorImp = JsonFileConstructorImp()

    fun listPresenter(listView: ListViewInfo) {
        this.listView = listView
    }

    //waiting information
    override fun successResult(code: Int, barcode : String, context: Context) {
        //send request for data
        val result : Int = if (data.requestBarcode(barcode) == 1) 1 else 0
        //wait
        handler.postDelayed({ listView?.onSuccessResult(result, code) }, 3 * 1000L)
    }

    //wow, we can begin
    override fun successParse(code: Int) {
        val result = 1
        listView?.onSuccessParse(result, code)
    }

    //if u don't know what is it, it is circle on view
    override fun progressBarVisible(code: Int) {
        listView?.onSetProgressBarVisible(code)
    }

    //it is table and button panel on view
    override fun informationVisible(code : Int) {
        listView?.onSetInformationVisible(code)
    }

    override fun successWrite(fileName : String) {
        data.requestData(fileName)
    }

    fun transferValues(context: Context, code: Int) {
        construct.createJsonFile()
        construct.writeInFile(
            context,
            json.getParseValue("taskNumber"),
            0, construct.getReport(code)
        )
    }

    fun getIndex() : Int = construct.getIndex()
    fun setPhoneID(id : Int) : Int = construct.setPhoneID(id)
}