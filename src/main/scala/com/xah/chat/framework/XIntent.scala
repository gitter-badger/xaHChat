package com.xah.chat.framework

import android.app.Activity
import android.content.Intent

/**
 * Project: xaHChat
 * Created on 2015-03-11 by
 * lemonxah -
 * https://github.com/lemonxah
 * http://stackoverflow.com/users/2919672/lemon-xah 
 */
object XIntent {
  def apply[T <: Activity](implicit context: Activity) = new Intent(context, Class[T])
}
class XActivity[T <: Activity](context: Activity) {
  lazy val intent = XIntent[T](context)
  def start() = context.startActivity(intent)
}
object XActivity {
  def apply[T <: Activity](implicit context: Activity) = new XActivity[T](context)
}

