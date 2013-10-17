package com.xah.chat.datamodel.tables

import android.database.sqlite.{SQLiteOpenHelper, SQLiteDatabase}
import android.content.Context
import android.net.Uri
import com.xah.chat.datamodel.xah
import android.provider.BaseColumns
import android.util.Log


object Contacts {
  val _ID = BaseColumns._ID
  val _COUNT = BaseColumns._COUNT
  val TABLE_NAME = "Contacts"
  final val CONTENT_URI = Uri.parse(s"content://${xah.AUTHORITY}/contacts")
  final val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.xah.contact"
  final val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.xah.contact"
  final val DEFAULT_SORT_ORDER = "name DESC"
}

object ContactType {
  val Server = 0
  val Player = 1
}

object ContactFields extends Enumeration {
  type Field = Value
  val _ID, MCName, Status, ContactType = Value
  val projection =
    (for (v <- values) yield if (v == ContactFields._ID) BaseColumns._ID else v.toString).toArray
}

class Contact(val JID: String, val MCName: String, val Status: String, val ContactType: Long)

class ContactsHelper(context: Context) extends SQLiteOpenHelper(context, "xah.db", null, 1) {
  val TAG = "com.xah.ContactsHelper"

  def onCreate(db: SQLiteDatabase): Unit = {
    val create = s"""
			create table ${Contacts.TABLE_NAME} (
				${BaseColumns._ID} integer primary key autoincrement,
				${ContactFields.MCName} Text,
				${ContactFields.Status} Text,
				${ContactFields.ContactType} long
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
