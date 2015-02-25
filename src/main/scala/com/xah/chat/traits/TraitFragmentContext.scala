package com.xah.chat.traits

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.View
import com.xah.chat.ui.activities.BaseActivity

/**
 * everything with context
 * Created by lemonxah on 2014/10/23.
 */
trait TraitFragmentContext {
  this: Fragment =>
  implicit lazy val context: BaseActivity = this.getActivity.asInstanceOf[BaseActivity]
  implicit lazy val contentView: View = this.getView
}
