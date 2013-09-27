package com.xah.chat.datamodel

import android.content.ContentProvider
import android.net.Uri
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.content.UriMatcher
import com.xah.chat.datamodel.tables.{ContactsHelper, MessagesHelper}
import android.content.{ContentValues, ContentUris}
import android.database.SQLException
import com.xah.chat.datamodel.tables.Contacts
import com.xah.chat.datamodel.tables.Messages

class DataProvider extends ContentProvider {
  val matcher = new UriMatcher(UriMatcher.NO_MATCH);
  val CONTACTS = 100
  val CONTACT = 101
  val MESSAGES = 200
  val MESSAGE = 201

  matcher.addURI(xah.AUTHORITY, "contacts", CONTACTS)
  matcher.addURI(xah.AUTHORITY, "contact/*", CONTACT)
  matcher.addURI(xah.AUTHORITY, "messages/*", MESSAGES)
  matcher.addURI(xah.AUTHORITY, "message/#", MESSAGE)

  private var mContactsHelper: ContactsHelper = _
  private var mMessagesHelper: MessagesHelper = _

  override def onCreate() = {
    mContactsHelper = new ContactsHelper(getContext)
    mMessagesHelper = new MessagesHelper(getContext)
    true
  }

  override def query(uri: Uri, projection: Array[String], selection: String, selectionArgs: Array[String], sortOrder: String): Cursor = {
    val queryBuilder = new SQLiteQueryBuilder()
    val pathSegments = uri.getPathSegments()
    val qb = new SQLiteQueryBuilder
    qb setTables getTablename(uri)
    val c = qb.query(getDb(uri), projection, selection, selectionArgs, null, null, sortOrder)
    c.setNotificationUri(getContext.getContentResolver, uri)
    c
  }

  def getTablename(uri: Uri) = matcher `match` uri match {
    case CONTACTS | CONTACT => Contacts.TABLE_NAME
    case MESSAGES | MESSAGE => Messages.TABLE_NAME
    case _ => throw new IllegalArgumentException("Unknown URI " + uri)
  }

  def getDb(uri: Uri) = matcher `match` uri match {
    case CONTACTS | CONTACT => mContactsHelper.getWritableDatabase()
    case MESSAGES | MESSAGE => mMessagesHelper.getWritableDatabase()
    case _ => throw new IllegalArgumentException("Unknown URI " + uri)
  }

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

    val rowId = getDb(uri).insert(getTablename(uri), null, values)
    if (rowId > 0) {
      val retUri = ContentUris.withAppendedId(getContentUri(uri), rowId)
      getContext.getContentResolver.notifyChange(retUri, null)
      retUri
    } else
      throw new SQLException("Failed to insert row into " + uri)
  }

  override def delete(uri: Uri, where: String, whereArgs: Array[String]): Int = {
    val db = getDb(uri)
    val count = getDb(uri).delete(Contacts.TABLE_NAME, where, whereArgs)
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
    val count = getDb(uri).update(getTablename(uri), values, where, whereArgs)
    getContext.getContentResolver.notifyChange(uri, null)
    count
  }
}