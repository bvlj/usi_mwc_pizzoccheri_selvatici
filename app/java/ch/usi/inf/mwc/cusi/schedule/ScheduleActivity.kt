package ch.usi.inf.mwc.cusi.schedule

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.courses.AllCoursesAdapter
import ch.usi.inf.mwc.cusi.courses.CoursesViewModel
import kotlinx.coroutines.launch

class ScheduleActivity : ComponentActivity(){
    private val viewModel: ScheduleViewModel by viewModels()

    private lateinit var adapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_schedule)

        adapter = ScheduleAdapter()

        val listView: RecyclerView = findViewById(R.id.courses_list)
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(this)
        listView.itemAnimator = DefaultItemAnimator()



        lifecycleScope.launch{
            adapter.setList(viewModel.getTodaySchedule())
        }
    }
}