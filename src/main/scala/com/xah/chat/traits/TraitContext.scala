package com.xah.chat.traits

import android.content.Context

/**
 * everything with context
 * Created by lemonxah on 2014/10/23.
 */
trait TraitContext[V <: Context] {
  def basis: V
  implicit lazy val context: V = basis
}
