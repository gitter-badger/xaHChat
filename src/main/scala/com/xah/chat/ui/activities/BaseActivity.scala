package com.xah.chat.ui.activities

import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.view.{View, Window}
import com.xah.chat.comms.{XService, XServiceConnection}
import android.content.{Context, Intent}
import com.xah.chat.framework.TraitActivityContext
import scala.language.implicitConversions
import com.xah.chat.utils.DeviceUtils
import com.xah.chat.datamodel.xah

class BaseActivity extends ActionBarActivity with TraitActivityContext[ActionBarActivity] {
  val mConnection = new XServiceConnection
  val mDeviceId = DeviceUtils.getDeviceId(this)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
  }

  protected def runOnUi(f: () => Unit) = this.runOnUiThread(new Runnable {
    override def run(): Unit = f()
  })

  override def onStart() = {
    super.onStart()
    if (xah.Handle(this) != "") {
      bindService(new Intent(this, classOf[XService]), mConnection, Context.BIND_AUTO_CREATE)
    }
  }

  override def onDestroy() = {
    super.onDestroy()
    mConnection.mBound match {
      case true => unbindService(mConnection)
      case _ => ()
    }
  }
}