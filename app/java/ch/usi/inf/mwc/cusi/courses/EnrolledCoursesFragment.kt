package ch.usi.inf.mwc.cusi.courses

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.course.CourseDetailsFragment
import ch.usi.inf.mwc.cusi.networking.sync.SyncBroadcast
import ch.usi.inf.mwc.cusi.networking.sync.SyncService
import ch.usi.inf.mwc.cusi.utils.removeItemDecorationByClass
import ch.usi.inf.mwc.cusi.utils.ui.SideHeaderDecoration

class EnrolledCoursesFragment : Fragment() {

    private val viewModel: EnrolledCoursesViewModel by viewModels()

    private lateinit var adapter: AllCoursesAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout

    private val syncBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Done syncing
            refreshLayout.isRefreshing = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Listen to sync status broadcasts
        context?.registerReceiver(syncBroadcastReceiver, SyncBroadcast.INTENT_FILTER)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enrolled_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AllCoursesAdapter(false) { openCourseInfo(it) }

        val listView: RecyclerView = view.findViewById(R.id.courses_list)
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(requireContext())
        listView.itemAnimator = DefaultItemAnimator()

        val emptyView: TextView = view.findViewById(R.id.enrolled_empty)

        refreshLayout = view.findViewById(R.id.courses_refresh)
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = true
            context?.run { SyncBroadcast.syncAll(this) }
        }

        viewModel.getEnrolledCourses().observe(this) { newList ->
            val decorationData = newList.mapIndexed { i, it -> i to it.info.name.first() }
                .distinctBy { it.second }
                .map { it.first to (it.second.toString() to "") }
                .toMap()

            listView.apply {
                removeItemDecorationByClass(SideHeaderDecoration::class.java)
                addItemDecoration(SideHeaderDecoration(requireContext(), decorationData))
            }

            emptyView.visibility = if (newList.isEmpty()) View.VISIBLE else View.GONE
            adapter.setList(newList)
        }
    }

    override fun onDestroy() {
        context?.unregisterReceiver(syncBroadcastReceiver)
        super.onDestroy()
    }

    private fun openCourseInfo(courseId: Int) {
        findNavController().navigate(
            R.id.action_enrolledCourses_to_enrolledDetails,
            Bundle().apply {
                putInt(CourseDetailsFragment.EXTRA_COURSE_ID, courseId)
            }
        )
    }
}