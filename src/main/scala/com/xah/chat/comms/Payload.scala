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
  val sender = obj.getString("sender")
  val isServer = obj.getBoolean("isServer")
  val serverName = obj.getString("serverName")
  val playerName = obj.getString("player")
  val message = obj.getString("message")
  val messageId = obj.getLong("messageId")
}
