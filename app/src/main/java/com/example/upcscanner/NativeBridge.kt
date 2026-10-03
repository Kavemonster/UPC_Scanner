package com.example.upcscanner

object NativeBridge {

    init {
        System.loadLibrary("native-lib")
    }

    external fun getMessage(): String
    external fun addProduct(upc: String, name: String)
    external fun getSize(): Int
    external fun getProductUPC(index: Int): String
    external fun getProductName(index: Int): String
    external fun getProductQuantity(index: Int): Int
    external fun saveInventory(filename: String)
    external fun loadInventory(filename: String)
    external fun changeQuantity(index: Int, amount: Int)
    external fun removeProduct(index: Int)
    external fun editProductName(index: Int, name: String)
    external fun editProductUPC(index: Int, name: String)
    external fun getCustomName(upc: String):  String
    external fun setCustomName(upc: String, name: String)
    external fun saveCustomNames(filename: String)
    external fun loadCustomNames(filename: String)
    external fun removeCustomName(upc: String)
}