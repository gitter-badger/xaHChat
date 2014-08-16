package com.xah.chat.ui.activities

import android.os.Bundle
import android.view.Window
import com.xah.chat.R

/**
 * Created by Ryno on 2014-08-15.
 */
class AddAccountActivity extends BaseActivity {
  override def onCreate(data: Bundle): Unit = {
    super.onCreate(data)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(R.layout.add_account_activity)
  }

  def login(): Unit = {

  }

  def register(): Unit = {

  }
}
