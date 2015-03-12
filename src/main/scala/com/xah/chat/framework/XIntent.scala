package com.xah.chat.framework

import android.app.{Service, Activity}
import android.content.Intent

/**
 * Project: xaHChat
 * Created on 2015-03-11 by
 * lemonxah -
 * https://github.com/lemonxah
 * http://stackoverflow.com/users/2919672/lemon-xah 
 */
object XIntent {
  def apply[T](c: Class[T])(implicit context: Activity) = new Intent(context, c)
}
class XActivity[T <: Activity](c: Class[T])(context: Activity) {
  lazy val intent = XIntent(c)(context)
  def start() = context.startActivity(intent)
}
object XActivity {
  def apply[T <: Activity](c: Class[T])(implicit context: Activity) = new XActivity[T](c)(context)
}
object XActivityStart {
  def apply[T <: Activity](c: Class[T])(implicit context: Activity) = new XActivity[T](c)(context).start()
}
object XServiceStart {
  def apply[T <: Service](c: Class[T])(implicit context: Activity) = context.startService(XIntent[T](c)(context))
}