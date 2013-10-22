package com.xah.chat.ui.activities

import android.app.Activity
import com.xah.chat.comms.{XService, XServiceConnection}
import android.content.{Context, Intent}
import com.xah.chat.datamodel.xah

class BaseActivity extends Activity {
  val mConnection = new XServiceConnection

  protected def runOnUi(action: Runnable) = this.runOnUiThread(action)

  implicit def funToRunnable(fun: () => Unit): Runnable = new Runnable {
    def run() = fun()
  }

  override def onStart() = {
    super.onStart()
    if (xah.MCName(this) != "") {
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