package com.xah.chat.comms

import org.json.JSONObject
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.String

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/16
 * Time: 9:59 PM
 */
class Payload(msg: MqttMessage) {
  val obj = new JSONObject(new String(msg.getPayload()))
  val sender = if (obj.has("sender")) obj.getString("sender") else ""
  val isServer = if (obj.has("isServer")) obj.getBoolean("isServer") else false
  val serverName = if (obj.has("serverName")) obj.getString("serverName") else ""
  val playerName = if (obj.has("player")) obj.getString("player") else ""
  val message = if (obj.has("message")) obj.getString("message") else ""
  val messageId = if (obj.has("messageId")) obj.getString("messageId") else ""
  val messageType = if (obj.has("messageType")) obj.getLong("messageType") else 0
  val timestamp = if (obj.has("timestamp")) obj.getLong("timestamp") else -1
}
