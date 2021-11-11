package ch.usi.inf.mwc.cusi.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ch.usi.inf.mwc.cusi.db.model.CourseLecturerCrossRef

data class CourseWithLecturers(
    @Embedded
    val info: CourseInfo,
    @Relation(
        parentColumn = "courseId",
        entityColumn = "lecturerId",
        associateBy = Junction(CourseLecturerCrossRef::class)
    )
    val lecturers: List<Lecturer>,
)