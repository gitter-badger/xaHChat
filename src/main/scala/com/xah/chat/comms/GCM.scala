package com.xah.chat.comms

import com.google.android.gms.gcm.GoogleCloudMessaging
import android.app.Activity
import android.content.Context
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import android.util.Log
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import com.xah.chat.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import com.xah.chat.ui.activities.MainActivity
import android.app.Notification

object GCM {
  val EXTRA_MESSAGE = "gmc_message"
  val SHAREDPREFERENCES_NAME = "com.xah.chat.comms.GCM"
  val PROPERTY_REG_ID = "gmc_registration_id"
  val PROPERTY_APP_VERSION = "appVersion"
  val PROPERTY_ON_SERVER_EXPIRATION_TIME = "gmc_on_server_expiration_time_ms"
  // Default life span (7 days) of a reservation until it is considered expired
  val REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7
  val SENDER_ID = "385981152969"

  def sendNotification(context: Context, bundle: Bundle) = {
    val pIntent = PendingIntent.getActivity(context, 0,
      new Intent(context, classOf[MainActivity]), 0)
    val noti = new NotificationCompat.Builder(context)
      .setContentTitle("xaHChat message")
      .setContentText(bundle.getString("message"))
      .setStyle(new NotificationCompat.BigTextStyle()
      .bigText(bundle.getString("message")))
      .setSmallIcon(R.drawable.ic_launcher)
      .setContentIntent(pIntent)
      .build()
    noti.flags |= Notification.FLAG_AUTO_CANCEL
    noti.defaults |= Notification.DEFAULT_SOUND
    noti.defaults |= Notification.DEFAULT_LIGHTS
    noti.defaults |= Notification.DEFAULT_VIBRATE
    context.getSystemService(Context.NOTIFICATION_SERVICE)
      .asInstanceOf[NotificationManager].notify(0, noti)
  }
}

class GCM(activity: Activity) {
  val TAG = "GCM"
  val appVersion = activity.getPackageManager.getPackageInfo(activity.getPackageName, 0).versionCode
  val gcm = GoogleCloudMessaging.getInstance(activity)
  val prefs = activity.getSharedPreferences(GCM.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE)
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
    future {
      gcm.register(GCM.SENDER_ID)
    } onComplete {
      case Success(regId) => {
        val editor = prefs.edit()
        editor.putString(GCM.PROPERTY_REG_ID, regId)
        editor.putInt(GCM.PROPERTY_APP_VERSION, appVersion)
        val expirationTime = System.currentTimeMillis() + GCM.REGISTRATION_EXPIRY_TIME_MS
        editor.putLong(GCM.PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime)
        editor.commit()
      }
      case Failure(e) => {
        Log.e(TAG, "registerBackground", e)
      }
    }
  }
}