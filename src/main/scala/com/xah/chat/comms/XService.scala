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
import org.json.{JSONObject, JSONException}
import java.security.MessageDigest
import com.xah.chat.utils.JavaUtils
import com.xah.chat.datamodel.xah
import scala.collection.mutable

class XService extends Service {

  private val TAG = "com.xah.chat/XService"
  private val brokerUrl = "tcp://chat.xah.co.za:1883"
  private val DISCONNECTED = 0
  private val CONNECTING = 1
  private val CONNECTED = 2
  private val RECONNECTING = 3

  private var connectionState = DISCONNECTED
  var defferedMessages: mutable.Queue[(MqttMessage, String)] = _
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
        case `mTopic` => {
          try {
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
              msgValues.put(MessageFields.MessageType.toString, payload.messageType.toString)
              msgValues.put(MessageFields.MessageId.toString, payload.messageId.toString)
              msgValues.put(MessageFields.Time.toString, payload.timestamp.toString)
              getApplicationContext.getContentResolver.update(
                Messages.CONTENT_URI, msgValues, s"${MessageFields.MessageId} = '${payload.messageId}'", null
              )
            }
          } catch {
            case e: JSONException => Log.w(TAG, "JSON error occured", e)
          }

        }
        case _ => Log.e(TAG, "Unhandled topic: ${topic}")
      }

    }

    override def deliveryComplete(arg0: IMqttDeliveryToken) {
    }

    override def connectionLost(thrown: Throwable) {
      connectionState = RECONNECTING
      Thread.sleep(2000)
      connect()
    }
  }
  //Initializing Mqtt Client specifying brokerUrl, clientID and MqttClientPersistance
  private var client: IMqttClient = _

  def enqueueMessage(msg: (MqttMessage, String)) {
    if (defferedMessages == null) defferedMessages = new mutable.Queue[(MqttMessage, String)]
    defferedMessages += msg
  }

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    Log.d(TAG, "Entering from onStartCommand")
    connect()
    Service.START_STICKY
  }

  override def onUnbind(intent: Intent): Boolean = {
    true
  }

  override def onDestroy() {
    if (client.isConnected) client.disconnect()
  }

  def send(msg: String, topic: String): Payload = {
    val crypt: MessageDigest = MessageDigest.getInstance("SHA-1")
    crypt.reset()
    crypt.update((msg.replace("\"", "'") + System.currentTimeMillis).getBytes("utf8"))
    val json: JSONObject = new JSONObject
    json.put("sender", xah.MCName(getApplicationContext))
    json.put("isServer", false)
    json.put("message", msg.replace("\"", "'"))
    json.put("messageType", MessageType.NormalMessage)
    json.put("messageId", JavaUtils.bytesToHex(crypt.digest()))
    json.put("timestamp", System.currentTimeMillis)
    val text: String = json.toString
    val message: MqttMessage = new MqttMessage(text.getBytes)
    try {
      if (connectionState != CONNECTED) {
        enqueueMessage(message -> topic)
        connect()
      } else {
        Log.d(TAG, s"publish on $topic :: ${message.getPayload.toString}")
        client.getTopic(topic).publish(message)
      }
    } catch {
      case e: Exception => {
        enqueueMessage(message -> topic)
        connect()
      }
    }
    new Payload(message)
  }

  def connect() {
    if (connectionState == DISCONNECTED || connectionState == RECONNECTING) {
      future {
        connectionState = CONNECTING
        Log.i(TAG, "about to connect")
        // mqtt client with specific url and client id
        if (client == null) {
          client = new MqttClient(brokerUrl, xah.MCName(getApplicationContext), peristance)
        }
        val mOpts = new MqttConnectOptions
        mOpts.setCleanSession(false)
        mOpts.setUserName("xahchat")
        mOpts.setPassword("!xahchat!".toCharArray)
        client.connect(mOpts)
        client.setCallback(callback)
        client.subscribe(mTopic.toLowerCase, 2)
        client.subscribe(s"${xah.MCName(getApplicationContext)}/in".toLowerCase, 2)

        //TODO: remove this section when servers list is implemented
        val svalues = new ContentValues()
        svalues.put(ContactFields.MCName.toString, "xaHCraft")
        svalues.put(ContactFields.ContactType.toString, ContactType.Server.toString)
        getApplicationContext.getContentResolver.update(
          Contacts.CONTENT_URI, svalues, s"${ContactFields.MCName} = 'xaHCraft'", null
        )

      } onComplete {
        case Success(t) => {
          connectionState = CONNECTED
          Log.i(TAG, "con/sub")
          if (defferedMessages != null && defferedMessages.length > 0) {
            Log.i(TAG, "sending defferedMessages")
            defferedMessages.dequeueAll((data: (MqttMessage, String)) => {
              try {
                Log.d(TAG, s"deffered publish on ${data._2} :: ${data._1.getPayload.toString}")
                client.getTopic(data._2).publish(data._1)
                true
              } catch {
                case _: Exception => false
              }
            })
          }
        }
        case Failure(e: MqttException) => {
          connectionState = DISCONNECTED
          Log.e(TAG, s"ReasonCode: ${e.getReasonCode}, Message: ${e.getMessage}")
          Thread.sleep(1000)
          connect()
        }
      }
    }
  }

  override def onBind(intent: Intent): IBinder = {
    mBinder = new XBinder(this)
    if (connectionState == DISCONNECTED) connect()
    mBinder
  }
}