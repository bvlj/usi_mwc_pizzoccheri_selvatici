@file:JvmName("LocalDateExt")

package ch.usi.inf.mwc.cusi.utils

import java.time.LocalDate

fun LocalDate.daysFrom(other: LocalDate): Int {
    return ((year - other.year) * 365) + (dayOfYear - other.dayOfYear)
}
