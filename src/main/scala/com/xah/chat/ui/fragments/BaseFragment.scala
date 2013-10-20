package com.xah.chat.ui.fragments

import android.app.Fragment
import com.xah.chat.ui.activities.BaseActivity

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/16
 * Time: 8:48 PM
 */
class BaseFragment extends Fragment {
  def mService = getActivity.asInstanceOf[BaseActivity].mConnection.getService
}
