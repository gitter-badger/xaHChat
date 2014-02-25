package com.xah.chat.ui.adapters

import android.database.Cursor
import android.content.{DialogInterface, Context}
import android.view.ViewGroup
import android.view.View
import android.widget._
import scala.language.implicitConversions
import android.app.AlertDialog
import com.xah.chat.datamodel.tables.{ContactType, ContactFields}
import com.xah.chat.R
import com.squareup.picasso.Picasso
import android.text.TextUtils
import android.view.View.OnClickListener
import android.os.Bundle
import com.xah.chat.ui.fragments.ChatFragment
import android.support.v4.app.FragmentActivity

class ContactsCursorAdapter(context: FragmentActivity) extends CursorAdapter(context, null, false) {
  val layoutInflater = context.getLayoutInflater
  val TAG = "com.xah.ContactsCursorAdapter"

  implicit def funToOnClickListener[F](f: View => F) = new OnClickListener {
    def onClick(v: View) {
      f(v)
    }
  }

  //implicit def funToDialogClickListener[F](f: (DialogInterface, Int => F)) =

  def newView(arg0: Context, arg1: Cursor, arg2: ViewGroup): View = {
    layoutInflater.inflate(R.layout.contact_row, null)
  }

  def bindView(v: View, c: Context, cursor: Cursor): Unit = {
    val t1 = v.findViewById(R.id.mcname).asInstanceOf[TextView]
    val t2 = v.findViewById(R.id.status).asInstanceOf[TextView]
    val avatar = v.findViewById(R.id.avatar).asInstanceOf[ImageView]
    val mcname = cursor.getString(cursor.getColumnIndex(ContactFields.ContactName.toString))
    val status = cursor.getString(cursor.getColumnIndex(ContactFields.Status.toString))
    t1.setText(mcname)
    if (!TextUtils.isEmpty(status)) t2.setText(status)
    val contactType = cursor.getInt(cursor.getColumnIndex(ContactFields.ContactType.toString))
    contactType match {
      case ContactType.Player => {
        Picasso.`with`(context)
          .load(s"https://minotar.net/helm/$mcname/116.png")
          .into(avatar)
        v.setOnClickListener(null)
      }
      case ContactType.Channel => {
        Picasso.`with`(context)
          .load(R.drawable.server)
          .into(avatar)
      }
    }
  }
}