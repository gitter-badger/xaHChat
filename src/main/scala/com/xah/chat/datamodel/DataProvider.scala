package com.xah.chat.datamodel

import android.content.ContentProvider
import android.net.Uri
import android.database.Cursor
import scala.language.implicitConversions
import android.database.sqlite.SQLiteQueryBuilder
import android.content.UriMatcher
import android.content.{ContentValues, ContentUris}
import android.database.SQLException
import com.xah.chat.datamodel.tables.Contacts
import com.xah.chat.datamodel.tables.Messages

class DataProvider extends ContentProvider {
  val matcher = new UriMatcher(UriMatcher.NO_MATCH)
  val TAG = "com.xah.DataProvider"

  val CONTACTS = 100
  val CONTACT = 101
  val MESSAGES = 200
  val MESSAGE = 201
  val MESSAGES_JOIN_CONTACTS = 202

  matcher.addURI(xah.AUTHORITY, "contacts", CONTACTS)
  matcher.addURI(xah.AUTHORITY, "contact/*", CONTACT)
  matcher.addURI(xah.AUTHORITY, "messages", MESSAGES)
  matcher.addURI(xah.AUTHORITY, "message/#", MESSAGE)
  matcher.addURI(xah.AUTHORITY, "messages/contacts", MESSAGES_JOIN_CONTACTS)

  private var dbHelper: DBHelper = _

  override def onCreate() = {
    dbHelper = new DBHelper(getContext)
    true
  }

  override def query(uri: Uri, projection: Array[String], selection: String, selectionArgs: Array[String], sortOrder: String): Cursor = {
    val queryBuilder = new SQLiteQueryBuilder()
    val pathSegments = uri.getPathSegments
    val qb = new SQLiteQueryBuilder
    qb setTables getTablename(uri)
    val c = qb.query(getDb, projection, selection, selectionArgs, null, null, sortOrder)
    c.setNotificationUri(getContext.getContentResolver, uri)
    c
  }

  def getTablename(uri: Uri) = matcher `match` uri match {
    case CONTACTS | CONTACT => Contacts.TABLE_NAME
    case MESSAGES | MESSAGE => Messages.TABLE_NAME
    case MESSAGES_JOIN_CONTACTS =>
      s"${Messages.TABLE_NAME} JOIN ${Contacts.TABLE_NAME} on ${Contacts.TABLE_NAME}.MCName = ${Messages.TABLE_NAME}.MCName "
    case _ => throw new IllegalArgumentException("Unknown URI " + uri)
  }

  def getDb = dbHelper.getWritableDatabase

  def getContentUri(uri: Uri) = matcher `match` uri match {
    case CONTACTS => Contacts.CONTENT_URI
    case MESSAGES => Messages.CONTENT_URI
    case _ => throw new IllegalArgumentException("Unknown URI " + uri)
  }

  override def bulkInsert(uri: Uri, values: Array[ContentValues]): Int = {
    values.foreach(value => this.insert(uri, value))
    0
  }

  override def insert(uri: Uri, initialValues: ContentValues): Uri = {
    val values =
      if (initialValues != null) new ContentValues(initialValues)
      else new ContentValues()

    val now = System.currentTimeMillis.toDouble
    val rowId = getDb.insert(getTablename(uri), null, values)
    if (rowId > 0) {
      val retUri = ContentUris.withAppendedId(getContentUri(uri), rowId)
      getContext.getContentResolver.notifyChange(uri, null)
      getContext.getContentResolver.notifyChange(Messages.MESSAGES_JOIN_CONTACTS_URI, null)
      retUri
    } else
      throw new SQLException("Failed to insert row into " + uri)
  }

  override def delete(uri: Uri, where: String, whereArgs: Array[String]): Int = {
    val count = getDb.delete(Contacts.TABLE_NAME, where, whereArgs)
    getContext.getContentResolver.notifyChange(uri, null)
    count
  }

  override def getType(uri: Uri) = {
    matcher `match` uri match {
      case CONTACTS => Contacts.CONTENT_TYPE
      case CONTACT => Contacts.CONTENT_ITEM_TYPE
      case MESSAGES => Messages.CONTENT_TYPE
      case MESSAGE => Messages.CONTENT_ITEM_TYPE
      case _ => throw new IllegalArgumentException("Unknown URI " + uri)
    }
  }

  override def update(uri: Uri, values: ContentValues,
                      where: String, whereArgs: Array[String]): Int = {
    val count = getDb.update(getTablename(uri), values, where, whereArgs)
    if (count == 0) {
      insert(uri, values)
    }
    getContext.getContentResolver.notifyChange(uri, null)
    count
  }
}