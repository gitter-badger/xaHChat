package com.xah.chat.datamodel.tables

import android.database.sqlite.SQLiteDatabase
import java.sql.Timestamp
import android.provider.BaseColumns
import android.net.Uri
import com.xah.chat.datamodel.{TableHelper, xah}
import android.util.Log
import scala.language.implicitConversions

object MessageFields extends Enumeration {
  type Field = Value
  val _ID, ContactName, Message, Time, isSent = Value
  val projection =
    (for (v <- values) yield if (v == MessageFields._ID) BaseColumns._ID else v.toString).toArray
}

class Message(val Contact: String, val Message: String,
              val TimeStamp: Timestamp, val isSent: Boolean)

object Messages {
  val _ID = BaseColumns._ID
  val _COUNT = BaseColumns._COUNT
  val TABLE_NAME = "Messages"
  final val CONTENT_URI = Uri.parse(s"content://${xah.AUTHORITY}/messages")
  final val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.xah.message"
  final val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.xah.message"
  final val DEFAULT_SORT_ORDER = "Time DESC"
}

class MessagesHelper extends TableHelper {
  def TAG = "com.xah.MessagesHelper"

  def CreateStatement = s"""
      create table ${Messages.TABLE_NAME} (
        ${BaseColumns._ID} integer primary key autoincrement,
        ${MessageFields.ContactName} Text,
        ${MessageFields.Message} Text,
        ${MessageFields.Time} long,
        ${MessageFields.isSent} Boolean
      )"""

  def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit = {
    db.execSQL(s"drop table if exists ${Messages.TABLE_NAME}")
    onCreate(db)
  }
}