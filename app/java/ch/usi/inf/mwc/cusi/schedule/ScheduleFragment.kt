package ch.usi.inf.mwc.cusi.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import kotlinx.coroutines.launch

class ScheduleFragment : Fragment() {
    private val viewModel: ScheduleViewModel by viewModels()

    private lateinit var adapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ScheduleAdapter()

        val listView: RecyclerView = view.findViewById(R.id.schedule_courses_list)
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(requireContext())
        listView.itemAnimator = DefaultItemAnimator()

        val daySelectorView: DaySelectorView = view.findViewById(R.id.schedule_day_selector)
        daySelectorView.setOnDateSelectedListener {
            lifecycleScope.launch {
                adapter.setList(viewModel.getSchedule(it))
            }
        }

        lifecycleScope.launch {
            adapter.setList(viewModel.getSchedule())
        }
    }
}