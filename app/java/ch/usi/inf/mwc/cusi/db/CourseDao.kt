package ch.usi.inf.mwc.cusi.db

import androidx.room.*
import ch.usi.inf.mwc.cusi.db.model.CourseLecturerCrossRef
import ch.usi.inf.mwc.cusi.model.Course
import ch.usi.inf.mwc.cusi.model.CourseInfo

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

    @Query("SELECT * FROM CourseInfo WHERE courseId = :courseId LIMIT 1")
    @Transaction
    suspend fun getCourse(courseId: Int): Course?
}