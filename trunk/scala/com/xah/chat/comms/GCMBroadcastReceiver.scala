package com.xah.chat.comms

import android.support.v4.content.WakefulBroadcastReceiver
import android.content.{ Context, Intent, ComponentName }

class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    def onReceive(context: Context, intent: Intent): Unit = {
        val comp = new ComponentName(context.getPackageName(), classOf[GCMIntentService].getName)
    	WakefulBroadcastReceiver.startWakefulService(context, (intent.setComponent(comp)))
    }

}