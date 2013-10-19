package com.xah.chat.comms

import android.os.IBinder
import android.app.Service
import android.content.{ContentValues, Intent}
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import android.util.Log
import scala.concurrent._
import ExecutionContext.Implicits.global
import com.xah.chat.datamodel.tables._
import scala.util.Success
import scala.util.Failure

class XService extends Service {

  private val TAG = "com.xah.chat/XService"
  private val brokerUrl = "tcp://chat.xah.co.za:1883"
  val mTopic = "xahcraft/out";

  private var mBinder: IBinder = _
  //Set up persistence for messages
  private val peristance = new MemoryPersistence();
  //Callback automatically triggers as and when new message arrives on specified topic
  private val callback = new MqttCallback() {
    //Handles Mqtt message
    override def messageArrived(topic: String, message: MqttMessage) {
      Log.i(TAG, new String(message.getPayload()))
      topic match {
        case mTopic => {
          val payload = new Payload(message)
          if (payload.isServer) {
            val values = new ContentValues()
            values.put(ContactFields.MCName.toString, payload.playerName)
            values.put(ContactFields.ContactType.toString, ContactType.Player.toString)
            getApplicationContext.getContentResolver.update(
              Contacts.CONTENT_URI, values, s"${ContactFields.MCName} = '${payload.playerName}'", null
            )
            val svalues = new ContentValues()
            svalues.put(ContactFields.MCName.toString, payload.serverName)
            svalues.put(ContactFields.ContactType.toString, ContactType.Server.toString)
            getApplicationContext.getContentResolver.update(
              Contacts.CONTENT_URI, svalues, s"${ContactFields.MCName} = '${payload.serverName}'", null
            )

            val msgValues = new ContentValues()
            msgValues.put(MessageFields.MCName.toString, payload.playerName)
            msgValues.put(MessageFields.Message.toString, payload.message)
            msgValues.put(MessageFields.ServerName.toString, payload.serverName)
            msgValues.put(MessageFields.MessageType.toString, MessageType.ServerMessage.toString)
            msgValues.put(MessageFields.MessageId.toString, payload.messageId.toString)
            getApplicationContext.getContentResolver.insert(
              Messages.CONTENT_URI, msgValues
            )
            Log.d(TAG, s"Messages URI: ${Messages.CONTENT_URI}")
          }

        }
        case _ => Log.e(TAG, "Unhandled topic: ${topic}")
      }

    }

    override def deliveryComplete(arg0: IMqttDeliveryToken) {
    }

    override def connectionLost(arg0: Throwable) {
    }
  }
  //Initializing Mqtt Client specifying brokerUrl, clientID and MqttClientPersistance
  private var client: IMqttClient = _


  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    Service.START_STICKY
  }

  override def onUnbind(intent: Intent): Boolean = {
    client.disconnect()
    true
  }

  override def onBind(intent: Intent): IBinder = {
    mBinder = new XBinder(this)
    future {
      Log.i(TAG, "about to connect")

      // mqtt client with specific url and client id
      client = new MqttClient(brokerUrl, "ThisClient", peristance)
      val mOpts = new MqttConnectOptions
      mOpts.setCleanSession(false)
      mOpts.setUserName("xahchat")
      mOpts.setPassword("!xahchat!".toCharArray)
      client.connect(mOpts)
      client.setCallback(callback)
      Log.i(TAG, "connected")
      //Subscribe to Mqtt topic

      client.subscribe(mTopic, 2)
      Log.i(TAG, "subscribed")
    } onComplete {
      case Success(t) => Log.i(TAG, "connected and working")
      case Failure(e: MqttException) => {
        Log.e(TAG, s"ReasonCode: ${e.getReasonCode}, Message: ${e.getMessage}")
        if (e.getReasonCode == 0) {
          Log.e(TAG, e.getCause.getMessage, e.getCause)
        }
      }
    }

    mBinder
  }
}