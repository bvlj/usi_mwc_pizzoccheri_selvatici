package ch.usi.inf.mwc.cusi.db

import androidx.room.*
import ch.usi.inf.mwc.cusi.model.Campus
import ch.usi.inf.mwc.cusi.model.CampusWithFaculties

@Dao
interface CampusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(campus: Campus)

    @Delete
    suspend fun delete(campus: Campus)

    @Query("SELECT * from Campus ORDER BY name")
    @Transaction
    suspend fun getAll(): List<CampusWithFaculties>
}