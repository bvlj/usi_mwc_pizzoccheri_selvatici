package ch.usi.inf.mwc.cusi.networking

import ch.usi.inf.mwc.cusi.model.*
import ch.usi.inf.mwc.cusi.utils.getJsonObject
import ch.usi.inf.mwc.cusi.utils.getLocalizedString
import ch.usi.inf.mwc.cusi.utils.map
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import org.json.JSONObject
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("BlockingMethodInNonBlockingContext") // coroutines are being used
object UsiServices {
    private const val GET_FACULTIES_URL = "https://search.usi.ch/api/faculties"
    private const val GET_COURSES_URL = "https://search.usi.ch/api/faculties/%d/courses"
    private const val GET_SCHEDULES_URL = "https://search.usi.ch/api/courses/%d/schedules"

    private val scope = CoroutineScope(Default) + CoroutineName("UsiServices")

    /**
     * Fetch campus and faculties data.
     */
    suspend fun getCampusesWithFaculties(): List<CampusWithFaculties> =
        withContext(scope.coroutineContext) {
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
                    showCourses = false,
                )
                campus to faculty
            }.groupBy({ it.first }) { it.second }
                .entries
                .map { CampusWithFaculties(it.key, it.value) }
        }

    /**
     * Fetch all the courses of a given faculty.
     */
    suspend fun getCoursesByFaculty(faculty: Faculty): List<CourseWithLecturers> =
        withContext(scope.coroutineContext) {
            val url = URL(GET_COURSES_URL.format(faculty.facultyId))
            val res = getJsonObject(url)
            res.getJSONArray("data").map {
                val lecturers: List<Lecturer> = it.getJSONObject("lecturers")
                    .getJSONArray("data")
                    .map { cl ->
                        val person = cl.getJSONObject("person")
                        val firstName = person.getString("first_name")
                        val lastName = person.getString("last_name")
                        val email = person.getArrayString("emails", 0)
                        val phoneNumber = person.getArrayString("phones", 0, "official")
                        val role = cl.getJSONObject("role")
                        val id = if (person.has("id"))
                            person.getString("id")
                        else
                            UUID.nameUUIDFromBytes(
                                // Seed the uuid with the person's name for
                                // reproducible uuids
                                "$lastName$firstName".encodeToByteArray()
                            ).toString()
                        Lecturer(
                            lecturerId = id,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phoneNumber = phoneNumber,
                            role = role.getLocalizedString("name"),
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
            }.filter {
                // Apparently, there exists courses with no name...
                it.info.name.isNotBlank()
            }
        }

    /**
     * Get information about a course and its lecturers.
     */
    suspend fun getCourseWithLectures(
        courseWithLecturers: CourseWithLecturers,
    ): Course = withContext(scope.coroutineContext) {
        val courseInfo = courseWithLecturers.info
        val url = URL(GET_SCHEDULES_URL.format(courseInfo.courseId))
        val res = getJsonObject(url)
        val lectures = res.getJSONArray("data").map {
            val start = it.getDateTime("start")
            val end = it.getDateTime("end")
            val place = it.getJSONObject("place")
            val lectureLocation = LectureLocation(
                room = place.getString("office"),
                address = place.getJSONObject("building").getString("address"),
            )
            Lecture(
                start = start,
                end = end,
                courseId = courseInfo.courseId,
                lectureLocation = lectureLocation,
            )
        }
        Course(
            courseWithLecturers = courseWithLecturers,
            lectures = lectures,
        )
    }

    /**
     * Helper method to get a string value of an array.
     */
    private fun JSONObject.getArrayString(
        key: String,
        i: Int,
        innerKey: String? = null,
        defaultVal: String = "",
    ): String {
        return if (has(key)) {
            val obj = getJSONObject(key)
            if (!obj.has("meta") || obj.getJSONObject("meta").getInt("count") > i) {
                val arr = obj.getJSONArray("data")
                if (innerKey == null) {
                    arr.getString(i)
                } else {
                    val innerObj = arr.getJSONObject(i)
                    if (innerObj.has(innerKey)) {
                        innerObj.getString(innerKey)
                    } else {
                        defaultVal
                    }
                }
            } else {
                defaultVal
            }
        } else {
            defaultVal
        }
    }

    /**
     * Extension method to get a value of type LocalDateTime.
     */
    private fun JSONObject.getDateTime(key: String): LocalDateTime {
        return LocalDateTime.from(
            DateTimeFormatter.ISO_DATE_TIME.parse(
                getString(key)
            )
        )
    }
}


