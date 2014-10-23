package com.xah.chat.utils

import android.view.View
import android.view.View.OnClickListener
import android.widget.{ViewAnimator, EditText, Button, TextView}
import StaticImports._

/**
 * Some View Helpers
 * Created by lemonxah on 2014/10/08.
 */

trait TraitView[V <: View] {
  def view: V
}

trait TraitTextView[V <: TextView] extends TraitView[V] {
  def text_=(p: CharSequence) = view.setText(p)
  def text  (p: CharSequence) = text_=(p)
  def text = view.getText
}

trait TraitButton[V <: Button] extends TraitTextView[V] {
  var onClickListenerLambda: View => Unit = _
  def onClick_=(f: View => Unit) = {
    onClickListenerLambda = f
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = f(v)
    })
  }
  def onClick(f: View => Unit) = onClick_=(f)
  def onClick = onClickListenerLambda
}

trait TraitEditText[V <: EditText] extends TraitTextView[V]

trait TraitViewAnimator[V <: ViewAnimator] extends TraitView[V] {
  def currentView = view.getCurrentView
  def showNext() = view.showNext()
  def showPrevious() = view.showPrevious()
}

class XButton(v: View, id: Int) extends TraitButton[Button] {
  def view = v.find[Button](id)
}
class XTextView(v: View, id: Int) extends TraitTextView[TextView] {
  def view = v.find[TextView](id)
}
class XEditText(v: View, id: Int) extends TraitEditText[EditText] {
  def view = v.find[EditText](id)
}
class XViewAnimator(v: View, id: Int) extends TraitViewAnimator[ViewAnimator] {
  def view = v.find[ViewAnimator](id)
}

object XTextView {
  def apply(id: Int)(implicit view: View) = new XTextView(view, id)
}
object XButton {
  def apply(id: Int)(implicit view: View) = new XButton(view, id)
  def apply(id: Int, onclick: View => Unit)(implicit view: View): XButton = {
    val b = apply(id)(view)
    b.onClick = onclick
    b
  }
}
object XEditText {
  def apply(id: Int)(implicit view: View) = new XEditText(view, id)
}
object XViewAnimator {
  def apply(id: Int)(implicit view: View) = new XViewAnimator(view, id)
}
