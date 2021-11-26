package ch.usi.inf.mwc.cusi.course

import android.app.Application
import android.text.SpannableStringBuilder
import androidx.core.text.HtmlCompat
import androidx.core.text.bold
import androidx.core.text.scale
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers


data class State(val description: CharSequence, val isEnrolled: Boolean)

class CourseViewModel(app: Application) : AndroidViewModel(app) {


    fun getCourseWithLecturers(courseId: Int): LiveData<State> {
        val db = AppDatabase.getInstance(getApplication())
        return db.course().getCourse(courseId).map {
            State(prepareDescription(it), it.info.hasEnrolled)
        }
    }

    suspend fun invertEnroll(courseId: Int){
        val db = AppDatabase.getInstance(getApplication())
        val info = db.course().getCourseInfo(courseId)!!
        db.course().update(info.copy(hasEnrolled = !info.hasEnrolled))
    }

    private fun prepareDescription(courseWithLecturers: CourseWithLecturers): CharSequence {
        val sb = SpannableStringBuilder()
        sb.apply {
            scale(1.5f) { bold { append("DESCRIPTION") } }
            append('\n')
            append(
                HtmlCompat.fromHtml(
                    courseWithLecturers.info.description,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            )
            append('\n')
            scale(1.5f) { bold { append("LECTURERS") } }
            courseWithLecturers.lecturers.forEach {
                append("• ${it.firstName} ${it.lastName}: ${it.email}\n")
//                TODO add role
            }

        }
        return sb

    }


}