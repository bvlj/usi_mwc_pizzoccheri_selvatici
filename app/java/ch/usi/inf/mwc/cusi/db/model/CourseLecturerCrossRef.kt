package ch.usi.inf.mwc.cusi.db.model

import androidx.room.Entity

@Entity(primaryKeys = ["courseId", "lecturerId"])
data class CourseLecturerCrossRef(
    val courseId: Int,
    val lecturerId: String,
)
