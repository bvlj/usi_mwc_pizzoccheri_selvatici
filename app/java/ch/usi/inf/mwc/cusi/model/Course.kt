package ch.usi.inf.mwc.cusi.model;

data class Course(val id: Int,
                  val name: String,
                  val description: String,
                  val semester: String,
                  val faculty: Faculty,
                  val lecturers: List<CourseLecturer>
                  )
