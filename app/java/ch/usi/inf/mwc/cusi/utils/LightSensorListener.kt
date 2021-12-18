package ch.usi.inf.mwc.cusi.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.util.Log

class LightSensorListener(val onLightChanged: (Boolean) -> Unit) : SensorEventListener {
    private var enabled = false
    private var lastChange = SystemClock.elapsedRealtimeNanos()

    override fun onSensorChanged(event: SensorEvent?) {
        val value = event?.values?.firstOrNull()
        val now = event?.timestamp ?: 0L

        if (value != null && (lastChange == 0L || now - lastChange > UPDATE_DELTA)) {
            Log.d(TAG, "New light sensor value: $value at time: $now")

            onLightChanged(value < SensorManager.LIGHT_CLOUDY)
            lastChange = now
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        enabled = accuracy > 0
    }

    private companion object {
        const val TAG = "LightSensorListener"
        const val UPDATE_DELTA = 1_500_000_000L
    }
}