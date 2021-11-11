package ch.usi.inf.mwc.cusi.db

import androidx.room.*
import ch.usi.inf.mwc.cusi.model.Lecturer

@Dao
interface LecturerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lecturer: Lecturer)

    @Delete
    suspend fun delete(lecturer: Lecturer)

    @Query("SELECT * FROM Lecturer ORDER BY lastName, firstName")
    suspend fun getAll(): List<Lecturer>
}