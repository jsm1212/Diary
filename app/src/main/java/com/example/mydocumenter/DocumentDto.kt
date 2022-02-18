package com.example.mydocumenter

data class DocumentDto(var seq:Int, var path:String, var address:String, var content:String, var reg:String){
    override fun toString(): String {
        return "$seq $path $address $content $reg"
    }
}