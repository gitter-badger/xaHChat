package com.xah.chat.ui.fragments

import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.datamodel.tables._
import android.app.LoaderManager
import android.database.Cursor
import android.content.{CursorLoader, Loader}
import com.xah.chat.ui.adapters.ChatCursorAdapter
import android.widget.ListView
import android.provider.BaseColumns

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/16
 * Time: 8:45 PM
 */
class ChatFragment extends BaseFragment with LoaderManager.LoaderCallbacks[Cursor] {
  var mAdapter: ChatCursorAdapter = _
  var chatList: ListView = _

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    getActivity.getActionBar.setTitle(getArguments.getString("chat_name"))
    val view = inflater.inflate(R.layout.chat_fragment, container, false)
    chatList = view.findViewById(R.id.chat_list).asInstanceOf[ListView]
    getArguments.getInt("contact_type") match {
      case ContactType.Player =>
      case ContactType.Server =>
    }
    getLoaderManager.initLoader(0, null, this)
    view
  }

  def onLoaderReset(loader: Loader[Cursor]) {

  }

  def onLoadFinished(loader: Loader[Cursor], cursor: Cursor) {
    if (mAdapter == null) {
      mAdapter = new ChatCursorAdapter(getActivity)
    }
    chatList.setAdapter(mAdapter)
    mAdapter.changeCursor(cursor)
  }

  def onCreateLoader(id: Int, data: Bundle): Loader[Cursor] = {
    var first = true
    new CursorLoader(getActivity, Messages.MESSAGES_JOIN_CONTACTS_URI,
      for (field <- MessageFields.projection ++ ContactFields.projection)
      yield if (field == BaseColumns._ID)
        s"${if (first) Messages.TABLE_NAME else Contacts.TABLE_NAME}.${BaseColumns._ID}"
      else if (field == MessageFields.MCName.toString)
        if (first) {
          first = false
          s"${Messages.TABLE_NAME}.${MessageFields.MCName}"
        } else s"${Contacts.TABLE_NAME}.${ContactFields.MCName}"
      else field,
      s"""${getArguments.getInt("contact_type") match {
        case ContactType.Player => MessageFields.MCName
        case ContactType.Server => MessageFields.ServerName
      }} = ?""", Array(getArguments.getString("chat_name")), null)
  }
}
