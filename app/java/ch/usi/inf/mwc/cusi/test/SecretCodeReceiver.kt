package ch.usi.inf.mwc.cusi.test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * The secret portal to CUSI
 *
 *
 * Secret code receiver.
 * To be used by the OS to launch the test activity.
 *
 * How to use:
 * 1. Open the app
 * 2. Open the dialer app
 * 3. Write "*#*#2874#*#*" in the T9
 * 4. The test activity should be appear
 */
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
            SECRET_CODE_TEST -> {
                context.startActivity(
                    Intent(context, TestActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            SECRET_CODE_UFO -> {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(UFO))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            else -> {
                Log.d(TAG, "Unknown secret code ($secretCode), ignoring...")
            }
        }
    }

    private companion object {
        const val TAG = "SecretCodeReceiver"
        const val ACTION_SECRET_CODE = "android.provider.Telephony.SECRET_CODE"
        const val SECRET_CODE_TEST = 2874 // "CUSI" in T9

        const val SECRET_CODE_UFO = 28746397 // CUSINEWS in T9
        const val UFO = "https://www.cusi-news.ch/"
    }
}