package ch.usi.inf.mwc.cusi.model

import androidx.room.Embedded
import androidx.room.Relation

data class CampusWithFaculties(
    @Embedded
    val campus: Campus,
    @Relation(
        parentColumn = "campusId",
        entityColumn = "campusId",
    )
    val faculties: List<Faculty>,
)