package com.xah.chat.ui.activities

import android.app.Activity
import com.xah.chat.comms.{XService, XServiceConnection}
import android.content.{Context, Intent}
import android.os.Bundle

class BaseActivity extends Activity {
  val mConnection = new XServiceConnection

  protected def runOnUi(action: Runnable) = this.runOnUiThread(action)


  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    startService(new Intent(this, classOf[XService]))
  }

  implicit def funToRunnable(fun: () => Unit): Runnable = new Runnable {
    def run() = fun()
  }

  override def onStart() = {
    super.onStart()
    bindService(new Intent(this, classOf[XService]), mConnection, Context.BIND_AUTO_CREATE)
  }

  override def onDestroy() = {
    super.onDestroy()
    mConnection.mBound match {
      case true => unbindService(mConnection)
      case _ => ()
    }
  }
}