package ch.usi.inf.mwc.cusi.db

import androidx.room.*
import ch.usi.inf.mwc.cusi.model.Campus
import ch.usi.inf.mwc.cusi.model.CampusWithFaculties
import ch.usi.inf.mwc.cusi.model.CourseInfo

@Dao
interface CampusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(campus: Campus)

    @Query("DELETE FROM Campus")
    suspend fun deleteAll()

    @Query("SELECT * from Campus ORDER BY name")
    @Transaction
    suspend fun getAll(): List<CampusWithFaculties>

    @Query("SELECT * FROM Campus WHERE campusId=:campusId LIMIT 1")
    suspend fun getById(campusId: Int) : Campus?

    @Update
    suspend fun update(campus: Campus)

    @Transaction
    suspend fun insertOrUpdateIfExists(campus: Campus): Campus {
        val existing = getById(campusId = campus.campusId)
        return if(existing  == null){
            campus.apply { insert(this) }
        } else {
            campus.apply { update(this) }
        }

    }

}