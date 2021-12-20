package ch.usi.inf.mwc.cusi.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.course.CourseDetailsFragment
import ch.usi.inf.mwc.cusi.utils.ui.dayselector.DaySelectorView
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleFragment : Fragment() {
    private val viewModel: ScheduleViewModel by viewModels()

    private lateinit var emptyView: TextView
    private lateinit var listAdapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter = ScheduleAdapter {
            // On click listener
            findNavController().navigate(
                R.id.action_schedule_to_scheduleDetails,
                Bundle().apply {
                    putInt(CourseDetailsFragment.EXTRA_COURSE_ID, it)
                }
            )
        }

        val listView: RecyclerView = view.findViewById(R.id.schedule_courses_list)
        listView.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
        }

        emptyView = view.findViewById(R.id.schedule_empty)

        val daySelectorView: DaySelectorView = view.findViewById(R.id.schedule_day_selector)
        daySelectorView.setOnDateSelectedListener { setList(it) }

        if (savedInstanceState == null) {
            // Show today's schedule only the first time
            // From the second time (savedInstanceState != null)
            // The daySelectorView will retain the selected date
            // And so it will call setList on its own through the listener
            setList()
        }
    }

    private fun setList(date: LocalDate = LocalDate.now()) {
        lifecycleScope.launch {
            val list = viewModel.getSchedule(date)
            emptyView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            listAdapter.setList(list)
        }
    }
}