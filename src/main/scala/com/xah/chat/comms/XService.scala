package com.xah.chat.comms

import android.os.IBinder
import android.app.Service
import android.content.Intent
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import android.util.Log
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

class XService extends Service {
  private val TAG = "com.xah.chat/XService"
  private val brokerUrl = "tcp://chat.xah.co.za:1883"
  val topic = "xahcraft/out";

  private var mBinder: IBinder = _
  //Set up persistence for messages
  private val peristance = new MemoryPersistence();
  //Callback automatically triggers as and when new message arrives on specified topic
  private val callback = new MqttCallback() {
    //Handles Mqtt message
    override def messageArrived(topic: String, message: MqttMessage) {
      Log.i(TAG, new String(message.getPayload()))
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

      client.subscribe(topic, 2)
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