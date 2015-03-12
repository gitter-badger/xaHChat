package com.xah.chat.ui.activities

import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.datamodel.xah
import com.xah.chat.framework.{XActivityStart, XServiceStart}
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
    if(xah.Handle.isEmpty) {
      XActivityStart(classOf[AddAccountActivity])
      finish()
    }
    else {
      XServiceStart(classOf[XService])
      setContentView(R.layout.activity_main)
      Log.d(TAG, s"DeviceId: $mDeviceId")
    }
  }
}