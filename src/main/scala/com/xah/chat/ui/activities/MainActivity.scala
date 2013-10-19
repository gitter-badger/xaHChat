package com.xah.chat.ui.activities

import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.ui.fragments.ContactsFragment

class MainActivity extends BaseActivity {
  val TAG = "com.xah.MainActivity"

  override def onCreate(data: Bundle): Unit = {
    super.onCreate(data)
    setContentView(R.layout.activity_main)
    if (getFragmentManager.findFragmentById(R.id.content_frame) == null) {
      getFragmentManager.beginTransaction
        .add(R.id.content_frame, new ContactsFragment)
        .commit
    }
  }
}


