package com.xah.chat.comms

import android.content.{Context, ServiceConnection, ComponentName}
import android.os.IBinder
import android.util.Log
import com.xah.chat.traits.TraitContext

class  XServiceConnection extends ServiceConnection with TraitContext[Context] {
  val TAG = "XServiceConnection"
  var mService: XService = _
  var mBound: Boolean = false
  def basis = mService.getBaseContext

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

  def isBound = mBound

  def getService = mService
}