package com.xah.chat.ui.adapters

import android.app.Activity
import android.widget.{ImageView, TextView, CursorAdapter}
import android.content.Context
import android.database.Cursor
import android.view.{View, ViewGroup}
import com.xah.chat.datamodel.tables.{MessageFields, ContactFields}
import com.xah.chat.R
import com.squareup.picasso.Picasso

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/16
 * Time: 9:05 PM
 */
class ChatCursorAdapter(context: Activity) extends CursorAdapter(context, null, false) {
  val layoutInflater = context.getLayoutInflater
  val TAG = "com.xah.ChatCursorAdapter"

  implicit def messageValueToString(v: MessageFields.Value) = v.toString

  implicit def contactValueToString(v: ContactFields.Value) = v.toString

  implicit def messageValueToInt(v: MessageFields.Value) = v.id

  implicit def contactValueToInt(v: ContactFields.Value) = v.id

  def newView(context: Context, cursor: Cursor, parent: ViewGroup): View = {
    getItemViewType(cursor) match {
      case 0 =>
        layoutInflater.inflate(R.layout.chat_me, parent, false)
      case 1 =>
        layoutInflater.inflate(R.layout.chat_them, parent, false)
    }
  }

  override def getViewTypeCount = 2

  override def getItemViewType(position: Int): Int = {
    getItemViewType(getItem(position).asInstanceOf[Cursor])
  }

  def getItemViewType(cursor: Cursor): Int = {
    if (cursor.getString(cursor.getColumnIndex(MessageFields.MCName)) == "lemonxah") {
      0
    } else {
      1
    }
  }

  def bindView(v: View, c: Context, cursor: Cursor): Unit = {
    val avatar = v.findViewById(R.id.avatar).asInstanceOf[ImageView]
    val chatText = v.findViewById(R.id.chat_text).asInstanceOf[TextView]
    chatText.setText(s"${cursor.getString(cursor.getColumnIndex(MessageFields.MCName))}: ${cursor.getString(cursor.getColumnIndex(MessageFields.Message))}")
    Picasso.`with`(context)
      .load(s"https://minotar.net/helm/${cursor.getString(cursor.getColumnIndex(MessageFields.MCName))}/116.png")
      .into(avatar)
  }
}