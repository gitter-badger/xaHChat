package com.xah.chat.utils

import android.view.View
import android.view.View.OnClickListener
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
  implicit def viewWithTraitView(v: View): TraitView[View] = new TraitView[View] {
    override def view: View = v
  }
  implicit class ViewExtender(v: View) {
    def find[A](id: Int) = v.findViewById(id).asInstanceOf[A]
  }
}
