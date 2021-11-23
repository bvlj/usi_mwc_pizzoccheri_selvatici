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

@Suppress("BlockingMethodInNonBlockingContext")
object UsiServices {
    private const val GET_FACULTIES_URL = "https://search.usi.ch/api/faculties"
    private const val GET_COURSES_URL = "https://search.usi.ch/api/faculties/%d/courses"
    private const val GET_SCHEDULES_URL = "https://search.usi.ch/api/courses/%d/schedules"

    private val scope = CoroutineScope(Default) + CoroutineName("UsiServices")

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
                )
                campus to faculty
            }.groupBy({ it.first }) { it.second }
                .entries
                .map { CampusWithFaculties(it.key, it.value) }
        }

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
        }

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

    private fun JSONObject.getDateTime(key: String): LocalDateTime {
        return LocalDateTime.from(
            DateTimeFormatter.ISO_DATE_TIME.parse(
                getString(key)
            )
        )
    }
}


