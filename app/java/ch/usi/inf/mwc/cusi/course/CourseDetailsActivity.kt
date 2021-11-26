package ch.usi.inf.mwc.cusi.course

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ch.usi.inf.mwc.cusi.R
import kotlinx.coroutines.launch

class CourseDetailsActivity : AppCompatActivity() {

    private val viewModel: CourseDetailsViewModel by viewModels()

    private lateinit var enrollButton: MenuItem
    private lateinit var descriptionView: TextView

    private var courseId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseId = intent.getIntExtra(EXTRA_COURSE_ID, -1)

        setContentView(R.layout.activity_course_details)
        descriptionView = findViewById(R.id.course_info)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.enroll_menu, menu)
        enrollButton = menu.findItem(R.id.menu_enroll_button)

        fetchContent()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_enroll_button -> {
                lifecycleScope.launch { viewModel.invertEnroll(courseId) }
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchContent() {
        viewModel.getCourseWithLecturers(courseId).observe(this) {
            actionBar?.title = it.courseName

            descriptionView.text = it.courseDescription

            if (it.hasEnrolled) {
                enrollButton.setIcon(R.drawable.ic_enrolled)
                enrollButton.setTitle(R.string.course_details_action_unenroll)
            } else {
                enrollButton.setIcon(R.drawable.ic_unenrolled)
                enrollButton.setTitle(R.string.course_details_action_enroll)
            }
        }
    }

    companion object {
        const val EXTRA_COURSE_ID = "course_id"
    }
}