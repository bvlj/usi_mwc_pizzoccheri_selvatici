package ch.usi.inf.mwc.cusi.course

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class CourseDetailsActivity : AppCompatActivity() {

    private val viewModel: CourseDetailsViewModel by viewModels()

    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var lecturersListView: RecyclerView
    private lateinit var enrollButton: ExtendedFloatingActionButton

    private lateinit var lecturersAdapter: LecturersAdapter

    private var courseId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseId = intent.getIntExtra(EXTRA_COURSE_ID, -1)

        setContentView(R.layout.activity_course_details)
        titleView = findViewById(R.id.course_title)
        descriptionView = findViewById(R.id.course_info)
        lecturersListView = findViewById(R.id.course_lecturers)
        enrollButton = findViewById(R.id.course_enroll_btn)

        lecturersAdapter = LecturersAdapter(
            onCallLecturer = this::callLecturer,
            onMailLecturer = this::mailLecturer
        )
        lecturersListView.apply {
            adapter = lecturersAdapter
            layoutManager = LinearLayoutManager(this@CourseDetailsActivity)
            itemAnimator = DefaultItemAnimator()
        }
        enrollButton.setOnClickListener {
            lifecycleScope.launch { viewModel.invertEnroll(courseId) }
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        fetchContent()
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

    private fun fetchContent() {
        viewModel.getCourseWithLecturers(courseId).observe(this) {
            titleView.text = it.courseName
            descriptionView.text = it.courseDescription

            if (it.hasEnrolled) {
                enrollButton.setIconResource(R.drawable.ic_enrolled)
                enrollButton.setText(R.string.course_details_action_unenroll)
            } else {
                enrollButton.setIconResource(R.drawable.ic_unenrolled)
                enrollButton.setText(R.string.course_details_action_enroll)
            }

            lecturersAdapter.setData(it.lecturers)
        }
    }

    private fun callLecturer(phoneNumber: String) {
        startActivity(Intent(Intent.ACTION_DIAL)
            .setData(Uri.fromParts("tel", phoneNumber, null)))
    }

    private fun mailLecturer(email: String) {
        startActivity(Intent(Intent.ACTION_SENDTO)
            .setData(Uri.fromParts("mailto", email, null)))
    }

    companion object {
        const val EXTRA_COURSE_ID = "course_id"
    }
}