package ch.usi.inf.mwc.cusi.course

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import ch.usi.inf.mwc.cusi.R
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class CourseActivity : ComponentActivity() {

    private val viewModel: CourseViewModel by viewModels()
    private lateinit var enrollButton: MenuItem
    private var courseId = -1
    private lateinit var descriptionView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_page)
        descriptionView = findViewById(R.id.course_info)
        courseId = intent.getIntExtra(EXTRA_COURSE_ID, -1)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.enroll_menu, menu)
        enrollButton = menu.findItem(R.id.menu_enroll_button)
//        return super.onCreateOptionsMenu(menu)
        viewModel.getCourseWithLecturers(courseId).observe(this) {
            descriptionView.text = it.description
            if (it.isEnrolled) {
                enrollButton.setIcon(R.drawable.ic_enrolled)
                enrollButton.setTitle(R.string.menu_unenroll)
            } else {
                enrollButton.setIcon(R.drawable.ic_unenrolled)
                enrollButton.setTitle(R.string.menu_enroll)
            }
        }


        return true

    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_enroll_button) {
            lifecycleScope.launch { viewModel.invertEnroll(courseId) }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }

    companion object {
        const val EXTRA_COURSE_ID = "course_id"
    }
}