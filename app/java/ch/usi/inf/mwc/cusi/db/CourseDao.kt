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
    @Insert(onConflict = OnConflictStrategy.FAIL)
    suspend fun insert(courseInfo: CourseInfo)

    @Update
    suspend fun update(courseInfo: CourseInfo)

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

    @Query("SELECT * FROM CourseInfo WHERE courseId = :courseId LIMIT 1")
    suspend fun getCourseInfo(courseId: Int): CourseInfo?

    @Query("SELECT hasEnrolled FROM CourseInfo WHERE courseId = :courseId LIMIT 1")
    suspend fun hasEnrolled(courseId: Int): Boolean?

    @Transaction
    suspend fun insertOrUpdateIfExists(courseInfo: CourseInfo): CourseInfo{
        val existing = getCourseInfo(courseInfo.courseId)
        return if(existing  == null){
            courseInfo.apply { insert(this) }
        } else {
            courseInfo.copy(hasEnrolled = existing.hasEnrolled).apply { update(this) }
        }

    }
}