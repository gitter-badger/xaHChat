package com.xah.chat.datamodel

import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.content.Context
import com.xah.chat.datamodel.tables.{MessagesHelper, ContactsHelper}

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/19
 * Time: 3:32 PM
 */
class DBHelper(context: Context) extends SQLiteOpenHelper(context, "xah.db", null, 1) {
  val tables = new ContactsHelper :: new MessagesHelper :: List()

  def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    tables.foreach(table => table.onUpgrade(db, oldVersion, newVersion))
  }

  def onCreate(db: SQLiteDatabase) {
    tables.foreach(table => table.onCreate(db))
  }
}
