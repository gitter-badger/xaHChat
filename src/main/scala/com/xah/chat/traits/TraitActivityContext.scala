package com.xah.chat.traits

import android.app.Activity
import android.content.Context
import android.view.View

/**
 * everything with context
 * Created by lemonxah on 2014/10/23.
 */
trait TraitActivityContext[V <: Activity] {
  this: Activity =>
  implicit lazy val context: V = this.asInstanceOf[V]
  implicit lazy val contentView: View = this.getWindow.getDecorView.getRootView
}
