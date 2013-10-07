package com.xah.chat.comms

import android.os.IBinder
import android.app.Service
import android.content.Intent

class XService extends Service {
  private val TAG = "com.xah.chat/XService"
  private var mBinder: IBinder = _

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    Service.START_STICKY
  }

  override def onUnbind(intent: Intent): Boolean = {
    true
  }

  override def onBind(intent: Intent): IBinder = {
    mBinder = new XBinder(this)

    mBinder
  }
}