package com.xah.chat.ui.activities

import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.view.{View, Window}
import com.xah.chat.comms.{XService, XServiceConnection}
import android.content.{Context, Intent}
import com.xah.chat.traits.TraitActivityContext
import scala.language.implicitConversions
import com.xah.chat.datamodel.xah
import com.xah.chat.utils.JavaUtils

class BaseActivity extends ActionBarActivity with TraitActivityContext[ActionBarActivity] {
  val mConnection = new XServiceConnection
  val mDeviceId = JavaUtils.getDeviceId(this)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
  }

  protected def runOnUi(f: () => Unit) = this.runOnUiThread(new Runnable {
    override def run(): Unit = f()
  })

  implicit def funToRunnable(fun: () => Unit): Runnable = new Runnable {
    def run() = fun()
  }

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