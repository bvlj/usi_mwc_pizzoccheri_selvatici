@file:JvmName("JsonExt")

package ch.usi.inf.mwc.cusi.utils

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Obtain a JSON object response from a GET request
 * for a given [URL].
 */
fun getJsonObject(url: URL): JSONObject {
    return getJson(url) as JSONObject
}

/**
 * Obtain a JSON array response from a GET request
 * for a given [URL].
 */
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

/**
 * Map a [JSONArray] of [JSONObject]s to a [List].
 */
fun <T> JSONArray.map(f: (JSONObject) -> T): List<T> =
    (0 until length()).map { f(getJSONObject(it)) }

/**
 * Get a localized string from a [JSONObject].
 * @param key "Name" of the key without the language.
 *             Eg. "name" will try to retrieve "name_en"
 *             first and if said field is empty, it will try
 *             "name_it"
 */
fun JSONObject.getLocalizedString(key: String): String =
    getString("${key}_en").ifBlank { getString("${key}_it") }
