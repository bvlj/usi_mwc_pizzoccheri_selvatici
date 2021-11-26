package ch.usi.inf.mwc.cusi.courses

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.course.CourseDetailsActivity
import kotlinx.coroutines.launch

class AllCoursesActivity : ComponentActivity() {

    private val viewModel: AllCoursesViewModel by viewModels()

    private lateinit var adapter: AllCoursesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_courses)

        adapter = AllCoursesAdapter {
            startActivity(
                Intent(
                    this,
                    CourseDetailsActivity::class.java
                ).putExtra(CourseDetailsActivity.EXTRA_COURSE_ID, it)
            )
        }

        val listView: RecyclerView = findViewById(R.id.courses_list)
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(this)
        listView.itemAnimator = DefaultItemAnimator()

        val refreshBtn: Button = findViewById(R.id.test)
        refreshBtn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.manualSync()
            }
        }

        viewModel.getAllCourses().observe(this) { newList -> adapter.setList(newList) }
    }
}