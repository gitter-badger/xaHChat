package com.xah.chat.ui.activities

import android.app.Activity
import com.xah.chat.comms.XServiceConnection
import android.content.Intent
import com.xah.chat.comms.XService
import android.content.Context

class BaseActivity extends Activity {
    val mConnection = new XServiceConnection  
	override def onStart() = {
        super.onStart()
	    bindService(new Intent(this, classOf[XService]), mConnection, Context.BIND_AUTO_CREATE)
	}
    
    override def onStop() = {
        super.onStop()
        mConnection.mBound match {
            case true => unbindService(mConnection)
            case _ => ()
        }
    }
}