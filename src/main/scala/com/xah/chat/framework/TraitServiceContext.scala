package com.xah.chat.framework

import android.app.Service
import android.content.Context

/**
 * Project: xaHChat
 * Created on 2015-02-25 by
 * lemonxah -
 * https://github.com/lemonxah
 * http://stackoverflow.com/users/2919672/lemon-xah 
 */
trait TraitServiceContext {
  this: Service =>
  implicit lazy val context: Context = this.getBaseContext
}
