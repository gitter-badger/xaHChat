package com.xah.chat.ui.adapters

import android.database.Cursor
import android.content.{DialogInterface, Context}
import android.view.ViewGroup
import android.view.View
import android.widget._
import android.app.{AlertDialog, Activity}
import com.xah.chat.datamodel.tables.{ContactType, ContactFields}
import com.xah.chat.R
import com.squareup.picasso.Picasso
import android.text.TextUtils
import android.view.View.OnClickListener
import android.os.Bundle
import com.xah.chat.ui.fragments.ChatFragment

class ContactsCursorAdapter(context: Activity) extends CursorAdapter(context, null, false) {
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
    val mcname = cursor.getString(cursor.getColumnIndex(ContactFields.MCName.toString))
    val status = cursor.getString(cursor.getColumnIndex(ContactFields.Status.toString))
    val password = cursor.getString(cursor.getColumnIndex(ContactFields.ServerPassword.toString))
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
      case ContactType.Server => {
        Picasso.`with`(context)
          .load(R.drawable.server)
          .into(avatar)
        v.setOnClickListener((view: View) => {
          val extras = new Bundle()
          extras.putString("chat_name", mcname)
          extras.putInt("contact_type", contactType)
          if (contactType == ContactType.Server) {
            if (password != null && password.trim.replace("null", "") != "") {
              val pview = layoutInflater.inflate(R.layout.server_password, null)
              val passwordField = pview.findViewById(R.id.server_password).asInstanceOf[EditText]
              val adb = new AlertDialog.Builder(context)
              adb.setView(pview)
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener {
                def onClick(p1: DialogInterface, p2: Int) = {

                }
              }).setPositiveButton("OK", new DialogInterface.OnClickListener {
                def onClick(p1: DialogInterface, p2: Int) = {
                  if (password == passwordField.getText.toString) {
                    switchFragment(extras)
                  } else {
                    Toast.makeText(context, "Server password incorrect.", Toast.LENGTH_SHORT)
                  }
                }
              }).create().show()
            } else {
              switchFragment(extras)
            }
          } else {
            switchFragment(extras)
          }
        })
      }
    }
  }

  def switchFragment(extras: Bundle) {
    val fragment = new ChatFragment
    fragment.setArguments(extras)
    context.getFragmentManager.beginTransaction
      .replace(R.id.content_frame, fragment)
      .addToBackStack(null)
      .commit
  }

}