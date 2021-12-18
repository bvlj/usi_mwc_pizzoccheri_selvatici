package ch.usi.inf.mwc.cusi.utils.ui.dayselector

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DaySelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : FrameLayout(context, attrs, defStyleAttrs) {
    private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
    private val adapter: DaySelectorAdapter

    private var date = LocalDate.now()

    private var onDateSelectedListener: (LocalDate) -> Unit = {}

    init {
        adapter = DaySelectorAdapter(date, dayOfWeekFormatter, dateFormatter, this::onDateSelected)

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

    private companion object {
        const val STATE_SUPER = "_super"
        const val STATE_DATE = "_date"
    }
}