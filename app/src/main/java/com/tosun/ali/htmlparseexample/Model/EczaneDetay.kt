package com.tosun.ali.htmlparseexample.Model

class EczaneDetay {

    var eczaneİsmi: String? = null
    var adres: String? = null
    var tel: String? = null
    var tarif: String? = null
    var fax:String?=null
    override fun toString(): String {
        return "EczaneDetay(eczaneİsmi=$eczaneİsmi, adres=$adres, tel=$tel, tarif=$tarif, fax=$fax)"
    }
}
