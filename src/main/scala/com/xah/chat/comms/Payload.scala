package com.xah.chat.comms

import org.json.JSONObject
import java.lang.String

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/16
 * Time: 9:59 PM
 */
class Payload(payload: Array[Byte]) {
  val obj = new JSONObject(new String(payload))
  val sender = obj.optString("sender")
  val isServer = obj.optBoolean("isServer")
  val serverName = obj.optString("serverName")
  val serverPassword = obj.optString("serverPassword")
  val serverIp = obj.optString("serverIp")
  val playerName = obj.optString("player")
  val message = obj.optString("message")
  val messageId = obj.optString("messageId")
  val messageType = obj.optLong("messageType")
  val timestamp = obj.optLong("timestamp")
}