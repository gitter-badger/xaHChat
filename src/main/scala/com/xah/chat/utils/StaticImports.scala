package com.xah.chat.utils

import android.view.View
import android.view.View.OnClickListener
import com.xah.chat.traits.TraitView
import language.implicitConversions

/**
 * Static implicit imports for implicit conversions
 * Created by lemonxah on 2014/08/25.
 */
object StaticImports {
  implicit def funToRunnable(fun: () => Any): Runnable = new Runnable {
    def run() = fun()
  }
  implicit def funToOnClick(fun: (View) => Any): OnClickListener = new OnClickListener {
    override def onClick(v: View): Unit = fun(v)
  }
  implicit def viewWithTraitView(ov: View): TraitView[View] = new TraitView[View] {
    val id = 0
    val v: View = ov
    override def view: View = ov
  }
  implicit class ViewExtender(v: View) {
    def find[A](id: Int) = v.findViewById(id).asInstanceOf[A]
  }
}
