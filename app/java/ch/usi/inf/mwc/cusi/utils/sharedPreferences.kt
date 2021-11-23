@file:JvmName("SharedPreferencesExt")

package ch.usi.inf.mwc.cusi.utils

import android.content.SharedPreferences
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun SharedPreferences.getDate(
    key: String,
    default: LocalDate,
): LocalDate {
    return getString(key, null)?.run {
        LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(this))
    } ?: default
}

fun SharedPreferences.Editor.putDate(
    key: String,
    value: LocalDate,
): SharedPreferences.Editor {
    return putString(key, DateTimeFormatter.ISO_LOCAL_DATE.format(value))
}