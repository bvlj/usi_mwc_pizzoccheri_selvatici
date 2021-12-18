package ch.usi.inf.mwc.cusi.networking.sync

import android.content.Intent
import android.content.IntentFilter

object SyncBroadcast {
    private const val ACTION = "ch.usi.inf.mwc.cusi.ACTION_SYNC"
    private const val SUCCESS = "success"

    val INTENT_FILTER = IntentFilter(ACTION)


    fun getIntent(success: Boolean) = Intent()
        .setAction(ACTION)
        .putExtra(SUCCESS, success)

    fun isSuccessful(intent: Intent?) =
        intent?.getBooleanExtra(SUCCESS, false) ?: false
}