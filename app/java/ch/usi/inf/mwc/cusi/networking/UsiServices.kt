package ch.usi.inf.mwc.cusi.networking

import ch.usi.inf.mwc.cusi.model.*
import ch.usi.inf.mwc.cusi.utils.map
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.net.URL
import java.util.*

object UsiServices {
    fun getScheduleBySemesterAndStudy(semester: Int, studyPlan: Int): String {
        TODO()
    }

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
                name = it.getString("name_en"),
                acronym = it.getString("acronym"),
                campus = campus,
                url = URL(it.getString("url_en"))
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
                    val role = cl.getJSONObject("role").getString("name_en")
                    val email = try {
                        person.getJSONObject("emails")
                            .getJSONArray("data")
                            .getString(0)
                    } catch (e: JSONException) {
                        ""
                    }
                    val id = if (person.has("id"))
                        person.getString("id")
                    else
                        UUID.randomUUID().toString() // Here you go
                    val lecturer = Lecturer(
                        fistName = person.getString("first_name"),
                        lastName = person.getString("last_name"),
                        email = email,
                        id = id
                    )
                    CourseLecturer(
                        lecturer = lecturer,
                        role = role
                    )
                }
            //TODO descritpion_it or eng
            Course(
                description = course.getString("description_it"),
                name = course.getString("name_en"),
                semester = course.getString("semester_academic_year"),
                lecturers = courseLecturers,
                faculty = faculty,
                id = course.getInt("id")
            )

        }
        courses

    }

}


