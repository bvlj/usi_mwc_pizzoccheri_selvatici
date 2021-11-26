package ch.usi.inf.mwc.cusi.courses

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.course.CourseDetailsActivity
import kotlinx.coroutines.launch

class AllCoursesActivity : AppCompatActivity() {

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

        val refreshLayout: SwipeRefreshLayout = findViewById(R.id.courses_refresh)
        refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                refreshLayout.isRefreshing = true
                viewModel.manualSync()
                refreshLayout.isRefreshing = false
            }
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        viewModel.getAllCourses().observe(this) { newList -> adapter.setList(newList) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}