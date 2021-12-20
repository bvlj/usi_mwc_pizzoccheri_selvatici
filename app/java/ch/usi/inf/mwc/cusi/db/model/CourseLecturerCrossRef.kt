package ch.usi.inf.mwc.cusi.db.model

import androidx.room.Entity
import androidx.room.Index

/**
 * Database entity used for the many-to-many relationship
 * between CourseInfo and Lecturer rows.
 */
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
