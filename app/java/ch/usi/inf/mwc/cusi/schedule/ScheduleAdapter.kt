package ch.usi.inf.mwc.cusi.schedule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.model.CourseInfo
import ch.usi.inf.mwc.cusi.model.Lecture
import ch.usi.inf.mwc.cusi.model.LectureInfo
import java.time.format.DateTimeFormatter

class ScheduleAdapter(
    private val onCourseSelected: (Int) -> Unit,
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {
    private var data: List<LectureInfo> = emptyList()

    fun setList(data: List<LectureInfo>) {
        // Compute diff for animation and set data
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
        holder.setContent(data[position], onCourseSelected)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun setContent(lectureInfo: LectureInfo, onCourseSelected: (Int) -> Unit) {
            val nameView: TextView = itemView.findViewById(R.id.course_name)
            val roomAndTimeView: TextView = itemView.findViewById(R.id.course_room)

            nameView.text = lectureInfo.second.name

            val time = DateTimeFormatter.ofPattern("HH:mm").run {
                format(lectureInfo.first.start) + " - "  + format(lectureInfo.first.end)
            }

            val room = lectureInfo.first.lectureLocation.room
            roomAndTimeView.text = "$time\n$room"

            itemView.setOnClickListener { onCourseSelected(lectureInfo.second.courseId) }
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
