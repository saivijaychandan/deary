package com.example.dearynotetaking

data class Note(
    val id: Int,
    val date:String,
    val title:String,
    val description:String,
    val imagePath:String?,
    var showDelete: Boolean = false
)