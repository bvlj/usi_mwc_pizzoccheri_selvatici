package ch.usi.inf.mwc.cusi.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ch.usi.inf.mwc.cusi.model.Lecture

@Dao
interface LectureDao {
    @Insert
    fun insert(lecture: Lecture)

    @Query("DELETE FROM Lecture WHERE courseId = :courseId")
    fun deleteAllOfCourse(courseId: Int)
}