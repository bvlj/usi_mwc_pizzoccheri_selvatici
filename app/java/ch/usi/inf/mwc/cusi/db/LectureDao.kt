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
    suspend fun insert(lecture: Lecture)

    @Query("SELECT * FROM Lecture WHERE id = :lectureId LIMIT 1")
    suspend fun getLecture(lectureId: Int): Lecture?

    @Query("DELETE FROM Lecture WHERE courseId = :courseId")
    suspend fun deleteAllOfCourse(courseId: Int)

    @Query("SELECT L.* FROM Lecture AS L, CourseInfo AS CI WHERE CI.courseId=L.courseId AND CI.hasEnrolled=1 AND L.start> :start AND L.start < :end ORDER BY L.start")
    suspend fun selectAllEnrolledWithinDates(
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<Lecture>
}