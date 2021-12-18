package ch.usi.inf.mwc.cusi.test

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.CourseInfo
import ch.usi.inf.mwc.cusi.model.Lecture
import ch.usi.inf.mwc.cusi.model.LectureLocation
import ch.usi.inf.mwc.cusi.notification.LectureNotificationUtil
import ch.usi.inf.mwc.cusi.utils.LocationUtils
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test)

        val addressField: EditText = findViewById(R.id.test_address)
        val notificationBtn: Button = findViewById(R.id.test_notification_btn)

        LocationUtils.getLastGoodLocation(this)?.let {
            val geocoder = Geocoder(this@TestActivity)
            val address = geocoder.getFromLocation(it.latitude, it.longitude, 1).firstOrNull()
            addressField.setText(address?.getAddressLine(0) ?: "")
        }

        notificationBtn.setOnClickListener {
            lifecycleScope.launch {
                it.isEnabled = false
                scheduleTestNotification(addressField.text.toString())
                it.isEnabled = true
            }
        }
    }

    private suspend fun scheduleTestNotification(address: String) {
        val db = AppDatabase.getInstance(this)
        db.course().run {
            getCourseInfo(TEST_COURSE.courseId)?.let { delete(it) }
            insert(TEST_COURSE)
        }
        db.lectures().run {
            deleteAllOfCourse(TEST_COURSE.courseId)

            insert(
                Lecture(
                    id = 1123581321,
                    start = LocalDateTime.now().plusMinutes(31),
                    end = LocalDateTime.now().plusMinutes(91),
                    courseId = TEST_COURSE.courseId,
                    lectureLocation = LectureLocation(
                        "C1.03",
                        address,
                    ),
                )
            )
        }
        val wm = WorkManager.getInstance(this)
        LectureNotificationUtil.schedule(wm)

        withContext(Default) { delay(5000L) }
        db.course().delete(TEST_COURSE)
    }

    private companion object {
        val TEST_COURSE = CourseInfo(
            courseId = 1123581321,
            name = "Test course",
            description = "This is a test course, please ignore me",
            semester = "",
            facultyId = 1,
            hasEnrolled = true,
        )
    }
}