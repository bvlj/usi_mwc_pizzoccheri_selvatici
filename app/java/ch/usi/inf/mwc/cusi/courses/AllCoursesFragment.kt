package ch.usi.inf.mwc.cusi.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.course.CourseDetailsFragment
import ch.usi.inf.mwc.cusi.utils.removeItemDecorationByClass
import ch.usi.inf.mwc.cusi.utils.ui.SideHeaderDecoration
import kotlinx.coroutines.launch

class AllCoursesFragment : Fragment() {

    private val viewModel: AllCoursesViewModel by viewModels()

    private lateinit var adapter: AllCoursesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AllCoursesAdapter { openCourseInfo(it) }

        val listView: RecyclerView = view.findViewById(R.id.courses_list)
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(requireContext())
        listView.itemAnimator = DefaultItemAnimator()

        val refreshLayout: SwipeRefreshLayout = view.findViewById(R.id.courses_refresh)
        refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                refreshLayout.isRefreshing = true
                viewModel.manualSync()
                refreshLayout.isRefreshing = false
            }
        }

        viewModel.getAllCourses().observe(this) { newList ->
            val decorationData = newList.mapIndexed { i, it -> i to it.info.name.first() }
                .distinctBy { it.second }
                .map { it.first to (it.second.toString() to "") }
                .toMap()

            listView.apply {
                removeItemDecorationByClass(SideHeaderDecoration::class.java)
                addItemDecoration(SideHeaderDecoration(requireContext(), decorationData))
            }

            adapter.setList(newList)
        }
    }

    private fun openCourseInfo(courseId: Int) {
        findNavController().navigate(
            R.id.action_allCoursesFragment_to_courseDetailsFragment,
            Bundle().apply {
                putInt(CourseDetailsFragment.EXTRA_COURSE_ID, courseId)
            }
        )
    }
}