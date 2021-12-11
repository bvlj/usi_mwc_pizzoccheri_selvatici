package ch.usi.inf.mwc.cusi.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.net.URL

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Campus::class,
            parentColumns = ["campusId"],
            childColumns = ["campusId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("name"),
        Index("campusId"),
    ],
)
data class Faculty(
    @PrimaryKey
    val facultyId: Int,
    val name: String,
    val url: URL,
    val acronym: String,
    val campusId: Int,
    val showCourses: Boolean,
)
