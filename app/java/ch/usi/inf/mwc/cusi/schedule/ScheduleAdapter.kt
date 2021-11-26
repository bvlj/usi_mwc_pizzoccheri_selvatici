package ch.usi.inf.mwc.cusi.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.model.CourseInfo
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers
import ch.usi.inf.mwc.cusi.model.Lecture
import ch.usi.inf.mwc.cusi.model.Lecturer
import java.time.format.DateTimeFormatter


private typealias LectureInfo = Pair<Lecture, CourseInfo>

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {
    private var data: List<LectureInfo> = emptyList()

    fun setList(data: List<LectureInfo>) {
        val diff = DiffUtil.calculateDiff(Callback(this.data, data))
        this.data = data
        diff.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContent(data[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun setContent(lectureInfo: LectureInfo) {
            val nameView: TextView = itemView.findViewById(R.id.course_name)
            val roomAndTimeView: TextView = itemView.findViewById(R.id.course_room)

            nameView.text = lectureInfo.second.name

            val time = DateTimeFormatter.ofPattern("hh:mm").run {
                format(lectureInfo.first.start) + "-"  + format(lectureInfo.first.end)
            }

            val room = lectureInfo.first.lectureLocation.room
            roomAndTimeView.text = "$time\n$room"

        }
    }

    private class Callback(
        private val oldList: List<LectureInfo>,
        private val newList: List<LectureInfo>,
    ) : DiffUtil.Callback() {

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int,
        ): Boolean {
            return oldList[oldItemPosition].second.courseId == newList[newItemPosition].second.courseId
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }
    }
}