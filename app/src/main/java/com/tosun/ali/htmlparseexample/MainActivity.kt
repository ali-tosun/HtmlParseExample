package com.tosun.ali.htmlparseexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {

    var myToken:String? = null


    var webView: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = WebView(this)

        //web viewin javascript tarafını aktif etmek.
        webView?.settings?.javaScriptEnabled = true

        //name kısmı Android olmalı.
        webView?.addJavascriptInterface(JsBridge(), "Android")

        this.getToken()


    }

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

    inner class JsBridge {


        @JavascriptInterface
        fun htmlContentForToken(url: String) {

            var tokens = url.split("token")
            //Log.e("cevap",tokens[1])

            if(tokens.size >1){

                var tokenSplit = tokens[1].substring(0,tokens[1].indexOf("}"))

               myToken= tokenSplit.replace("\"","").replace(" ","").replace(":","")

            }


        }

    }

}




