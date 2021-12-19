package ch.usi.inf.mwc.cusi.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.Faculty
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext

class PreferencesViewModel(app: Application) : AndroidViewModel(app) {

    fun getFaculties(): LiveData<List<Faculty>> {
        val db = AppDatabase.getInstance(getApplication())
        return db.faculties().getLive()
    }

    fun getSelectedFacultiesNames(): LiveData<List<String>> {
        val db = AppDatabase.getInstance(getApplication())
        return db.faculties().getSelectedLiveNames()
    }

    suspend fun setSelectedFaculties(selectedIds: List<Int>) = withContext(Default) {
        val db = AppDatabase.getInstance(getApplication())
        db.faculties().getAll()
            .map { it.copy(showCourses = it.facultyId in selectedIds) }
            .forEach { db.faculties().update(it) }
    }
}