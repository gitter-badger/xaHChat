package com.xah.chat.datamodel.tables

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.Context
import java.sql.Timestamp
import android.provider.BaseColumns
import android.net.Uri
import com.xah.chat.datamodel.xah
import android.util.Log

object MessageFields extends Enumeration {
  type Field = Value
  val _ID, JID, Message, Time, isSent = Value
  val projection =
    (for (v <- values) yield if (v == MessageFields._ID) BaseColumns._ID else v.toString).toArray
}

class Message(val Contact: String, val Message: String,
              val TimeStamp: Timestamp, val isSent: Boolean)

object Messages {
  val _ID = BaseColumns._ID
  val _COUNT = BaseColumns._COUNT
  val TABLE_NAME = "Contacts"
  final val CONTENT_URI = Uri.parse("content://" + xah.AUTHORITY + "/messages")
  final val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.xah.message"
  final val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.xah.message"
  final val DEFAULT_SORT_ORDER = "Time DESC"
}

class MessagesHelper(context: Context) extends SQLiteOpenHelper(context, "xah.db", null, 2) {
  val TAG = "MessagesHelper"

  def onCreate(db: SQLiteDatabase): Unit = {
    val create = s"""
			create table ${Messages.TABLE_NAME} (
				${BaseColumns._ID} integer primary key autoincrement,
				foreign key(${ContactFields.JID}) references Contacts(${MessageFields.JID}) not null,
				${MessageFields.Message} Text,
				${MessageFields.Time} TIMESTAMP,
				${MessageFields.isSent} Boolean
			)"""
    Log.d(TAG, "create")
    db.execSQL(create)
  }

  def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit = {
    val dropTable = s"drop table if exists ${Messages.TABLE_NAME}"
    db.execSQL(dropTable)
    onCreate(db)
  }

}