package com.xah.chat.ui.activities

import android.app.Activity
import android.os.Bundle
import com.xah.chat.R
import android.widget.{Button, EditText}
import android.view.View
import android.view.View.OnClickListener
import java.net.URL
import java.util.Scanner

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

  implicit def funToOnClick[T](f: View => T) = new OnClickListener {
    def onClick(v: View) {
      f(v)
    }
  }

  override def onCreate(savedInstanceState: Bundle) {
    setContentView(R.layout.login_activity)
    val username = findViewById(R.id.username).asInstanceOf[EditText]
    val password = findViewById(R.id.password).asInstanceOf[EditText]
    val login = findViewById(R.id.login).asInstanceOf[Button]

    login.setOnClickListener((v: View) => {
      login.setEnabled(false)
      val connection = new URL(s"http://login.minecraft.net/?user=${username.getText}&password=${password.getText}&version=13").openConnection
      connection.setConnectTimeout(3000)
      connection.setReadTimeout(4000)
      connection.connect
      val scanner = new Scanner(connection.getInputStream).useDelimiter("\\A")
      val response = if (scanner.hasNext) scanner.next else ""

      login.setEnabled(true)
    })
  }
}
