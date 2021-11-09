package ch.usi.inf.mwc.cusi.utils

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

//val SEMSETER_0 = LocalDate.of(1996, 9,1)
//
//
//fun LocalDate.toSemester(): String {
////    val months = (year - SEMSETER_0.year) * 12 + (monthValue - SEMSETER_0.monthValue) - 1 //TODO more precise
////    val semester = months/6
//    return when (monthValue) {
//        in 3..9 -> "SP $year"
//        in 9..12 -> "SA $year-${year+1}"
//        else -> "SA ${year-1}-$year"
//    }
//}


fun <T> JSONArray.map(f: (JSONObject) -> T): List<T> {
    val list = mutableListOf<T>()
    var i = 0
    while (i < length()) {
        list.add(f(getJSONObject(i)))
        i++
    }
    return list
}