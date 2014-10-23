package com.xah.chat.datamodel

import android.content.Context

object xah {
  val AUTHORITY = "com.xah.chat.data"
  val SHAREDPREFS = "com.xah.chat.prefs"

  def Handle(implicit context: Context): String =
    context.getSharedPreferences(xah.SHAREDPREFS, Context.MODE_PRIVATE).getString(xah.PREF_HANDLE, "")

  val PREF_HANDLE = "pref_handle"
}