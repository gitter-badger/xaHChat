package com.xah.chat.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.xah.chat.R
import android.widget.ListView
import android.app.LoaderManager
import android.database.Cursor
import android.content.Loader
import com.xah.chat.datamodel.tables.{Contacts, ContactFields}
import android.content.CursorLoader
import com.xah.chat.ui.adapters.ContactsCursorAdapter
import android.util.Log

class ContactsFragment extends BaseFragment with LoaderManager.LoaderCallbacks[Cursor] {
  var contacts_list: ListView = _
  var mAdapter: ContactsCursorAdapter = _
  val TAG = "ContactsFragment"

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle) = {
    val view = inflater.inflate(R.layout.contacts_fragment, null)
    contacts_list = view.findViewById(R.id.contacts_list).asInstanceOf[ListView]
    getLoaderManager().initLoader(0, null, this)
    view
  }

  override def onCreateLoader(id: Int, data: Bundle) = {
    Log.d(TAG, ContactFields.projection.toString)
    new CursorLoader(getActivity(), Contacts.CONTENT_URI, ContactFields.projection, null, null, null)
  }

  override def onLoadFinished(loader: Loader[Cursor], cursor: Cursor) = {
    if (mAdapter == null) {
      mAdapter = new ContactsCursorAdapter(getActivity())
    }
    contacts_list.setAdapter(mAdapter)
    mAdapter.changeCursor(cursor);

  }

  override def onLoaderReset(loader: Loader[Cursor]) = {

  }
} 