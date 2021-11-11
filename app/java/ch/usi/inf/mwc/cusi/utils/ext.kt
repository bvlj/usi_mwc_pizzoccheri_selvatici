@file:JvmName("ExtUtils")

package ch.usi.inf.mwc.cusi.utils

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/*
val SEMSETER_0 = LocalDate.of(1996, 9, 1)

fun LocalDate.toSemester(): String {
    // TO-DO more precise
    //    val months = (year - SEMSETER_0.year) * 12 + (monthValue - SEMSETER_0.monthValue) - 1
    //    val semester = months/6
    return when (monthValue) {
        in 3..9 -> "SP $year"
        in 9..12 -> "SA $year-${year+1}"
        else -> "SA ${year-1}-$year"
    }
}
*/

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
