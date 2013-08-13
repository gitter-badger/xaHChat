package com.xah.chat.comms

import com.google.android.gms.gcm.GoogleCloudMessaging
import android.app.Activity
import android.content.Context
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{ Success, Failure }
import android.util.Log

object GCM {
	val EXTRA_MESSAGE = "gmc_message"
	val SHAREDPREFERNCES_NAME = "com.xah.chat.comms.GCM"
	val PROPERTY_REG_ID = "gmc_registration_id"
	val PROPERTY_APP_VERSION = "appVersion"
	val PROPERTY_ON_SERVER_EXPIRATION_TIME = "gmc_on_server_expiration_time_ms"
	// Default life span (7 days) of a reservation until it is considered expired
	val REGISTRATION_EXPIRTY_TIME_MS = 1000 * 3600 * 24 * 7
	val SENDER_ID = "385981152969"
}

class GCM(activity: Activity) {
	val TAG = "GCM"
	val appVersion = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode
	val gcm = GoogleCloudMessaging.getInstance(activity)
	val prefs = activity.getSharedPreferences(GCM.SHAREDPREFERNCES_NAME, Context.MODE_PRIVATE)
	val isRegistrationExpiredq =
		System.currentTimeMillis() > prefs.getLong(GCM.PROPERTY_ON_SERVER_EXPIRATION_TIME, -1)
	val registrationId =
		prefs.getString(GCM.PROPERTY_REG_ID, "") match {
			case "" => ""
			case regId => prefs.getInt(GCM.PROPERTY_APP_VERSION, -1) match {
				case -1 => ""
				case version => if (version != appVersion) "" else regId
			}
		}
	def registerBackground(): Unit = {
		val registerFuture = future {
			gcm.register(GCM.SENDER_ID)
		}
		registerFuture onComplete {
			case Success(regId) => {
				val editor = prefs.edit()
				editor.putString(GCM.PROPERTY_REG_ID, regId)
				editor.putInt(GCM.PROPERTY_APP_VERSION, appVersion)
				val expirationTime = System.currentTimeMillis() + GCM.REGISTRATION_EXPIRTY_TIME_MS 
				editor.putLong(GCM.PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
				editor.commit();
			}
			case Failure(e) => {
				Log.e(TAG, "registerBackground", e)
			}
		}
		registerFuture
	}
}