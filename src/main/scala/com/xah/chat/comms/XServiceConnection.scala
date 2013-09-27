package com.xah.chat.comms

import android.content.ServiceConnection
import android.content.ComponentName
import android.os.IBinder
import android.util.Log

class XServiceConnection extends ServiceConnection {
  val TAG = "XServiceConnection"
  var mService: XService = _
  var mBound: Boolean = false

  override def onServiceConnected(className: ComponentName, service: IBinder) = {
    mService = service.asInstanceOf[XBinder].getService()
    mBound = true
    Log.d(TAG, "Service Connected")
  }

  override def onServiceDisconnected(className: ComponentName) = {
    mService = null
    mBound = false
    Log.d(TAG, "Service Disconnected")
  }

  def isBound() = mBound

  def getService() = mService
}