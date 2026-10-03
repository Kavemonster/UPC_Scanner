package com.example.upcscanner
//converts the c++ values into Kotlin strings, ints
data class ProductData(
    val upc: String,
    val name: String,
    val quantity: Int,
)