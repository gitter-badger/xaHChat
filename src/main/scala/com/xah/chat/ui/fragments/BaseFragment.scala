package com.xah.chat.ui.fragments

import android.app.Activity
import android.support.v4.app.{FragmentActivity, Fragment}
import com.xah.chat.R
import com.xah.chat.traits.TraitContext
import com.xah.chat.ui.activities.BaseActivity

/**
 * some fragment helpers
 * Created by lemonxah on 2014/10/10.
 */
abstract class BaseFragment extends Fragment with TraitContext[BaseActivity] {
  def TAG: String
  private[BaseFragment] var mActivity: BaseActivity = _
  private[BaseFragment] def trans = basis.getSupportFragmentManager.beginTransaction
  override def onAttach(activity: Activity): Unit = mActivity = activity.asInstanceOf[BaseActivity]
  override def basis: BaseActivity = mActivity
  def show(): Unit = show(R.id.content_frame)
  def show(viewId: Int): Unit = trans.replace(viewId, this, TAG).addToBackStack(TAG).commit()
  def showNoBackStack(): Unit = showNoBackStack(R.id.content_frame)
  def showNoBackStack(viewId: Int): Unit = trans.replace(viewId, this, TAG).commit()
  def add(): Unit = add(R.id.content_frame)
  def add(viewId: Int): Unit = trans.add(viewId, this, TAG).commit()
}