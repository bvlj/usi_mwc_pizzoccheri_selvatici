package ch.usi.inf.mwc.cusi.schedule

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.courses.AllCoursesAdapter
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.ceil

class DaySelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : FrameLayout(context, attrs, defStyleAttrs) {
    private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
    private val adapter: Adapter

    private var date = LocalDate.now()

    private var onDateSelectedListener: (LocalDate) -> Unit = {}

    init {
        adapter = Adapter(date, dayOfWeekFormatter, dateFormatter, this::onDateSelected)

        inflate(context, R.layout.view_day_selector, this)
        val listView: RecyclerView = findViewById(R.id.day_selector_list)
        val previousBtn: ImageView = findViewById(R.id.day_selector_previous)
        val nextBtn: ImageView = findViewById(R.id.day_selector_next)

        listView.apply {
            adapter = this@DaySelectorView.adapter
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
        previousBtn.setOnClickListener {
            onDateSelected(date.minusDays(1))
        }
        nextBtn.setOnClickListener {
            onDateSelected(date.plusDays(1))
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(STATE_SUPER, super.onSaveInstanceState())
            putLong(STATE_DATE, date.toEpochDay())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(STATE_SUPER))
            date = LocalDate.ofEpochDay(state.getLong(STATE_DATE))
        }
    }

    fun setOnDateSelectedListener(listener: (LocalDate) -> Unit) {
        onDateSelectedListener = listener
    }

    private fun onDateSelected(date: LocalDate) {
        this.date = date
        adapter.setDate(date)
        onDateSelectedListener(date)
    }

    private class Adapter(
        initialDate: LocalDate,
        private val dayOfWeekFormatter: DateTimeFormatter,
        private val dateFormatter: DateTimeFormatter,
        private val onDateSelected: (LocalDate) -> Unit,
    ) : RecyclerView.Adapter<Adapter.ViewHolder>() {
        private var dates: List<LocalDate>

        init {
            dates = getDates(initialDate)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_day_selector, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setValue(
                dates[position], when {
                    position == DAYS_TO_SHOW / 2 -> DayType.SELECTED
                    dates[position] == LocalDate.now() -> DayType.TODAY
                    else -> DayType.DEFAULT
                }
            )
        }

        override fun getItemCount(): Int {
            return DAYS_TO_SHOW
        }

        fun setDate(date: LocalDate) {
            val dates = getDates(date)
            assert(dates.size == DAYS_TO_SHOW)
            val diff = DiffUtil.calculateDiff(Callback(this.dates, dates))
            this.dates = dates
            diff.dispatchUpdatesTo(this)
        }

        private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            fun setValue(date: LocalDate, type: DayType) {
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
    }

    private companion object {
        const val DAYS_TO_SHOW = 5
        const val STATE_SUPER = "_super"
        const val STATE_DATE = "_date"

        fun getDates(middleDate: LocalDate): List<LocalDate> {
            return (0 until DAYS_TO_SHOW).map {
                middleDate.plusDays(it - DAYS_TO_SHOW / 2L)
            }
        }
    }

    private enum class DayType {
        SELECTED,
        TODAY,
        DEFAULT,
    }
}