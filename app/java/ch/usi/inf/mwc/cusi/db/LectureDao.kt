package ch.usi.inf.mwc.cusi.db

import androidx.room.Insert
import ch.usi.inf.mwc.cusi.model.Lecture

interface LectureDao {
    @Insert
    fun insert(lecture: Lecture)
}