package com.xah.chat.utils

import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager

/**
 * Project: xaHChat
 * Created on 2015-03-02 by
 * lemonxah -
 * https://github.com/lemonxah
 * http://stackoverflow.com/users/2919672/lemon-xah 
 */
object DeviceUtils {
  def hex2bytes(hex: String): Array[Byte] = {
    hex.replaceAll("[^0-9A-Fa-f]","").sliding(2,2).toArray.map(Integer.parseInt(_,16).toByte)
  }

  def bytes2Hex(bytes: Array[Byte], sep: Option[String] = None): String = {
    bytes.map("%02x".format(_)) mkString (sep match {
      case None => ""
      case Some(s) => s
    })
  }

  def getDeviceId(context: Context): String = {
    var id: String = getUniqueID(context)
    if (id == null) id = Settings.Secure.getString(context.getContentResolver, Settings.Secure.ANDROID_ID)
    id
  }

  private def getStringIntegerHexBlocks(v: Int): String = v.toString.toCharArray.sliding(4,4).mkString("-")

  private def getUniqueID(context: Context): String = {
    var telephonyDeviceId: String = "NoTelephonyId"
    var androidDeviceId: String = "NoAndroidId"
    try {
      val tm: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE).asInstanceOf[TelephonyManager]
      telephonyDeviceId = tm.getDeviceId
      if (telephonyDeviceId == null) {
        telephonyDeviceId = "NoTelephonyId"
      }
    }
    catch {
      case e: Exception =>
    }
    try {
      androidDeviceId = android.provider.Settings.Secure.getString(context.getContentResolver, android.provider.Settings.Secure.ANDROID_ID)
      if (androidDeviceId == null) {
        androidDeviceId = "NoAndroidId"
      }
    }
    catch {
      case e: Exception =>
    }
    try {
      getStringIntegerHexBlocks(androidDeviceId.hashCode) + "-" + getStringIntegerHexBlocks(telephonyDeviceId.hashCode)
    }
    catch {
      case e: Exception => "0000-0000-1111-1111"
    }
  }
}
