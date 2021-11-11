package ch.usi.inf.mwc.cusi.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.usi.inf.mwc.cusi.db.model.CourseLecturerCrossRef
import ch.usi.inf.mwc.cusi.model.*

@Database(
    entities = [
        Campus::class,
        CourseInfo::class,
        CourseLecturerCrossRef::class,
        Faculty::class,
        Lecture::class,
        Lecturer::class,
    ],
    exportSchema = true,
    version = 1,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun campuses(): CampusDao

    abstract fun course(): CourseDao

    abstract fun faculties(): FacultyDao

    abstract fun lecturers(): LecturerDao
}