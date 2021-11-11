package ch.usi.inf.mwc.cusi.model

import androidx.room.Embedded
import androidx.room.Relation

data class Course(
    @Embedded
    val courseWithLecturers: CourseWithLecturers,
    @Relation(
        parentColumn = "courseId",
        entity = Lecture::class,
        entityColumn = "courseId",
    )
    val lectures: List<Lecture>,
)