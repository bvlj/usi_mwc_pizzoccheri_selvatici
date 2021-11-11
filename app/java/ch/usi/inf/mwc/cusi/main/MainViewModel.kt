package ch.usi.inf.mwc.cusi.main

import android.util.Log
import androidx.lifecycle.ViewModel
import ch.usi.inf.mwc.cusi.networking.UsiServices

class MainViewModel : ViewModel() {
    suspend fun test() {
        val sb = StringBuilder()
        UsiServices.getCampusesWithFaculties().forEach { cf ->
            sb.append("\n============================\n")
                .append("Campus: ${cf.campus}\n")
            cf.faculties.forEach { faculty ->
                sb.append("  - Faculty: $faculty\n")
                UsiServices.getCoursesByFaculty(faculty).forEach { course ->
                    sb.append("    - Course: ${course.info}\n")
                    course.lecturers.forEach { lecturer ->
                        sb.append("      - $lecturer\n")
                    }
                }
            }
            Log.e("HELLOTAG", sb.toString())
        }
    }
}