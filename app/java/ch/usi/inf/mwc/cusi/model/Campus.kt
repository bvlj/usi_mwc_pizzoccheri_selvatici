package ch.usi.inf.mwc.cusi.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("name"),
    ],
)
data class Campus(
    @PrimaryKey
    val campusId: Int,
    val name: String,
)
