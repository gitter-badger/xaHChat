package com.xah.chat.ui.adapters

import android.database.Cursor
import android.content.Context
import android.view.ViewGroup
import android.view.View
import android.widget.CursorAdapter
import android.app.Activity
import android.widget.TextView
import com.xah.chat.datamodel.tables.ContactFields

class ContactsCursorAdapter(context: Activity) extends CursorAdapter(context, null, false) {
  val layoutInflater = context.getLayoutInflater
  val TAG = "ContactsCursorAdapter"

  def newView(arg0: Context, arg1: Cursor, arg2: ViewGroup): View = {
    layoutInflater.inflate(android.R.layout.simple_list_item_2, null)
  }

  def bindView(v: View, c: Context, cursor: Cursor): Unit = {
    val t1 = v.findViewById(android.R.id.text1).asInstanceOf[TextView]
    val t2 = v.findViewById(android.R.id.text2).asInstanceOf[TextView]
    t1.setText(cursor.getString(cursor.getColumnIndex(ContactFields.MCName.toString)))
    t2.setText(cursor.getString(cursor.getColumnIndex(ContactFields.Status.toString)))
  }
}