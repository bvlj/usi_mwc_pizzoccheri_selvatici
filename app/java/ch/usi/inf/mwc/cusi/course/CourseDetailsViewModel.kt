package ch.usi.inf.mwc.cusi.course

import android.app.Application
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.text.style.QuoteSpan.STANDARD_COLOR
import androidx.core.text.HtmlCompat
import androidx.core.text.bold
import androidx.core.text.inSpans
import androidx.core.text.scale
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers


class CourseDetailsViewModel(app: Application) : AndroidViewModel(app) {

    fun getCourseWithLecturers(courseId: Int): LiveData<CourseDetailsState> {
        val db = AppDatabase.getInstance(getApplication())
        return db.course().getCourse(courseId).map {
            CourseDetailsState(
                courseName = it.info.name,
                courseDescription = prepareDescription(it),
                hasEnrolled = it.info.hasEnrolled,
            )
        }
    }

    suspend fun invertEnroll(courseId: Int) {
        val db = AppDatabase.getInstance(getApplication())
        val info = db.course().getCourseInfo(courseId)
        if (info != null) {
            db.course().update(info.copy(hasEnrolled = !info.hasEnrolled))
        }
    }

    private fun prepareDescription(courseWithLecturers: CourseWithLecturers): CharSequence {
        return SpannableStringBuilder().apply {
            // TODO: handle localization
            scale(1.25f) { bold { append("Description") } }
            append('\n')
            append(
                HtmlCompat.fromHtml(
                    courseWithLecturers.info.description,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            )
            append('\n')
            scale(1.25f) { bold { append("Lecturers") } }
            append('\n')
            courseWithLecturers.lecturers.forEach {
                // TODO: add lecturer roles
                inSpans(BulletSpan(4)) {
                    append("${it.lastName} ${it.firstName} (${it.role})")
                    if (it.email.isNotEmpty()) {
                        append(" ${it.email}")
                    }
                    if (it.phoneNumber.isNotEmpty()) {
                        append(" ${it.phoneNumber}")
                    }
                    append('\n')
                }
            }
        }
    }
}