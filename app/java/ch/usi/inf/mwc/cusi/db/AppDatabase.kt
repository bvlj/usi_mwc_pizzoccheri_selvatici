package ch.usi.inf.mwc.cusi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.usi.inf.mwc.cusi.db.model.CourseLecturerCrossRef
import ch.usi.inf.mwc.cusi.model.*
import ch.usi.inf.mwc.cusi.utils.SingletonHolder

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

    abstract fun lectures(): LectureDao

    companion object : SingletonHolder<AppDatabase, Context>({
        if (AppDatabase.DEBUG) {
            // In–memory db for debug
            Room.inMemoryDatabaseBuilder(
                it.applicationContext ?: it,
                AppDatabase::class.java
            ).build()
        } else {
            // Actual db in storage
            Room.databaseBuilder(
                it.applicationContext ?: it,
                AppDatabase::class.java,
                AppDatabase.NAME
            ).build()
        }
    }) {
        private var DEBUG = false
        private var NAME = "app_database.sql"
    }
}