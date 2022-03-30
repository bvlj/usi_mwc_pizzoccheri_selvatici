package ch.usi.inf.mwc.cusi.schedule.widget

import android.content.Context
import android.os.Binder
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.LectureInfo
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class ScheduleWidgetViewsFactory(
    private val context: Context,
) : RemoteViewsService.RemoteViewsFactory {

    private val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private var items: List<LectureInfo> = emptyList()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val idToken = Binder.clearCallingIdentity()

        val db = AppDatabase.getInstance(context)
        val today = LocalDate.now()
        items = runBlocking {
            db.lectures().selectAllEnrolledWithinDates(
                LocalDateTime.of(today, LocalTime.of(0, 0)),
                LocalDateTime.of(today, LocalTime.of(23, 59)),
            ).fold(emptyList()) { acc, lecture ->
                val course = db.course().getCourseInfo(lecture.courseId)
                if (course == null)
                    acc
                else
                    acc + (lecture to course)
            }
        }

        Binder.restoreCallingIdentity(idToken)
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int =
        items.size

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == AdapterView.INVALID_POSITION) {
            return null
        }

        return RemoteViews(context.packageName, R.layout.widget_schedule_item).apply {
            setTextViewText(
                R.id.widget_schedule_item_title,
                items[position].second.name,
            )
            setTextViewText(
                R.id.widget_schedule_item_summary,
                items[position].first.run {
                    "${lectureLocation.room} • ${formatter.format(start)}- ${formatter.format(end)}"
                },
            )
        }
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return items[position].first.id.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
