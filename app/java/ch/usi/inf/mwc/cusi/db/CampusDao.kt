package ch.usi.inf.mwc.cusi.db

import androidx.room.*
import ch.usi.inf.mwc.cusi.model.Campus
import ch.usi.inf.mwc.cusi.model.CampusWithFaculties

@Dao
interface CampusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(campus: Campus)

    @Query("DELETE FROM Campus")
    suspend fun deleteAll()

    @Query("SELECT * from Campus ORDER BY name")
    @Transaction
    suspend fun getAll(): List<CampusWithFaculties>
}