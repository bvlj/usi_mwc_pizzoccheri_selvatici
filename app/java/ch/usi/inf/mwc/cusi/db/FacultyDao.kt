package ch.usi.inf.mwc.cusi.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ch.usi.inf.mwc.cusi.model.Faculty

@Dao
interface FacultyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(faculty: Faculty)
}