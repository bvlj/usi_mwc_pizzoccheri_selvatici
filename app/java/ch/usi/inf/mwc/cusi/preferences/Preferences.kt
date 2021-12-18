package ch.usi.inf.mwc.cusi.preferences

object Preferences {
    const val KEY_FACULTIES = "key_selected_faculties"
    const val KEY_STYLE = "key_style"
    const val KEY_NOTIFICATIONS = "key_notifications"
    const val KEY_SYNC_FREQUENCY = "key_sync_frequency"
    const val KEY_SYNC_LAST = "key_sync_last_date_time"

    const val VALUE_STYLE_SYSTEM = "0"
    const val VALUE_STYLE_LIGHT_SENSOR = "1"
    const val VALUE_STYLE_NIGHT = "2"
    const val VALUE_STYLE_DAY = "3"
    // "number of days" - 1
    const val VALUE_SYNC_NEVER = "-1"
    const val VALUE_SYNC_DAILY = "0"
    const val VALUE_SYNC_3_DAYS = "2"
    const val VALUE_SYNC_7_DAYS = "6"
}