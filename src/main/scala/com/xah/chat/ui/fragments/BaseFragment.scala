package com.xah.chat.ui.fragments

import android.app.Activity
import android.support.v4.app.{FragmentActivity, Fragment}
import com.xah.chat.R
import com.xah.chat.traits.TraitFragmentContext
import com.xah.chat.ui.activities.BaseActivity

/**
 * some fragment helpers
 * Created by lemonxah on 2014/10/10.
 */
abstract class BaseFragment extends Fragment with TraitFragmentContext {
  def TAG: String
  private[BaseFragment] def trans = context.getSupportFragmentManager.beginTransaction
  def show(): Unit = show(R.id.content_frame)
  def show(viewId: Int): Unit = trans.replace(viewId, this, TAG).addToBackStack(TAG).commit()
  def showNoBackStack(): Unit = showNoBackStack(R.id.content_frame)
  def showNoBackStack(viewId: Int): Unit = trans.replace(viewId, this, TAG).commit()
  def add(): Unit = add(R.id.content_frame)
  def add(viewId: Int): Unit = trans.add(viewId, this, TAG).commit()
}