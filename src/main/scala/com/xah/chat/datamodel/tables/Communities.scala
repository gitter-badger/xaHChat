package com.xah.chat.datamodel.tables

import android.database.sqlite.SQLiteDatabase
import java.sql.Timestamp
import android.provider.BaseColumns
import android.net.Uri
import com.xah.chat.datamodel.{TableHelper, xah}
import android.util.Log
import scala.language.implicitConversions

object CommunitiesFields extends Enumeration {
  type Field = Value
  val _ID, CommunityName, CommunityDesc, CommunityType, CommunityPassword = Value
  val projection =
    (for (v <- values) yield if (v == CommunitiesFields._ID) BaseColumns._ID else v.toString).toArray
}

class Community(val Contact: String, val Message: String,
              val TimeStamp: Timestamp, val isSent: Boolean)

object Communities {
  val _ID = BaseColumns._ID
  val _COUNT = BaseColumns._COUNT
  val TABLE_NAME = "Communities"
  final val CONTENT_URI = Uri.parse(s"content://${xah.AUTHORITY}/communities")
  final val MESSAGES_JOIN_CONTACTS_URI = CONTENT_URI.buildUpon().appendPath("communities").build()
  final val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.xah.communities"
  final val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.xah.communities"
  final val DEFAULT_SORT_ORDER = ""
}

object CommunitiesType {
  val NormalMessage = 0
  val CommandMessage = 1
  val FeedMessage = 2
  val ServerMessage = 3
  val SublistMessage = 4
  val PlayerlistMessage = 5
}

class CommunitiesHelper extends TableHelper {
  val TAG = "com.xah.MessagesHelper"

  def onCreate(db: SQLiteDatabase): Unit = {
    val create = s"""
      create table ${Messages.TABLE_NAME} (
        ${BaseColumns._ID} integer primary key autoincrement,
        ${CommunitiesFields.CommunityName} Text,
        ${CommunitiesFields.CommunityDesc} Text,
        ${CommunitiesFields.CommunityType} long,
        ${CommunitiesFields.CommunityPassword} Text
      )"""
    Log.d(TAG, create)
    db.execSQL(create)
  }

  def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit = {
    val dropTable = s"drop table if exists ${Messages.TABLE_NAME}"
    db.execSQL(dropTable)
    onCreate(db)
  }

}