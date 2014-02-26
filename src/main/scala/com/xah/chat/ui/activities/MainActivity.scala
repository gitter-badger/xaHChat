package com.xah.chat.ui.activities

import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.datamodel.xah
import scala.language.implicitConversions
import android.content.{Intent, Context}
import android.text.TextUtils
import com.xah.chat.comms.XService

class MainActivity extends BaseActivity {
  val TAG = "com.xah.MainActivity"
  var MCName: String = _

  override def onCreate(data: Bundle): Unit = {
    super.onCreate(data)
    startService(new Intent(this, classOf[XService]))
    setContentView(R.layout.activity_main)
  }
}


