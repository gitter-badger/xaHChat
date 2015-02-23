package com.xah.chat.comms

import android.os.IBinder
import android.app.Service
import android.content._
import com.xah.chat.traits.TraitContext
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import android.util.Log
import scala.concurrent._
import scala.language.implicitConversions
import ExecutionContext.Implicits.global
import com.xah.chat.datamodel.tables._
import java.security.MessageDigest
import com.xah.chat.utils.JavaUtils
import com.xah.chat.datamodel.xah
import scala.collection.mutable
import android.net.ConnectivityManager
import scala.util.Failure
import scala.util.Success
import org.json.JSONException
import java.util.UUID

class XService extends Service with TraitContext[Context] {

  private val TAG = "com.xah.chat/XService"
  private val brokerUrl = "tcp://xahbox.com:1883"
  private val DISCONNECTED = 0
  private val CONNECTING = 1
  private val CONNECTED = 2
  private val RECONNECTING = 3

  private var connectionState = DISCONNECTED

  private val NETWORK_UNAVAILABLE = 0
  private val NETWORK_AVAILABLE = 1

  private var networkState = NETWORK_UNAVAILABLE
  def basis = getBaseContext

  var defferedMessages: mutable.Queue[(MqttMessage, String)] = _

  private var mBinder: IBinder = _
  //Set up persistence for messages
  private val peristance = new MemoryPersistence()


  class ConnectivityReceiver extends BroadcastReceiver {
    def onReceive(context: Context, intent: Intent) {
      intent.getAction match {
        case ConnectivityManager.CONNECTIVITY_ACTION =>
          val oldState = networkState
          updateNetworkState()
          val newState = networkState
          if (oldState != newState && networkState == NETWORK_AVAILABLE) {
            connect()
          }
      }
    }
  }

  //Callback automatically triggers as and when new message arrives on specified topic
  private val callback = new MqttCallback() {
    //Handles Mqtt message
    override def messageArrived(topic: String, message: MqttMessage): Unit = {

    }

    override def deliveryComplete(arg0: IMqttDeliveryToken) {
    }

    override def connectionLost(thrown: Throwable) {
      Log.e(TAG, "ConnectionLost", thrown)
      connectionState = RECONNECTING
      Thread.sleep(2000)
      if (networkState == NETWORK_AVAILABLE) connect()
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
    updateNetworkState()
    val filter = new IntentFilter()
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
    getApplicationContext.registerReceiver(new ConnectivityReceiver, filter)
    if (networkState == NETWORK_AVAILABLE) connect()
    Service.START_STICKY
  }

  def subscribe(topic: String) {
    if (client != null && client.isConnected) {
      client.subscribe(topic)
    }
  }

  def unsubscribe(topic: String) {
    if (client != null && client.isConnected) {
      client.unsubscribe(topic)
    }
  }

  override def onUnbind(intent: Intent): Boolean = {
    true
  }

  override def onDestroy() {
    if (client.isConnected) client.disconnect()
  }

  def send(msg: String, topic: String) = {
    val crypt: MessageDigest = MessageDigest.getInstance("SHA-1")
    crypt.reset()
    crypt.update((msg.replace("\"", "'") + System.currentTimeMillis).getBytes("utf8"))

    val message = new MqttMessage("test message".getBytes)
    try {
      if (connectionState != CONNECTED) {
        enqueueMessage(message -> topic)
        if (networkState == NETWORK_AVAILABLE) connect()
      } else {
        Log.d(TAG, s"publish on $topic :: ${new String(message.getPayload)}")
        client.getTopic(topic).publish(message)
      }
    } catch {
      case e: Exception => {
        enqueueMessage(message -> topic)
        if (networkState == NETWORK_AVAILABLE) connect()
      }
    }
  }

  def connect() {
    if (networkState == NETWORK_AVAILABLE &&
      (connectionState == DISCONNECTED || connectionState == RECONNECTING)) {
      connectionState = CONNECTING
      Future {
        Log.i(TAG, "connecting")
        // mqtt client with specific url and client id
        if (client == null) {
          client = new MqttClient(brokerUrl, xah.Handle, peristance)
        }
        val mOpts = new MqttConnectOptions
        mOpts.setCleanSession(false)
        mOpts.setUserName("xahchat")
        mOpts.setPassword("!xahchat!".toCharArray)
        client.connect(mOpts)
        client.setCallback(callback)
        client.subscribe(s"${xah.Handle}/in".toLowerCase, 2)

      } onComplete {
        case Success(t) =>
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
        case Failure(e: MqttException) =>
          connectionState = DISCONNECTED
          Log.e(TAG, s"ReasonCode: ${e.getReasonCode}, Message: ${e.getMessage}")
          Thread.sleep(1000)
          if (networkState == NETWORK_AVAILABLE) connect()
        case _ =>
      }
    }
  }

  def updateNetworkState() {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]
    val activeNetworkInfo = connectivityManager.getActiveNetworkInfo
    networkState = activeNetworkInfo != null && activeNetworkInfo.isConnected match {
      case true => NETWORK_AVAILABLE
      case false => NETWORK_UNAVAILABLE
    }
  }

  override def onBind(intent: Intent): IBinder = {
    mBinder = new XBinder(this)
    if (connectionState == DISCONNECTED) {
      if (networkState == NETWORK_AVAILABLE) connect()
    }
    mBinder
  }
}