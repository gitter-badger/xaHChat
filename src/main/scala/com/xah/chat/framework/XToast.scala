package com.xah.chat.framework

import android.content.Context
import android.os.{Message, Looper, Handler}
import android.widget.Toast
import com.xah.chat.utils.StaticImports._

/**
 * Project: xaHChat
 * Created on 2015-03-02 by
 * lemonxah -
 * https://github.com/lemonxah
 * http://stackoverflow.com/users/2919672/lemon-xah 
 */
object XToast {
  def apply(text: String)(implicit context: Context) = new Handler(Looper.getMainLooper)
    .post(() => Toast.makeText(context, text, Toast.LENGTH_SHORT).show())
}
object XToastL {
  def apply(text: String)(implicit context: Context) = new Handler(Looper.getMainLooper)
    .post(() => Toast.makeText(context, text, Toast.LENGTH_LONG).show())
}