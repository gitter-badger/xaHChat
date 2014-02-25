package com.xah.chat.ui.fragments

import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.datamodel.tables._
import android.database.Cursor
import android.content.ContentValues
import com.xah.chat.ui.adapters.ChatCursorAdapter
import android.widget.{AbsListView, Button, EditText, ListView}
import android.view.View.OnClickListener
import com.xah.chat.datamodel.xah
import scala.language.implicitConversions
import android.support.v4.app.LoaderManager
import android.support.v4.content.{Loader, CursorLoader}

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
      val json = mService.send(msg, s"${getArguments.getString("chat_name")}/in".toLowerCase)
      chatText.setText("")
      json.messageType match {
        case MessageType.NormalMessage => {
          val msgValues = new ContentValues()
          msgValues.put(MessageFields.MCName.toString, xah.MCName(getActivity))
          msgValues.put(MessageFields.Message.toString, msg)
          if (getArguments.getInt("contact_type") == ContactType.Channel) {
            msgValues.put(MessageFields.ServerName.toString, getArguments.getString("chat_name"))
          }
          msgValues.put(MessageFields.MessageType.toString, MessageType.NormalMessage.toString)
          msgValues.put(MessageFields.MessageId.toString, json.messageId)
          msgValues.put(MessageFields.Time.toString, json.timestamp.toString)
          getActivity.getContentResolver.update(
            Messages.CONTENT_URI, msgValues, s"${MessageFields.MessageId} = '${json.messageId}'", null
          )
          chatList.post(() => {
            chatList.smoothScrollToPosition(chatList.getCount - 1)
          })
        }
        case _ =>
      }
    })
    getLoaderManager.initLoader(0, null, this)
    view
  }

  override def onResume() {
    super.onResume()
    (Option(mService), getArguments.getInt("contact_type")) match {
      case (Some(service), ContactType.Channel) => service.subscribe(s"${getArguments.getString("chat_name").toLowerCase}/out")
      case (Some(service), ContactType.Player) =>
      case _ =>
    }
  }

  def onLoaderReset(loader: Loader[Cursor]) {

  }

  def onLoadFinished(loader: Loader[Cursor], cursor: Cursor) {
    chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED)
    if (mAdapter == null) {
      mAdapter = new ChatCursorAdapter(getActivity)
    }
    chatList.setAdapter(mAdapter)
    mAdapter.changeCursor(cursor)
    chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL)
  }

  def onCreateLoader(id: Int, data: Bundle): Loader[Cursor] = {
    new CursorLoader(getActivity, Messages.CONTENT_URI,
      MessageFields.projection,
      s"""${getArguments.getInt("contact_type") match {
        case ContactType.Player => MessageFields.MCName
        case ContactType.Channel => MessageFields.ServerName
      }} = ?""", Array(getArguments.getString("chat_name")), null)

  }

  override def onSaveInstanceState(outState: Bundle) {

  }
}
