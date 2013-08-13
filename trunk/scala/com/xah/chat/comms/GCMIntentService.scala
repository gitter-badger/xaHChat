package com.xah.chat.comms

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import com.google.android.gms.gcm.GoogleCloudMessaging

class GCMIntentService extends IntentService("GCMIntentService") {
    
	override def onHandleIntent(intent: Intent) = {
	    val gcm = GoogleCloudMessaging.getInstance(this)
	    if (!intent.getExtras().isEmpty()) {
	        gcm.getMessageType(intent) match {
	            case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR => // send error
	            case GoogleCloudMessaging.MESSAGE_TYPE_DELETED => // mesage deleted
	            case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE => {
	                GCM.sendNotification(this, intent.getExtras())
	            }
	        }
	    }
	    WakefulBroadcastReceiver.completeWakefulIntent(intent)
	}
}