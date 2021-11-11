package ch.usi.inf.mwc.cusi.db

import androidx.room.*
import ch.usi.inf.mwc.cusi.model.Course
import ch.usi.inf.mwc.cusi.model.CourseInfo
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(courseInfo: CourseInfo)

    @Query("SELECT * FROM CourseInfo WHERE facultyId = :facultyId")
    @Transaction
    suspend fun getCoursesByFaculty(facultyId: Int): List<Course>

    @Query("SELECT * FROM CourseInfo WHERE hasEnrolled = 1")
    @Transaction
    suspend fun getEnrolled(): List<Course>

    @Query("SELECT * FROM CourseInfo WHERE courseId = :courseId LIMIT 1")
    @Transaction
    suspend fun getCourse(courseId: Int): Course?
}