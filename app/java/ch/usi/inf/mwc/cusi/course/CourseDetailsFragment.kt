package ch.usi.inf.mwc.cusi.course

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class CourseDetailsFragment : Fragment() {

    private val viewModel: CourseDetailsViewModel by viewModels()

    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var lecturersListView: RecyclerView
    private lateinit var enrollButton: ExtendedFloatingActionButton

    private lateinit var lecturersAdapter: LecturersAdapter

    private var courseId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_course_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Read course from arguments
        courseId = arguments?.getInt(EXTRA_COURSE_ID, -1) ?: -1

        titleView = view.findViewById(R.id.course_title)
        descriptionView = view.findViewById(R.id.course_info)
        lecturersListView = view.findViewById(R.id.course_lecturers)
        enrollButton = view.findViewById(R.id.course_enroll_btn)

        lecturersAdapter = LecturersAdapter(
            onCallLecturer = this::callLecturer,
            onMailLecturer = this::mailLecturer
        )
        lecturersListView.apply {
            adapter = lecturersAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
        }
        fetchContent()
    }

    private fun fetchContent() {
        viewModel.getCourseWithLecturers(courseId).observe(this) {
            // Set UI content based on the state

            titleView.text = it.courseName
            descriptionView.text = it.courseDescription

            enrollButton.isExtended = true
            if (it.hasEnrolled) {
                enrollButton.setIconResource(R.drawable.ic_enrolled)
                enrollButton.setText(R.string.course_details_action_unenroll)
                enrollButton.setOnClickListener {
                    // Execute on the activity lifecycleScope so that if the user
                    // quickly goes back, the operation is not interrupted
                    requireActivity().lifecycleScope.launch { viewModel.unenroll(courseId) }
                }
            } else {
                enrollButton.setIconResource(R.drawable.ic_unenrolled)
                enrollButton.setText(R.string.course_details_action_enroll)
                enrollButton.setOnClickListener {
                    // Execute on the activity lifecycleScope so that if the user
                    // quickly goes back, the operation is not interrupted
                    requireActivity().lifecycleScope.launch { viewModel.enroll(courseId) }
                }
            }

            // Set lecturers data
            lecturersAdapter.setData(it.lecturers)
        }
    }

    private fun callLecturer(phoneNumber: String) {
        // Open the dialer app with the phone number ready to be called
        startActivity(
            Intent(Intent.ACTION_DIAL)
                .setData(Uri.fromParts("tel", phoneNumber, null))
        )
    }

    private fun mailLecturer(email: String) {
        // Open the mail app with the recipient address already filled
        startActivity(
            Intent(Intent.ACTION_SENDTO)
                .setData(Uri.fromParts("mailto", email, null))
        )
    }

    companion object {
        const val EXTRA_COURSE_ID = "course_id"
    }
}