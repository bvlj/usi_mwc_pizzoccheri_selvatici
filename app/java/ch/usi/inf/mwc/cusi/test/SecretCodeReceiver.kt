package ch.usi.inf.mwc.cusi.test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SecretCodeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val receivedContext = context ?: return
        val receivedIntent = intent ?: return
        handleIntent(receivedContext, receivedIntent)
    }

    private fun handleIntent(context: Context, intent: Intent) {
        if (ACTION_SECRET_CODE != intent.action) {
            // we did not receive a secret code action, ignore
            return
        }

        val secretCodeString = intent.dataString
            ?.replace("android_secret_code://", "")
            ?.replace("tel:*#*#", "")
            ?.replace("#*#*", "")
            ?: ""

        val secretCode = try {
            secretCodeString.toInt()
        } catch (nfe: NumberFormatException) {
            Int.MIN_VALUE
        }
        Log.d(TAG, "Got secret code: $secretCode")

        handleSecretCode(context, secretCode)
    }

    private fun handleSecretCode(context: Context, secretCode: Int) {
        when (secretCode) {
            SECRET_CODE_DIAGNOSTICS -> {
                context.startActivity(Intent(context, TestActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            else -> {
                Log.d(TAG, "Unknown secret code ($secretCode), ignoring...")
            }
        }
    }

    private companion object {
        const val TAG = "SecretCodeReceiver"
        const val ACTION_SECRET_CODE = "android.provider.Telephony.SECRET_CODE"
        const val SECRET_CODE_DIAGNOSTICS = 2874
    }
}