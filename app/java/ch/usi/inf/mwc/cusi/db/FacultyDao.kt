package ch.usi.inf.mwc.cusi.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.usi.inf.mwc.cusi.model.Faculty

@Dao
interface FacultyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(faculty: Faculty)

    @Query("SELECT * FROM Faculty WHERE facultyId = :facultyId LIMIT 1")
    suspend fun getById(facultyId: Int): Faculty?

    @Query("SELECT * FROM Faculty")
    suspend fun getAll(): List<Faculty>

    @Query("SELECT * FROM Faculty ORDER BY name")
    fun getLive(): LiveData<List<Faculty>>

    @Query("SELECT name FROM Faculty WHERE showCourses = 1 ORDER BY name")
    fun getSelectedLiveNames(): LiveData<List<String>>

    @Update
    suspend fun update(faculty: Faculty)

    @Transaction
    suspend fun insertOrUpdateIfExists(faculty: Faculty): Faculty {
        val existing = getById(facultyId = faculty.facultyId)
        return if (existing == null) {
            faculty.apply { insert(this) }
        } else {
            faculty.run { copy(showCourses = existing.showCourses).apply { update(this) } }
        }
    }
}