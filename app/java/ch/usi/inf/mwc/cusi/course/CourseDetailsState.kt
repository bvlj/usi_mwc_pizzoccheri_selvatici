package ch.usi.inf.mwc.cusi.course

import ch.usi.inf.mwc.cusi.model.Lecturer

/**
 * State of the course details UI.
 */
data class CourseDetailsState(
    val courseName: String,
    val courseDescription: CharSequence,
    val lecturers: List<Lecturer>,
    val hasEnrolled: Boolean,
)