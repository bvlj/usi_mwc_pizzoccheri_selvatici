package ch.usi.inf.mwc.cusi.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.usi.inf.mwc.cusi.db.model.CourseLecturerCrossRef
import ch.usi.inf.mwc.cusi.model.Course
import ch.usi.inf.mwc.cusi.model.CourseInfo
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(courseInfo: CourseInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(courseLecturerCrossRef: CourseLecturerCrossRef)

    @Query("SELECT * FROM CourseInfo WHERE facultyId = :facultyId")
    @Transaction
    suspend fun getCoursesByFaculty(facultyId: Int): List<Course>

    @Query("SELECT * FROM CourseInfo WHERE hasEnrolled = 1")
    @Transaction
    suspend fun getEnrolled(): List<Course>

    @Query("SELECT * FROM CourseInfo")
    @Transaction
    fun getAll(): List<CourseWithLecturers>

    @Query("SELECT * FROM CourseInfo")
    @Transaction
    fun getLiveAll(): LiveData<List<CourseWithLecturers>>

    @Query("SELECT * FROM CourseInfo WHERE courseId = :courseId LIMIT 1")
    @Transaction
    suspend fun getCourse(courseId: Int): Course?

    @Query("SELECT hasEnrolled FROM CourseInfo WHERE courseId = :courseId LIMIT 1")
    suspend fun hasEnrolled(courseId: Int): Boolean?

    suspend fun insertRetainingHasEnrolled(courseInfo: CourseInfo) {
        when (hasEnrolled(courseInfo.courseId)) {
            true -> insert(courseInfo.copy(hasEnrolled = true))
            false,
            null -> insert(courseInfo)
        }
    }
}