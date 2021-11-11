package ch.usi.inf.mwc.cusi.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Faculty::class,
            parentColumns = ["facultyId"],
            childColumns = ["facultyId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("name"),
        Index("facultyId"),
    ],
)
data class CourseInfo(
    @PrimaryKey
    val courseId: Int,
    val name: String,
    val description: String,
    val semester: String,
    val facultyId: Int,
    val hasEnrolled: Boolean = false,
)
