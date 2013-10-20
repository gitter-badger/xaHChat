package com.xah.chat.ui.fragments

import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.datamodel.tables._
import android.app.LoaderManager
import android.database.Cursor
import android.content.{ContentValues, CursorLoader, Loader}
import com.xah.chat.ui.adapters.ChatCursorAdapter
import android.widget.{Button, EditText, ListView}
import android.view.View.OnClickListener

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/16
 * Time: 8:45 PM
 */
class ChatFragment extends BaseFragment with LoaderManager.LoaderCallbacks[Cursor] {
  var mAdapter: ChatCursorAdapter = _
  var chatList: ListView = _

  implicit def funToRunnable(f: () => Unit) = new Runnable {
    def run() {
      f()
    }
  }

  implicit def funToOnClick[T](f: View => T) = new OnClickListener {
    def onClick(v: View) {
      f(v)
    }
  }

  def runOnUi(action: Runnable) {
    getActivity.runOnUiThread(action)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    getActivity.getActionBar.setTitle(getArguments.getString("chat_name"))
    val view = inflater.inflate(R.layout.chat_fragment, container, false)
    chatList = view.findViewById(R.id.chat_list).asInstanceOf[ListView]

    val chatText = view.findViewById(R.id.chat_text).asInstanceOf[EditText]
    val send = view.findViewById(R.id.send_chat).asInstanceOf[Button]

    send.setOnClickListener((v: View) => {
      val msg = chatText.getText.toString
      val json = mService.send(msg)
      chatText.setText("")
      val msgValues = new ContentValues()
      msgValues.put(MessageFields.MCName.toString, "lemonxah")
      msgValues.put(MessageFields.Message.toString, msg)
      msgValues.put(MessageFields.ServerName.toString, "xaHCraft")
      msgValues.put(MessageFields.MessageType.toString, MessageType.ServerMessage.toString)
      msgValues.put(MessageFields.MessageId.toString, json.messageId)
      msgValues.put(MessageFields.Time.toString, json.timestamp.toString)
      getActivity.getContentResolver.update(
        Messages.CONTENT_URI, msgValues, s"${MessageFields.MessageId} = '${json.messageId}'", null
      )
    })

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
    runOnUi(() => chatList.setSelection(chatList.getCount - 1))
  }

  def onCreateLoader(id: Int, data: Bundle): Loader[Cursor] = {
    new CursorLoader(getActivity, Messages.CONTENT_URI,
      MessageFields.projection,
      s"""${getArguments.getInt("contact_type") match {
        case ContactType.Player => MessageFields.MCName
        case ContactType.Server => MessageFields.ServerName
      }} = ?""", Array(getArguments.getString("chat_name")), null)

  }
}
