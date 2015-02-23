package com.xah.chat.ui.activities

import android.content.Intent
import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.comms.XService

class RegisterActivity extends BaseActivity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    startService(new Intent(this, classOf[XService]))
    setContentView(R.layout.register_activity)
  }
}
