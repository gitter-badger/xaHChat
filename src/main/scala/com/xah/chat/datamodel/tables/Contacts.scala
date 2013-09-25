package com.xah.chat.datamodel.tables

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.Context
import android.net.Uri
import com.xah.chat.datamodel.xah
import android.provider.BaseColumns
import android.util.Log

class ContactsHelper(context: Context) extends SQLiteOpenHelper(context, "xah.db", null, 1) {
	val TAG = "ContactsHelper"
	def onCreate(db: SQLiteDatabase): Unit = {
		val create = """
			create table %s (
				%s integer primary key autoincrement,
				%s Text not null,
				%s Text,
				%s Text,
				%s Text,
				%s Text,
				%s Text
			)""".format(Contacts.TABLE_NAME, BaseColumns._ID, ContactFields.JID, ContactFields.Name, ContactFields.MCName, 
				ContactFields.Server, ContactFields.Status, ContactFields.AvatarId)
		Log.d(TAG, create)
		db.execSQL(create);
	}

	def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit = {
		val droptable = "drop table if exists %s".format(Contacts.TABLE_NAME)
		db.execSQL(droptable)
		onCreate(db)
	}
}

object Contacts {
	val _ID = BaseColumns._ID
    val _COUNT = BaseColumns._COUNT	
	val TABLE_NAME = "Contacts"
	final val CONTENT_URI = Uri.parse("content://" + xah.AUTHORITY + "/contacts")
	final val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.xah.contact"
	final val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.xah.contact"
	final val DEFAULT_SORT_ORDER = "name DESC"
}

object ContactFields extends Enumeration {
	type Field = Value
	val _ID, JID, Name, MCName, Server, Status, AvatarId = Value
	val projection =
    (for(v <- values) yield ( if (v == ContactFields._ID) BaseColumns._ID else v.toString()))
}

class Contact(val JID: String, val Name: String, val MCName: String, 
		val Server: String, val Status: String, val AvatarId: String) 
