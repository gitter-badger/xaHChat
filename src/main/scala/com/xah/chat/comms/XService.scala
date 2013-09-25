package com.xah.chat.comms

import android.os.IBinder
import android.app.Service
import android.content.{Intent, ContentValues}
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{ Success, Failure }
import android.util.Log
import android.os.Build
import org.jivesoftware.smack._
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode
import org.jivesoftware.smack.packet.Presence
import java.util.Collection
import scala.collection.JavaConversions._
import com.xah.chat.datamodel.tables.{ContactFields, Contacts }

class XService extends Service with ConnectionListener with RosterListener {
    private val TAG = "com.xah.chat/XService"
    private var mBinder: IBinder = _
    private var connection: XMPPConnection = _
    private var config: AndroidConnectionConfiguration = _
    private val username = "test"
    private val password = "test"

    private var roster: Roster = _

    override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
        Service.START_STICKY
    }

    private def getLoginName(username: String) = {
        username + "@chat.xah.co.za"
    }

    override def onCreate() = {
        SmackAndroid.init(this.getApplicationContext())
        future {
            //ec2-54-200-17-151.us-west-2.compute.amazonaws.com
            Log.i(TAG, "just before call")
            config = new AndroidConnectionConfiguration("chat.xah.co.za", 5222, "xaHChat Server")
            Log.i(TAG, "just after call")
            config.setSASLAuthenticationEnabled(true);
            config.setCompressionEnabled(false);
            config.setSecurityMode(SecurityMode.enabled)

            Build.VERSION.SDK_INT match {
                case i if i > Build.VERSION_CODES.ICE_CREAM_SANDWICH => {
	                config.setTruststoreType("AndroidCAStore")
	                config.setTruststorePassword(null)
                }
                case _ => {
                  var path = System.getProperty("javax.net.ssl.trustStore")
                  if (path == null)
                    path = System.getProperty("java.home") + "/etc/security/cacerts.bks"
	                config.setTruststoreType("BKS")
                  config.setTruststorePath(path);
                }
            }


            Log.i(TAG, "setting connection")
            connection = new XMPPConnection(config)
            Log.i(TAG, "connecting")
            connection.connect()
            Log.i(TAG, "just before login")
            try {
              connection.login(getLoginName(username), password)
            } catch {
              case ex: XMPPException => {
                Log.e(TAG, "login broke", ex)
              }
            }
            Log.i(TAG, "just after login")
            connection.addConnectionListener(this)
            handleRoster()
        }
    }

    def handleRoster() = {
        future {
          try {
            val entries = connection.getRoster.getEntries
            val allValues = entries.map(entry => {
                val values = new ContentValues()
                Log.i(TAG, "%s: %s".format(entry.getName, entry.getUser))
                values.put(ContactFields.JID.toString, entry.getUser)
                values.put(ContactFields.Name.toString, entry.getName)
                if (entry.getStatus != null)
                  values.put(ContactFields.Status.toString, entry.getStatus.toString)
                values
            }).toArray
            Log.i(TAG, "inserting Roster")
            this.getContentResolver.bulkInsert(Contacts.CONTENT_URI, allValues)
          } catch {
            case ex: Exception => Log.e(TAG, "getRoster failed", ex)
          }
        }
    }
    
    override def onDestroy() = {
        future {
            Log.i(TAG, "disconnecting if still connected")
            if (connection.isConnected) connection.disconnect()
        }
    }

    override def onUnbind(intent: Intent): Boolean = {
        true
    }

    override def onBind(intent: Intent): IBinder = {
        mBinder = new XBinder(this)

        mBinder
    }

    /** Connection Listener section */

    /**
     * Connection Closed
     */
    override def connectionClosed() = {

    }

    override def connectionClosedOnError(e: Exception) = {
        //TODO: have to reconnect
    }

    override def reconnectingIn(sec: Int) = {

    }

    override def reconnectionFailed(e: Exception) = {
        //TODO: have to reconnect again
    }

    override def reconnectionSuccessful() = {
        //TODO: display successful reconnections
    }

    /** Roster Listener */

    override def entriesAdded(entries: Collection[String]) = {

    }

    override def entriesUpdated(entries: Collection[String]) = {

    }

    override def entriesDeleted(entries: Collection[String]) = {

    }

    override def presenceChanged(presence: Presence) = {

    }
}