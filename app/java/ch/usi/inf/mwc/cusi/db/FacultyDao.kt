package ch.usi.inf.mwc.cusi.db

import androidx.room.*
import ch.usi.inf.mwc.cusi.model.Campus
import ch.usi.inf.mwc.cusi.model.Faculty

@Dao
interface FacultyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(faculty: Faculty)


    @Query("SELECT * FROM Faculty WHERE facultyId=:facultyId LIMIT 1")
    suspend fun getById(facultyId: Int) : Faculty?

    @Update
    suspend fun update(faculty: Faculty)

    @Transaction
    suspend fun insertOrUpdateIfExists(faculty: Faculty): Faculty {
        val existing = getById(facultyId = faculty.facultyId)
        return if(existing  == null){
            faculty.apply { insert(this) }
        } else {
            faculty.apply { update(this) }
        }

    }
}