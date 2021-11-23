package ch.usi.inf.mwc.cusi.db.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = [
        "courseId",
        "lecturerId",
    ],
    indices = [
        Index("lecturerId"),
    ],
)
data class CourseLecturerCrossRef(
    val courseId: Int,
    val lecturerId: String,
)
