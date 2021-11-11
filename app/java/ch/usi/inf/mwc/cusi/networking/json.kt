package ch.usi.inf.mwc.cusi.networking

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun getJsonObject(url: URL): JSONObject {
    return getJson(url) as JSONObject
}

fun getJsonArray(url: URL): JSONArray {
    return getJson(url) as JSONArray
}

private fun getJson(url: URL): Any {
    val connection = url.openConnection() as HttpsURLConnection
    try {
        if (connection.responseCode != 200) {
            return JSONObject()
        }

        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
            val str = reader.readText()
            return JSONTokener(str).nextValue()
        }
    } finally {
        connection.disconnect()
    }
}