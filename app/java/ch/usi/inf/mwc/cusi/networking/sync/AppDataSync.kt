package ch.usi.inf.mwc.cusi.networking.sync

import android.content.Context
import ch.usi.inf.mwc.cusi.db.AppDatabase
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
                        courseWithLecturers.lecturers.forEach { lecturer ->
                            database.lecturers().insert(lecturer)
                        }
                        database.course().insert(courseWithLecturers.info)
                    }
                }
            }
        }
    }
}