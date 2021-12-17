package ch.usi.inf.mwc.cusi.networking.sync

import android.content.Context
import android.util.Log
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.db.model.CourseLecturerCrossRef
import ch.usi.inf.mwc.cusi.networking.UsiServices
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext

object AppDataSync {
    private const val TAG = "AppDataSync"
    private val scope = CoroutineScope(IO) + CoroutineName("AppDataSync")


    suspend fun fetchInfo(context: Context) {
        withContext(scope.coroutineContext) {
            Log.d(TAG, "Starting app data sync")
            val database = getDatabase(context)

            // Fetch campuses
            UsiServices.getCampusesWithFaculties().forEach { campusWithFaculties ->
                database.campuses().insertOrUpdateIfExists(campusWithFaculties.campus)

                campusWithFaculties.faculties.forEach { faculty ->
                    Log.d(TAG, "Fetching courses of the ${faculty.acronym} faculty")
                    val dbFaculty = database.faculties().insertOrUpdateIfExists(faculty)

                    if (dbFaculty.showCourses) {
                        UsiServices.getCoursesByFaculty(dbFaculty).forEach { courseWithLecturers ->
                            Log.d(TAG, " - Course: ${courseWithLecturers.info.name}")

                            val info = database.course()
                                .insertOrUpdateIfExists(courseWithLecturers.info)

                            courseWithLecturers.lecturers.forEach { lecturer ->
                                database.lecturers().insert(lecturer)
                                database.course().insertCrossRef(
                                    CourseLecturerCrossRef(
                                        courseId = courseWithLecturers.info.courseId,
                                        lecturerId = lecturer.lecturerId,
                                    )
                                )
                            }

                            if (info.hasEnrolled) {
                                refreshCourseLectures(context, info.courseId)
                            }
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Done syncing")
    }

    suspend fun refreshCourseLectures(context: Context, courseId: Int) {
        val database = getDatabase(context)
        database.lectures().deleteAllOfCourse(courseId)
        val course = database.course().getCourse(courseId)

        UsiServices.getCourseWithLectures(course).lectures
            .distinctBy { it.start to it.end to it.courseId }
            .forEach { lecture -> database.lectures().insert(lecture) }
    }

    suspend fun refreshAllEnrolledCoursesLectures(context: Context) {
        val database = getDatabase(context)
        val enrolledCourses = database.course().getEnrolled()
        enrolledCourses.forEach {
            refreshCourseLectures(context, it.courseWithLecturers.info.courseId)
        }
    }


    private fun getDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
}