package com.xah.chat.ui.adapters

import android.app.Activity
import android.widget.{ImageView, TextView, CursorAdapter}
import android.content.Context
import android.database.Cursor
import scala.language.implicitConversions
import android.view.{View, ViewGroup}
import com.xah.chat.datamodel.tables.{MessageType, MessageFields, ContactFields}
import com.xah.chat.R
import com.squareup.picasso.Picasso
import com.xah.chat.datamodel.xah
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/16
 * Time: 9:05 PM
 */
class ChatCursorAdapter(context: Activity) extends CursorAdapter(context, null, false) {
  val layoutInflater = context.getLayoutInflater
  val TAG = "com.xah.ChatCursorAdapter"
  val MCName = xah.MCName(context)

  implicit def messageValueToString(v: MessageFields.Value) = v.toString

  implicit def contactValueToString(v: ContactFields.Value) = v.toString

  implicit def messageValueToInt(v: MessageFields.Value) = v.id

  implicit def contactValueToInt(v: ContactFields.Value) = v.id

  def newView(context: Context, cursor: Cursor, parent: ViewGroup): View = {
    layoutInflater.inflate(getItemViewType(cursor) match {
      case 0 => R.layout.chat_me
      case 1 => R.layout.chat_them
      case 2 => R.layout.chat_feed
    }, parent, false)
  }

  override def getViewTypeCount = 3

  override def getItemViewType(position: Int): Int = {
    getItemViewType(getItem(position).asInstanceOf[Cursor])
  }

  def getItemViewType(cursor: Cursor): Int = {
    val mcname = cursor.getString(cursor.getColumnIndex(MessageFields.MCName))
    val messageType = cursor.getInt(cursor.getColumnIndex(MessageFields.MessageType))
    messageType match {
      case MessageType.NormalMessage =>
        mcname match {
          case MCName => 0
          case _ => 1
        }
      case MessageType.FeedMessage => 2
    }
  }

  def bindView(v: View, c: Context, cursor: Cursor): Unit = {
    cursor.getInt(cursor.getColumnIndex(MessageFields.MessageType)) match {
      case MessageType.NormalMessage => {
        val avatar = v.findViewById(R.id.avatar).asInstanceOf[ImageView]
        val chatText = v.findViewById(R.id.chat_text).asInstanceOf[TextView]
        val chatSub = v.findViewById(R.id.chat_sub).asInstanceOf[TextView]
        val millis = cursor.getLong(cursor.getColumnIndex(MessageFields.Time))
        val date = new SimpleDateFormat("dd-MM HH:mm", Locale.US).format(new Date(millis))
          .replace(new SimpleDateFormat("dd-MM ", Locale.US).format(new Date()), "")
        chatSub.setText(s"${cursor.getString(cursor.getColumnIndex(MessageFields.MCName))} at $date")
        chatText.setText(cursor.getString(cursor.getColumnIndex(MessageFields.Message)))
        Picasso.`with`(context)
          .load(s"https://minotar.net/helm/${cursor.getString(cursor.getColumnIndex(MessageFields.MCName))}/116.png")
          .into(avatar)
      }
      case MessageType.FeedMessage => {
        val millis = cursor.getLong(cursor.getColumnIndex(MessageFields.Time))
        val date = new SimpleDateFormat("dd-MM HH:mm", Locale.US).format(new Date(millis))
          .replace(new SimpleDateFormat("dd-MM ", Locale.US).format(new Date()), "")
        val feed = v.findViewById(android.R.id.text1).asInstanceOf[TextView]
        feed.setText(s"${cursor.getString(cursor.getColumnIndex(MessageFields.Message))} at $date")
      }
    }
  }
}