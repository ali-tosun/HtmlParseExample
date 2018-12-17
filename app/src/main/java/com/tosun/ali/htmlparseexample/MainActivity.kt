package com.tosun.ali.htmlparseexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tosun.ali.htmlparseexample.Model.Eczane
import com.tosun.ali.htmlparseexample.Model.EczaneDetay
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class MainActivity : AppCompatActivity() {

    var myToken: String? = null

    var tumEczaneler: ArrayList<Eczane>? = null


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

        getToken()

        hello.setOnClickListener {
            //getToken()
            this.getEczaneDetay("33")


        }

        button.setOnClickListener {

            Log.e("oankieczane",tumEczaneler.toString())
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

                var eczane = parseHtml(msg.obj.toString())

                tumEczaneler?.add(eczane)

                Log.e("myCevap", msg.obj.toString())
            }


            super.handleMessage(msg)
        }
    }

    private fun parseHtml(kaynakHtml: String): Eczane {

        var document = Jsoup.parse(kaynakHtml)


        var elements = document.select("table.ilce-nobet-detay")


        var ilceDetay = elements.select("caption>b")
        var eczaneDetay = document.select("table.nobecti-eczane")

        var eczane = Eczane()

        try{
            eczane.tarih = ilceDetay.get(0).text().toString()
            eczane.ilceİsim = ilceDetay.get(1).text().toString()
        }
        catch (e:Exception){

        }


        var tumEczaneDetayları: ArrayList<EczaneDetay> = ArrayList<EczaneDetay>()




        for (elements in eczaneDetay) {

            var eczaneDetay = getParseEczaneDetay(elements)

            Log.e("eczanebas1",eczaneDetay.toString())

            if (eczaneDetay != null) {

                tumEczaneDetayları.add(eczaneDetay)


            }

        }

        eczane.eczaneDetay = tumEczaneDetayları

        Log.e("eczanebas",eczane.toString())
        return eczane


    }

    private fun getParseEczaneDetay(elements: Element): EczaneDetay {


        var eczaneDetay: EczaneDetay = EczaneDetay()

        var eczaneİsimTag = elements.select("thead")

        var eczaneİsim = eczaneİsimTag.select("div").attr("title")
        Log.e("eczaneisim", eczaneİsim)


        //TORAMAN ECZANESİ


        var trTags = elements.select("tbody>tr")
        var adresTag = trTags.select("tr#adres")
        var eczaneAdres = adresTag.select("label").get(1).text()
        Log.e("eczaneadres", eczaneAdres)

        //Çınar Mahallesi, Esenler Caddesi, 11/A, Bağcılar


        var eczaneTelTag = trTags.select("tr#Tel")
        var eczaneTel = eczaneTelTag.select("label").get(1).text()
        Log.e("eczanetel", eczaneTel)


        var eczaneFaxTag = trTags.get(2)
        var eczaneFax = eczaneFaxTag.select("label").get(1).text()
        Log.e("eczanefax", eczaneFax)


        var eczaneAdresTag = trTags.get(3)
        var eczaneAdresTarif = eczaneAdresTag.select("label").get(1).text()
        Log.e("eczaneadres", eczaneAdresTarif)



        eczaneDetay.eczaneİsmi = eczaneİsim
        eczaneDetay.adres = eczaneAdres
        eczaneDetay.tel = eczaneTel
        eczaneDetay.fax = eczaneFax
        eczaneDetay.tarif = eczaneAdresTarif



        return eczaneDetay
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




