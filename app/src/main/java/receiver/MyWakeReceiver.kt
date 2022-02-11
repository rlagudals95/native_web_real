package receiver

import android.content.Context
import android.content.Intent
import androidx.legacy.content.WakefulBroadcastReceiver
import service.MyIntentService

class MyWakefulReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        Intent(context, MyIntentService::class.java).also { service ->
            WakefulBroadcastReceiver.startWakefulService(context, service)
        }
    }

    fun completeWakefulIntent(intent: Intent) {

    }
}
