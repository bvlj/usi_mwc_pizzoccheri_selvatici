package ch.usi.inf.mwc.cusi.networking

import ch.usi.inf.mwc.cusi.model.*
import ch.usi.inf.mwc.cusi.utils.getLocalizedString
import ch.usi.inf.mwc.cusi.utils.map
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*

@Suppress("BlockingMethodInNonBlockingContext")
object UsiServices {
    private const val GET_FACULTIES_URL = "https://search.usi.ch/api/faculties"
    private const val GET_COURSES_URL = "https://search.usi.ch/api/faculties/%d/courses"

    suspend fun getCampusesWithFaculties(): List<CampusWithFaculties> = withContext(Default) {
        val res = getJsonObject(URL(GET_FACULTIES_URL))
        val data = res.getJSONArray("data")
        data.map {
            val campus = it.getJSONObject("campus").let { campus ->
                Campus(
                    campusId = campus.getInt("id"),
                    name = campus.getString("name"),
                )
            }
            val faculty = Faculty(
                facultyId = it.getInt("id"),
                name = it.getLocalizedString("name"),
                acronym = it.getString("acronym"),
                campusId = campus.campusId,
                url = URL(it.getLocalizedString("url")),
            )
            campus to faculty
        }.groupBy({ it.first }) { it.second }
            .entries
            .map { CampusWithFaculties(it.key, it.value) }
    }

    suspend fun getCoursesByFaculty(faculty: Faculty): List<CourseWithLecturers> =
        withContext(Default) {
            val url = URL(GET_COURSES_URL.format(faculty.facultyId))
            val res = getJsonObject(url)
            val data = res.getJSONArray("data").map {
                val lecturers: List<Lecturer> = it.getJSONObject("lecturers")
                    .getJSONArray("data")
                    .map { cl ->
                        val person = cl.getJSONObject("person")
                        val email = person.getArrayString("emails", 0)
                        val id = if (person.has("id"))
                            person.getString("id")
                        else
                            UUID.randomUUID().toString() // Here you go
                        Lecturer(
                            lecturerId = id,
                            firstName = person.getString("first_name"),
                            lastName = person.getString("last_name"),
                            email = email,
                        )
                    }
                val course = CourseInfo(
                    courseId = it.getInt("id"),
                    name = it.getLocalizedString("name"),
                    description = it.getLocalizedString("description"),
                    semester = it.getString("semester_academic_year"),
                    facultyId = faculty.facultyId,
                )
                CourseWithLecturers(
                    info = course,
                    lecturers = lecturers,
                )
            }
            data
        }

    private fun JSONObject.getArrayString(
        key: String,
        i: Int,
        defaultVal: String = "",
    ): String {
        return if (has(key)) {
            val obj = getJSONObject(key)
            if (!obj.has("meta") || obj.getJSONObject("meta").getInt("count") > i) {
                obj.getJSONArray("data").getString(i)
            } else {
                defaultVal
            }
        } else {
            defaultVal
        }
    }
}


