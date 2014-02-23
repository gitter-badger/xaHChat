package com.xah.chat.datamodel

import android.database.sqlite.SQLiteDatabase

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/19
 * Time: 3:48 PM
 */
trait TableHelper {
  def onCreate(db: SQLiteDatabase): Unit

  def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit
}
