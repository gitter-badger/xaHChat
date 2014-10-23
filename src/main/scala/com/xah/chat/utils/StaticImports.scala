package com.xah.chat.utils

import android.view.View
import android.view.View.OnClickListener

/**
 * Static implicit imports for implicit conversions
 * Created by lemonxah on 2014/08/25.
 */
object StaticImports {
  implicit def funToRunnable(fun: () => Any) = new Runnable {
    def run() = fun()
  }
  implicit def funToOnClick(fun: (View) => Any) = new OnClickListener {
    override def onClick(v: View): Unit = fun(v)
  }
  implicit def viewWithTraitView(v: View) = new TraitView[View] {
    override def view: View = v
  }
  implicit class ViewExtender(v: View) {
    def find[A](id: Int) = v.findViewById(id).asInstanceOf[A]
  }
}
