package com.xah.chat.ui.activities

import com.xah.chat.comms.{XService, XServiceConnection}
import android.content.{Context, Intent}
import scala.language.implicitConversions
import com.xah.chat.datamodel.xah
import android.support.v7.app.ActionBarActivity

class BaseActivity extends ActionBarActivity {
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