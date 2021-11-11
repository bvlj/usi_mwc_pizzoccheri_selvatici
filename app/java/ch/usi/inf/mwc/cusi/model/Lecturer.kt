package ch.usi.inf.mwc.cusi.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index("lastName", "firstName"),
    ],
)
data class Lecturer(
    @PrimaryKey
    val lecturerId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
)
