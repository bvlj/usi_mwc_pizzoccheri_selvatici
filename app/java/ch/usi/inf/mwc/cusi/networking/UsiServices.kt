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

    suspend fun getFaculties(): List<Faculty> = withContext(Default) {
        val res = getJsonObject(URL(GET_FACULTIES_URL))
        val data = res.getJSONArray("data")
        data.map {
            val campus = it.getJSONObject("campus").run {
                Campus(getInt("id"), getString("name"))
            }
            Faculty(
                id = it.getInt("id"),
                name = it.getLocalizedString("name"),
                acronym = it.getString("acronym"),
                campus = campus,
                url = URL(it.getLocalizedString("url")),
            )
        }
    }

    suspend fun getCoursesByFaculty(faculty: Faculty): List<Course> = withContext(Default) {
        val url = URL(GET_COURSES_URL.format(faculty.id))
        val res = getJsonObject(url)
        val data = res.getJSONArray("data")
        val courses = data.map { course ->
            val courseLecturers: List<CourseLecturer> =
                course.getJSONObject("lecturers").getJSONArray("data").map { cl ->
                    val person = cl.getJSONObject("person")
                    val role = cl.getJSONObject("role").getLocalizedString("name")
                    val email = person.getArrayString("emails", 0)
                    val id = if (person.has("id"))
                        person.getString("id")
                    else
                        UUID.randomUUID().toString() // Here you go
                    val lecturer = Lecturer(
                        id = id,
                        firstName = person.getString("first_name"),
                        lastName = person.getString("last_name"),
                        email = email,
                    )
                    CourseLecturer(
                        lecturer = lecturer,
                        role = role,
                    )
                }
            Course(
                id = course.getInt("id"),
                name = course.getLocalizedString("name"),
                description = course.getLocalizedString("description"),
                semester = course.getString("semester_academic_year"),
                lecturers = courseLecturers,
                faculty = faculty,
            )
        }
        courses
    }

    private fun JSONObject.getArrayString(
        key: String,
        i: Int,
        defaultVal: String = "",
    ): String =
        if (has(key) && (!has("meta") || getJSONObject("meta").getInt("count") > i))
            getJSONArray(key).getString(i)
        else
            defaultVal
}


