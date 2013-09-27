package com.xah.chat.ui.activities

import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.ui.fragments.ContactsFragment
import android.util.Log
import com.xah.chat.comms.GCM

class MainActivity extends BaseActivity {
  val TAG = "MainActivity"

  override def onCreate(data: Bundle): Unit = {
    super.onCreate(data)
    setContentView(R.layout.activity_main)
    getFragmentManager().beginTransaction()
      .add(R.id.content_frame, new ContactsFragment())
      .commit()

    // setup GCM and register only once a week
    // or re-register if the app version has changed.
    val gcm = new GCM(this)
    Option(gcm.registrationId) match {
      case Some(s) => if (s.isEmpty()) gcm.registerBackground()
      case _ => //null string should never happen
    }
    Log.d(TAG, "GCM Registration ID: " + gcm.registrationId)
  }

}


