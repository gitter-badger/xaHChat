package com.xah.chat.ui.activities

import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.ui.fragments.ContactsFragment
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
    if (TextUtils.isEmpty(MCName)) MCName = xah.MCName(this)
    val prefs = getSharedPreferences(xah.SHAREDPREFS, Context.MODE_PRIVATE)
    if (!prefs.contains(xah.PREF_MCNAME) || TextUtils.isEmpty(MCName)) {
      val intent = new Intent(this, classOf[LoginActivity])
      startActivity(intent)
      this.finish()
    } else {
      startService(new Intent(this, classOf[XService]))
    }

    setContentView(R.layout.activity_main)
    if (getFragmentManager.findFragmentById(R.id.content_frame) == null) {
      getFragmentManager.beginTransaction
        .add(R.id.content_frame, new ContactsFragment)
        .commit
    }
  }
}


