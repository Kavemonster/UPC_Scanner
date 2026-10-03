package com.example.upcscanner

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ProductLookup {

    fun lookupProduct(upc: String): String {

        val url = URL("https://world.openfoodfacts.org/api/v3/product/$upc")

        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val responseCode = connection.responseCode

        if (responseCode == HttpURLConnection.HTTP_OK) {

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(response)

            val product = json.optJSONObject("product")

            return product?.optString("product_name", " ")
                ?: " "
        }
        return ""
    }
}