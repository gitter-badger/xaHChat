package com.xah.chat.ui.activities

import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.framework.XServiceStart
import scala.language.implicitConversions
import android.content.{Intent, Context}
import com.xah.chat.comms.XService
import android.util.Log
import android.widget.{ArrayAdapter, ListView}
import android.view.{LayoutInflater, ViewGroup, View}

class MainActivity extends BaseActivity {
  val TAG = "com.xah.MainActivity"
  var Handle: String = _

  override def onCreate(data: Bundle): Unit = {
    super.onCreate(data)
    XServiceStart[XService]
    setContentView(R.layout.activity_main)
    Log.d(TAG, s"DeviceId: $mDeviceId")
  }
}