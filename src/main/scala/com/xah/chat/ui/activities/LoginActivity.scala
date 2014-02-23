package com.xah.chat.ui.activities

import android.app.Activity
import android.os.Bundle
import com.xah.chat.R
import scala.concurrent._
import android.widget.{Button, EditText}
import android.view.View
import android.view.View.OnClickListener
import java.net.URL
import java.util.Scanner
import android.util.Log
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global
import com.xah.chat.datamodel.xah
import android.content.{Intent, Context}

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/20
 * Time: 8:52 AM
 * http://login.minecraft.net/?user=??&password=??&version=13
 * 1374223516000:deprecated:lemonxah:b60d517295e1467489abb26921b244a3:9703a84b9d2e41ed831a96017679900e
 */
class LoginActivity extends Activity {
  val TAG = "com.xah.LoginActivity"

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
    runOnUiThread(action)
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login_activity)
    val username = findViewById(R.id.username).asInstanceOf[EditText]
    val password = findViewById(R.id.password).asInstanceOf[EditText]
    val login = findViewById(R.id.login).asInstanceOf[Button]

    login.setOnClickListener((v: View) => {
      future {
        runOnUi(() => login.setEnabled(false))
        val connection = new URL(s"http://login.minecraft.net/?user=${username.getText}&password=${password.getText}&version=13").openConnection
        connection.setConnectTimeout(3000)
        connection.setReadTimeout(4000)
        connection.connect()
        val scanner = new Scanner(connection.getInputStream).useDelimiter("\\A")
        val response = if (scanner.hasNext) scanner.next else ""
        if (response.length > 70) {
          getSharedPreferences(xah.SHAREDPREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(xah.PREF_MCNAME, response.split(":")(2))
            .commit()
          startActivity(new Intent(this, classOf[MainActivity]))
          this.finish()
        }
        Log.i(TAG, response)
        runOnUi(() => login.setEnabled(true))
      }
    })
  }
}
