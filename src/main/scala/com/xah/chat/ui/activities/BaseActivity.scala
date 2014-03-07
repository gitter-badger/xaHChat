package com.xah.chat.ui.activities

import com.xah.chat.comms.{XService, XServiceConnection}
import android.content.{Context, Intent}
import scala.language.implicitConversions
import com.xah.chat.datamodel.xah
import com.xah.chat.utils.JavaUtils
import android.support.v4.app.FragmentActivity

class BaseActivity extends FragmentActivity {
  val mConnection = new XServiceConnection
  val mDeviceId = JavaUtils.getDeviceId(this)

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