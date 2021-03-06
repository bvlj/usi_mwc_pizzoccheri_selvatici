package ch.usi.inf.mwc.cusi.courses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers

class AllCoursesAdapter(
    private val showEnrollIcon: Boolean,
    private val onCourseSelected: (Int) -> Unit,
) :
    RecyclerView.Adapter<AllCoursesAdapter.ViewHolder>() {
    private var data: List<CourseWithLecturers> = emptyList()

    fun setList(data: List<CourseWithLecturers>) {
        // Compute diff for animation and set data
        val diff = DiffUtil.calculateDiff(Callback(this.data, data))
        this.data = data
        diff.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_info, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContent(data[position], showEnrollIcon, onCourseSelected)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun setContent(
            course: CourseWithLecturers,
            showEnrollIcon: Boolean,
            onCourseSelected: (Int) -> Unit,
        ) {
            val nameView: TextView = itemView.findViewById(R.id.course_name)
            val lecturersView: TextView = itemView.findViewById(R.id.course_lecturers)
            val statusView: ImageView = itemView.findViewById(R.id.course_enroll_status)

            nameView.text = course.info.name
            lecturersView.text = course.lecturers.joinToString(", ") {
                "${it.lastName} ${it.firstName[0]}."
            }
            if (course.info.hasEnrolled && showEnrollIcon) {
                statusView.setImageResource(R.drawable.ic_enrolled)
            } else {
                statusView.setImageDrawable(null)
            }

            itemView.setOnClickListener {
                onCourseSelected(course.info.courseId)
            }
        }
    }

    private class Callback(
        private val oldList: List<CourseWithLecturers>,
        private val newList: List<CourseWithLecturers>,
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
            return oldList[oldItemPosition].info.courseId == newList[newItemPosition].info.courseId
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }
    }
}