package ch.usi.inf.mwc.cusi.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ch.usi.inf.mwc.cusi.model.Lecture
import java.time.LocalDateTime

@Dao
interface LectureDao {
    @Insert
    fun insert(lecture: Lecture)

    @Query("DELETE FROM Lecture WHERE courseId = :courseId")
    fun deleteAllOfCourse(courseId: Int)

    @Query("SELECT L.* FROM Lecture AS L, CourseInfo AS CI WHERE CI.courseId=L.courseId AND CI.hasEnrolled=1 AND L.start> :start AND L.start < :end")
    fun selectAllEnrolledWithinDates(start: LocalDateTime, end: LocalDateTime): List<Lecture>
}