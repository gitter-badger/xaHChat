package com.xah.chat.ui.activities

import android.content.Intent
import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.comms.XService

class AddAccountActivity extends BaseActivity {
  override def onCreate(data: Bundle): Unit = {
    super.onCreate(data)
    startService(new Intent(this, classOf[XService]))
    setContentView(R.layout.add_account_activity)
  }
}
