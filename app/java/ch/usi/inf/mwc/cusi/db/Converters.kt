package ch.usi.inf.mwc.cusi.db

import androidx.room.TypeConverter
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    /* URL <-> String */

    @TypeConverter
    fun fromURL(url: URL): String =
        url.toExternalForm()

    @TypeConverter
    fun toURL(string: String): URL =
        URL(string)

    /* LocalDateTime <-> String */

    @TypeConverter
    fun toString(localDateTime: LocalDateTime): String =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime)

    @TypeConverter
    fun toLocalDateTime(string: String): LocalDateTime =
        LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(string))
}