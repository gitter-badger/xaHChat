package com.xah.chat.datamodel.tables

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.xah.chat.datamodel.{TableHelper, xah}
import android.provider.BaseColumns
import android.util.Log


object Channels {
  val _ID = BaseColumns._ID
  val _COUNT = BaseColumns._COUNT
  val TABLE_NAME = "Contacts"
  final val CONTENT_URI = Uri.parse(s"content://${xah.AUTHORITY}/contacts")
  final val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.xah.contact"
  final val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.xah.contact"
  final val DEFAULT_SORT_ORDER = "name DESC"
}

object ChannelType {
  val Channel = 0
  val Player = 1
}

object ChannelsFields extends Enumeration {
  type Field = Value
  val _ID, ChannelName, Status, ChannelPassword, ChannelType = Value
  val projection =
    (for (v <- values) yield if (v == ChannelsFields._ID) BaseColumns._ID else v.toString).toArray
}

class Channel(val JID: String, val MCName: String, val Status: String, val ContactType: Long)

class ChannelsHelper extends TableHelper {
  val TAG = "com.xah.ContactsHelper"

  def onCreate(db: SQLiteDatabase): Unit = {
    val create = s"""
      create table ${Contacts.TABLE_NAME} (
        ${BaseColumns._ID} integer primary key autoincrement,
        ${ChannelsFields.ChannelName} Text,
        ${ChannelsFields.Status} Text,
        ${ChannelsFields.ChannelPassword} Text,
        ${ChannelsFields.ChannelType} long
      )"""
    Log.d(TAG, create)
    db.execSQL(create)
  }

  def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit = {
    val dropTable = s"drop table if exists ${Contacts.TABLE_NAME}"
    db.execSQL(dropTable)
    onCreate(db)
  }
}
