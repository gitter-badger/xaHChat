package com.xah.chat.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.xah.chat.R
import com.xah.chat.comms.XService
import com.xah.chat.traits.{XToast, XButton}

class AddAccountActivity extends BaseActivity {
  override def onCreate(data: Bundle): Unit = {
    super.onCreate(data)
    startService(new Intent(this, classOf[XService]))
    setContentView(R.layout.add_account_activity)
    XButton(R.id.login).onClick = v => {
      XToast("do some login stuff")
    }
    XButton(R.id.register).onClick = v => {
      XToast("do some register stuff")
    }
  }
}
