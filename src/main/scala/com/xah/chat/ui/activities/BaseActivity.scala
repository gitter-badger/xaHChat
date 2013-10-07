package com.xah.chat.ui.activities

import android.app.Activity
import com.xah.chat.comms.{XService, XServiceConnection}
import android.content.{Context, Intent}

class BaseActivity extends Activity {
  val mConnection = new XServiceConnection

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