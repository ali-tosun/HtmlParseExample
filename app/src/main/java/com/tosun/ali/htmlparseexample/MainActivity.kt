package com.tosun.ali.htmlparseexample

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tosun.ali.htmlparseexample.Model.Eczane
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {

    var myToken: String? = null


    var webView: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = WebView(this)

        //web viewin javascript tarafını aktif etmek.
        webView?.settings?.javaScriptEnabled = true

        //name kısmı Android olmalı.
        webView?.addJavascriptInterface(JsBridge(), "Android")

        /*myAsyncTask.execute()*/ //AsynTask ile token verisinin alınması.
        this.getToken()

        hello.setOnClickListener {
            this.getEczaneDetay("33")

        }


    }


    /* var myAsyncTask = object : AsyncTask<Void,Void,String>(){

         override fun doInBackground(vararg params: Void?): String {
             var document = Jsoup.connect("http://apps.istanbulsaglik.gov.tr/Eczane").get()

             var tokens = document.toString().split("token")



             if(tokens.size >1){


                 var tokenSplit = tokens[1].substring(0,tokens[1].indexOf("}"))

                 myToken= tokenSplit.replace("\"","").replace(" ","").replace(":","")

                 Log.e("myTokendeneme",""+myToken)

             }


             return myToken!!
         }

         override fun onPostExecute(result: String?) {
             Log.e("myTokendeneme",""+myToken)
             super.onPostExecute(result)
         }
     }*/


    public fun getToken() {

        webView?.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.loadUrl("javascript:window.Android.htmlContentForToken(" +
                        "'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>')")

            }

        }

        webView?.loadUrl("http://apps.istanbulsaglik.gov.tr/Eczane")


    }

    public fun getEczaneDetay(id: String) {

        webView?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.loadUrl("javascript:window.Android.htmlEczaneDetay(" +
                        "'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>')")
            }
        }

        // http://apps.istanbulsaglik.gov.tr/Eczane/nobetci?id=33&token=7c718e3d263387f9
        webView?.loadUrl("http://apps.istanbulsaglik.gov.tr/Eczane/nobetci?id=$id&token=$myToken")
    }


    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {

            if (msg?.what == 1) {

                myToken = msg.obj.toString()
            } else if (msg?.what == 2) {
                Log.e("cevapTag", msg.obj.toString())

                parseHtml(msg.obj.toString())

                Log.e("myCevap", msg.obj.toString())
            }


            super.handleMessage(msg)
        }
    }

    private fun parseHtml(kaynakHtml: String) {

        var document = Jsoup.parse(kaynakHtml)

        var elements = document.select("table.ilce-nobet-detay")

        Log.e("elements", elements.toString())

        var ilceDetay = elements.select("caption>b")


        var eczane = Eczane()

        try {
            eczane.ilceİsim =elements.get(1).text().toString()
            eczane.tarih = elements.get(0).text().toString()
        }
        catch (e:Exception){
            Log.e("exception",e.message)
        }




    }

    inner class JsBridge {


        @JavascriptInterface
        fun htmlContentForToken(url: String) {

            var tokens = url.split("token")
            //Log.e("cevap",tokens[1])

            if (tokens.size > 1) {

                var tokenSplit = tokens[1].substring(0, tokens[1].indexOf("}"))

                myToken = tokenSplit.replace("\"", "").replace(" ", "").replace(":", "")

                var message = Message()
                message.what = 1
                message.obj = myToken
                handler.sendMessage(message)

            }


        }


        @JavascriptInterface
        fun htmlEczaneDetay(str: String) {

            Log.e("eczanedetay", str)
            var message = Message()
            message.what = 2
            message.obj = str
            handler.sendMessage(message)

        }

    }

}




