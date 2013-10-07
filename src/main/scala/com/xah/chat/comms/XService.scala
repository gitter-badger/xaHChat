package com.xah.chat.comms

import android.os.IBinder
import android.app.Service
import android.content.Intent
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class XService extends Service {
  private val TAG = "com.xah.chat/XService"
  private val brokerUrl = "tcp://chat.xah.co.za@1883"
  val topic = "hello";

  private var mBinder: IBinder = _
  //Set up persistence for messages
  private val peristance = new MemoryPersistence();
  //Callback automatically triggers as and when new message arrives on specified topic
  private val callback = new MqttCallback() {
    //Handles Mqtt message
    override def messageArrived(arg0: String, message: MqttMessage) {
      println(String.valueOf(message.getPayload()));
    }

    override def deliveryComplete(arg0: IMqttDeliveryToken) {
    }

    override def connectionLost(arg0: Throwable) {
      System.err.println("Connection lost " + arg0)
    }
  }
  //Initializing Mqtt Client specifying brokerUrl, clientID and MqttClientPersistance
  private val client = new MqttClient(brokerUrl, "MQTTSub", peristance);
  client.setCallback(callback)

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    Service.START_STICKY
  }

  override def onUnbind(intent: Intent): Boolean = {
    client.disconnect()
    true
  }

  override def onBind(intent: Intent): IBinder = {
    mBinder = new XBinder(this)
    //Connect to MqttBroker
    client.connect();
    //Subscribe to Mqtt topic
    client.subscribe(topic);
    mBinder
  }
}