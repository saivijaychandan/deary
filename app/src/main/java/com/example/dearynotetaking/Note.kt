package com.example.dearynotetaking

data class Note(
    val id: Int,
    val title:String,
    val date:String,
    val imageResId:Int,
    val description:String,
    var showDelete: Boolean = false
)