package com.xah.chat.framework

import android.view.View
import android.view.View.OnClickListener
import android.widget.{Button, EditText, TextView, ViewAnimator}
import com.xah.chat.utils.StaticImports._
import language.implicitConversions


/**
 * Some View Helpers
 * Created by lemonxah on 2014/10/08.
 */


trait TraitView[V <: View] {
  def id: Int
  def v: View
  def view: V = v.find[V](id)
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

class XButton(val v: View, val id: Int) extends TraitButton[Button]
class XTextView(val v: View, val id: Int) extends TraitTextView[TextView]
class XEditText(val v: View, val id: Int) extends TraitEditText[EditText]
class XViewAnimator(val v: View, val id: Int) extends TraitViewAnimator[ViewAnimator]

object ViewConversions {
  implicit def x2v[V <: View](x: TraitView[V]) : V  = x.view
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
