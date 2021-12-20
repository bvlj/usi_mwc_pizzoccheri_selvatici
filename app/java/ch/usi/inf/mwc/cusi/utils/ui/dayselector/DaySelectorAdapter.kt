package ch.usi.inf.mwc.cusi.utils.ui.dayselector

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DaySelectorAdapter(
    initialDate: LocalDate,
    private val dayOfWeekFormatter: DateTimeFormatter,
    private val dateFormatter: DateTimeFormatter,
    private val onDateSelected: (LocalDate) -> Unit,
) : RecyclerView.Adapter<DaySelectorAdapter.ViewHolder>() {
    private var dates = getDates(initialDate)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_day_selector, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setValue(
            dates[position],
            when {
                position == DAYS_TO_SHOW / 2 -> DayType.SELECTED
                dates[position] == LocalDate.now() -> DayType.TODAY
                else -> DayType.DEFAULT
            },
            dayOfWeekFormatter,
            dateFormatter,
            onDateSelected,
        )
    }

    override fun getItemCount(): Int {
        return DAYS_TO_SHOW
    }

    fun setDate(date: LocalDate) {
        val dates = getDates(date)
        // Compute diff for animation and set data
        val diff = DiffUtil.calculateDiff(Callback(this.dates, dates))
        this.dates = dates
        diff.dispatchUpdatesTo(this)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun setValue(
            date: LocalDate,
            type: DayType,
            dayOfWeekFormatter: DateTimeFormatter,
            dateFormatter: DateTimeFormatter,
            onDateSelected: (LocalDate) -> Unit,
        ) {
            val dayOfWeekView: TextView = itemView.findViewById(R.id.day_selector_weekday)
            val dateView: TextView = itemView.findViewById(R.id.day_selector_date)

            dayOfWeekView.text = dayOfWeekFormatter.format(date)
            dayOfWeekView.setBackgroundResource(
                when (type) {
                    DayType.SELECTED -> R.drawable.bg_day_selector
                    DayType.TODAY -> R.drawable.bg_day_selector_today
                    DayType.DEFAULT -> 0
                }
            )
            dateView.text = dateFormatter.format(date)
            itemView.apply {
                setOnClickListener { onDateSelected(date) }
            }
        }
    }

    private class Callback(
        private val oldList: List<LocalDate>,
        private val newList: List<LocalDate>,
    ) : DiffUtil.Callback() {

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return false
        }

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int,
        ): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }
    }

    private companion object {
        // Should be an odd number otherwise the "middle date" is not in the middle
        const val DAYS_TO_SHOW = 5

        /**
         * Get a sequence of [DAYS_TO_SHOW] dates that have
         * the [middleDate] as the middle element
         */
        fun getDates(middleDate: LocalDate): List<LocalDate> {
            return (0 until DAYS_TO_SHOW).map {
                middleDate.plusDays(it - DAYS_TO_SHOW / 2L)
            }
        }
    }
}