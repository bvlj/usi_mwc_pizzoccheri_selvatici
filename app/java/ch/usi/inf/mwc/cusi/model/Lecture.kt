package ch.usi.inf.mwc.cusi.model

import androidx.room.*
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CourseInfo::class,
            parentColumns = ["courseId"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("end"),
        Index("courseId"),
    ]
)
data class Lecture(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val courseId: Int,
    @Embedded
    val lectureLocation: LectureLocation,
)