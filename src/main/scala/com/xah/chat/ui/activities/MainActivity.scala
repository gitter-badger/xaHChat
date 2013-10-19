package com.xah.chat.ui.activities

import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.ui.fragments.ContactsFragment
import android.app.Fragment

class MainActivity extends BaseActivity {
  val TAG = "com.xah.MainActivity"
  var currentFragment: Fragment = _

  override def onCreate(data: Bundle): Unit = {
    super.onCreate(data)
    setContentView(R.layout.activity_main)
    if ((currentFragment = getFragmentManager.findFragmentById(R.id.content_frame)) == null) {
      currentFragment = new ContactsFragment
      getFragmentManager.beginTransaction
        .add(R.id.content_frame, currentFragment)
        .commit
    }
  }
}


