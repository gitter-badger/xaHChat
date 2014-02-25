package com.xah.chat.ui.fragments

import android.view.{View, LayoutInflater, ViewGroup}
import android.os.Bundle
import com.xah.chat.R
import android.widget._
import android.database.Cursor
import scala.language.implicitConversions
import com.xah.chat.datamodel.tables.{ContactType, Contacts, ContactFields}
import com.xah.chat.ui.adapters.ContactsCursorAdapter
import android.util.Log
import android.support.v4.app.LoaderManager
import android.support.v4.content.{Loader, CursorLoader}
import android.content.DialogInterface
import android.app.AlertDialog
import android.widget.AdapterView.OnItemClickListener


class ContactsFragment extends BaseFragment with LoaderManager.LoaderCallbacks[Cursor] {
  var contacts_list: ListView = _
  var mAdapter: ContactsCursorAdapter = _
  val TAG = "com.xah.ContactsFragment"

  implicit def funToOnItemClick[T](f: (AdapterView[_], View, Int, Long) => T) = new OnItemClickListener {
    def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
      f(parent, view, position, id)
    }
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle) = {
    getActivity.getActionBar.setTitle("Server List")
    val view = inflater.inflate(R.layout.contacts_fragment, null)
    contacts_list = view.findViewById(R.id.contacts_list).asInstanceOf[ListView]
    getLoaderManager().initLoader(0, null, this)
    view
  }

  override def onCreateLoader(id: Int, data: Bundle) = {
    Log.d(TAG, ContactFields.projection.toString)
    new CursorLoader(getActivity, Contacts.CONTENT_URI,
      ContactFields.projection, s"${ContactFields.ContactType} = ${ContactType.Channel}", null, null)
  }

  override def onLoadFinished(loader: Loader[Cursor], cursor: Cursor) = {
    if (mAdapter == null) {
      mAdapter = new ContactsCursorAdapter(getActivity)
    }
    contacts_list.setAdapter(mAdapter)
    contacts_list.setOnItemClickListener((parent: AdapterView[_], view: View, position: Int, id: Long) => {
      val c = contacts_list.getItemAtPosition(position).asInstanceOf[Cursor]
      val extras = new Bundle()
      val mcname = c.getString(cursor.getColumnIndex(ContactFields.ContactName.toString))
      val contactType = c.getInt(cursor.getColumnIndex(ContactFields.ContactType.toString))
      val password = cursor.getString(cursor.getColumnIndex(ContactFields.ChannelPassword.toString))

      extras.putString("chat_name", mcname)
      extras.putInt("contact_type", contactType)
      if (contactType == ContactType.Channel) {
        if (password != null && password.trim.replace("null", "") != "") {
          val pview = getLayoutInflater(null).inflate(R.layout.server_password, null)
          val passwordField = pview.findViewById(R.id.server_password).asInstanceOf[EditText]
          val adb = new AlertDialog.Builder(getActivity)
          adb.setView(pview)
            .setCancelable(true)
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener {
            def onClick(p1: DialogInterface, p2: Int) = {

            }
          }).setPositiveButton("OK", new DialogInterface.OnClickListener {
            def onClick(p1: DialogInterface, p2: Int) = {
              if (password == passwordField.getText.toString) {
                switchFragment(extras)
              } else {
                Toast.makeText(getActivity, "Server password incorrect.", Toast.LENGTH_SHORT)
              }
            }
          }).create().show()
        } else {
          switchFragment(extras)
        }
      } else {
        switchFragment(extras)
      }
    })
    mAdapter.changeCursor(cursor)
  }

  def switchFragment(extras: Bundle) {
    val fragment = new ChatFragment
    fragment.setArguments(extras)
    getFragmentManager.beginTransaction
      .setTransition(android.R.anim.slide_in_left)
      .replace(R.id.content_frame, fragment, "openchat")
      .addToBackStack("chat opened")
      .commit
  }
  override def onLoaderReset(loader: Loader[Cursor]) = {

  }
} 