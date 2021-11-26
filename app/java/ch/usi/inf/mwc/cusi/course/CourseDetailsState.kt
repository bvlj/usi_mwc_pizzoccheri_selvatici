package ch.usi.inf.mwc.cusi.course

data class CourseDetailsState(
    val courseName: String,
    val courseDescription: CharSequence,
    val hasEnrolled: Boolean,
)