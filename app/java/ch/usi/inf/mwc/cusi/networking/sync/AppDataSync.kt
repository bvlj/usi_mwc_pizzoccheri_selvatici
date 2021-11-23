package ch.usi.inf.mwc.cusi.networking.sync

import android.content.Context
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.db.model.CourseLecturerCrossRef
import ch.usi.inf.mwc.cusi.networking.UsiServices
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext

object AppDataSync {
    private val scope = CoroutineScope(IO) + CoroutineName("AppDataSync")

    suspend fun fetchInfo(context: Context) {
        withContext(scope.coroutineContext) {
            val database = AppDatabase.getInstance(context)
            database.clearAllTables()

            // Fetch campuses
            UsiServices.getCampusesWithFaculties().forEach { campusWithFaculties ->
                database.campuses().insert(campusWithFaculties.campus)

                campusWithFaculties.faculties.forEach { faculty ->
                    database.faculties().insert(faculty)

                    UsiServices.getCoursesByFaculty(faculty).forEach { courseWithLecturers ->
                        database.course().insert(courseWithLecturers.info)

                        courseWithLecturers.lecturers.forEach { lecturer ->
                            database.lecturers().insert(lecturer)
                            database.course().insertCrossRef(
                                CourseLecturerCrossRef(
                                    courseId = courseWithLecturers.info.courseId,
                                    lecturerId = lecturer.lecturerId,
                                )
                            )
                        }

                        /*
                         * TODO: we only want to download lectures iff the user enrolled
                        UsiServices.getCourseWithLectures(courseWithLecturers).lectures.forEach { lecture ->
                            database.lectures().insert(lecture)
                        }
                         */
                    }
                }
            }
        }
    }
}