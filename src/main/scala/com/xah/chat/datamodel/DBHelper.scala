package com.xah.chat.datamodel

import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.content.Context
import com.xah.chat.datamodel.tables.MessagesHelper

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/19
 * Time: 3:32 PM
 */
class DBHelper(context: Context) extends SQLiteOpenHelper(context, "xahchat.db", null, 2) {
  val tables = new MessagesHelper :: Nil

  def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    tables.foreach(_.onUpgrade(db, oldVersion, newVersion))
  }

  def onCreate(db: SQLiteDatabase) {
    tables.foreach(_.onCreate(db))
  }
}
