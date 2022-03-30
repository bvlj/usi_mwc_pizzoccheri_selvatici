package ch.usi.inf.mwc.cusi.schedule.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import ch.usi.inf.mwc.cusi.R

class ScheduleWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetManager.updateAppWidget(
            appWidgetIds,
            RemoteViews(
                context.packageName,
                R.layout.widget_schedule
            ).apply {
                setRemoteAdapter(
                    R.id.widget_schedule_list,
                    Intent(context, ScheduleWidgetViewsService::class.java),
                )
            }
        )
    }
}
