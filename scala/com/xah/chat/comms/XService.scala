package com.xah.chat.comms

import android.os.IBinder
import android.app.Service
import android.content.Intent
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{ Success, Failure }
import android.util.Log
import org.jivesoftware.smack.AndroidConnectionConfiguration
import org.jivesoftware.smack.SmackAndroid

class XService extends Service {
	private val TAG = "XService"
    private var mBinder: IBinder = _
    private var connection: XMPPConnection = _
    private var config: AndroidConnectionConfiguration = _
    private var username = "lemonxah"
    private var password = "M8kio9ou9u"

    override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
        Service.START_STICKY
    }

    private def getLoginName(username: String) = {
        username + "@chat.xah.co.za"
    }

    override def onCreate() = {
        SmackAndroid.init(this.getApplicationContext())
        config = new AndroidConnectionConfiguration("chat.xah.co.za")
        connection = new XMPPConnection(config)
    }

    override def onDestroy() = {
        if (connection.isConnected()) connection.disconnect()
    }

    override def onUnbind(intent: Intent): Boolean = {
        true
    }

    override def onBind(intent: Intent): IBinder = {
        mBinder = new XBinder(this)

        future {
        	connection.connect()
        	connection.login(getLoginName(username), password)
        } onComplete {
            case Success(_) =>
            case Failure(e) => Log.e(TAG, e.getMessage(), e)
        }

        mBinder
    }

}