package com.xah.chat.framework

import android.app.Fragment
import android.view.View
import com.xah.chat.R
import com.xah.chat.ui.activities.BaseActivity

/**
 * everything with context
 * Created by lemonxah on 2014/10/23.
 */
trait TraitFragmentContext {
  this: Fragment =>
  implicit lazy val context: BaseActivity = this.getActivity.asInstanceOf[BaseActivity]
  implicit lazy val contentView: View = this.getView
  private lazy val TAG = this.getClass.getCanonicalName
  private def trans = context.getFragmentManager.beginTransaction
  def show(): Unit = show(R.id.content_frame)
  def show(viewId: Int): Unit = trans.replace(viewId, this, TAG).addToBackStack(TAG).commit()
  def showNoBackStack(): Unit = showNoBackStack(R.id.content_frame)
  def showNoBackStack(viewId: Int): Unit = trans.replace(viewId, this, TAG).commit()
  def add(): Unit = add(R.id.content_frame)
  def add(viewId: Int): Unit = trans.add(viewId, this, TAG).commit()
}
