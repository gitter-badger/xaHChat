package com.xah.chat.ui.fragments

import android.view.{View, ViewGroup, LayoutInflater}
import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.datamodel.tables._
import android.app.LoaderManager
import android.database.Cursor
import android.content.{ContentValues, CursorLoader, Loader}
import com.xah.chat.ui.adapters.ChatCursorAdapter
import android.widget.{AbsListView, Button, EditText, ListView}
import android.view.View.OnClickListener
import com.xah.chat.datamodel.xah
import android.widget.AbsListView.OnScrollListener

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/16
 * Time: 8:45 PM
 */
class ChatFragment extends BaseFragment with LoaderManager.LoaderCallbacks[Cursor] {
  var mAdapter: ChatCursorAdapter = _
  var chatList: ListView = _
  var firstOpen = true
  var listBottom = true

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

    chatList.setOnScrollListener(new OnScrollListener {
      def onScrollStateChanged(p1: AbsListView, p2: Int) {

      }

      def onScroll(lv: AbsListView, firstVisible: Int, visibleCount: Int, totalItems: Int) {
        lv.getId() match {
          case R.id.chat_list => {
            val lastItem = firstVisible + visibleCount;
            listBottom = lastItem == totalItems
          }
        }
      }
    })

    send.setOnClickListener((v: View) => {
      val msg = chatText.getText.toString
      val json = mService.send(msg, s"${getArguments.getString("chat_name")}/in".toLowerCase)
      chatText.setText("")
      val msgValues = new ContentValues()
      msgValues.put(MessageFields.MCName.toString, xah.MCName(getActivity))
      msgValues.put(MessageFields.Message.toString, msg)
      if (getArguments.getInt("contact_type") == ContactType.Server) {
        msgValues.put(MessageFields.ServerName.toString, getArguments.getString("chat_name"))
      }
      msgValues.put(MessageFields.MessageType.toString, MessageType.NormalMessage.toString)
      msgValues.put(MessageFields.MessageId.toString, json.messageId)
      msgValues.put(MessageFields.Time.toString, json.timestamp.toString)
      getActivity.getContentResolver.update(
        Messages.CONTENT_URI, msgValues, s"${MessageFields.MessageId} = '${json.messageId}'", null
      )
      chatList.setSelection(chatList.getCount - 1)
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
    // save index and top position

    var index: Int = -1
    var top: Int = -1
    runOnUi(() => {
      index = chatList.getFirstVisiblePosition
      val v = chatList.getChildAt(0)
      top = if (v == null) 0 else v.getTop
    })

    // ...
    // restore

    if (mAdapter == null) {
      mAdapter = new ChatCursorAdapter(getActivity)
    }
    chatList.setAdapter(mAdapter)
    mAdapter.changeCursor(cursor)
    runOnUi(() => {
      if (firstOpen) {
        chatList.setSelection(chatList.getCount - 1)
        firstOpen = false
      } else if (listBottom) {
        chatList.setSelection(chatList.getCount - 1)
      } else {
        chatList.setSelectionFromTop(index, top);
      }
    })
  }

  def onCreateLoader(id: Int, data: Bundle): Loader[Cursor] = {
    new CursorLoader(getActivity, Messages.CONTENT_URI,
      MessageFields.projection,
      s"""${getArguments.getInt("contact_type") match {
        case ContactType.Player => MessageFields.MCName
        case ContactType.Server => MessageFields.ServerName
      }} = ?""", Array(getArguments.getString("chat_name")), null)

  }

  override def onSaveInstanceState(outState: Bundle) {

  }
}
