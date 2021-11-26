package ch.usi.inf.mwc.cusi.course

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.model.Lecturer

class LecturersAdapter(
    private val onCallLecturer: (String) -> Unit,
    private val onMailLecturer: (String) -> Unit,
) : RecyclerView.Adapter<LecturersAdapter.ViewHolder>() {
    private var data: List<Lecturer> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lecturer, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContent(data[position], onCallLecturer, onMailLecturer)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: List<Lecturer>) {
        val diff = DiffUtil.calculateDiff(Callback(this.data, data))
        this.data = data
        diff.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun setContent(
            lecturer: Lecturer,
            onCall: (String) -> Unit,
            onMail: (String) -> Unit,
        ) {
            val nameView: TextView = itemView.findViewById(R.id.lecturer_name)
            val roleView: TextView = itemView.findViewById(R.id.lecturer_role)
            val phoneView: ImageView = itemView.findViewById(R.id.lecturer_phone)
            val mailView: ImageView = itemView.findViewById(R.id.lecturer_mail)

            val fullName = "${lecturer.lastName} ${lecturer.firstName}"
            nameView.text = fullName
            roleView.text = lecturer.role

            mailView.apply {
                contentDescription = context.getString(
                    R.string.course_details_mail_lecturer,
                    fullName,
                )
                if (lecturer.email.isEmpty()) {
                    visibility = View.GONE
                    setOnClickListener { }
                } else {
                    visibility = View.VISIBLE
                    setOnClickListener { onMail(lecturer.email) }
                }
            }

            phoneView.apply {
                contentDescription = context.getString(
                    R.string.course_details_call_lecturer,
                    fullName,
                )
                if (lecturer.phoneNumber.isEmpty()) {
                    visibility = View.GONE
                    setOnClickListener { }
                } else {
                    visibility = View.VISIBLE
                    setOnClickListener { onCall(lecturer.phoneNumber) }
                }
            }
        }
    }

    private class Callback(
        private val oldList: List<Lecturer>,
        private val newList: List<Lecturer>,
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
            return oldList[oldItemPosition].lecturerId == newList[newItemPosition].lecturerId
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }
    }
}