package ch.usi.inf.mwc.cusi.schedule.widget

import android.content.Intent
import android.widget.RemoteViewsService

class ScheduleWidgetViewsService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ScheduleWidgetViewsFactory(applicationContext)
    }
}
