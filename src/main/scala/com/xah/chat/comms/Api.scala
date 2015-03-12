package com.xah.chat.comms

import retrofit.Callback
import retrofit.http.{Body, POST}

/**
 * Project: xaHChat
 * Created on 2015-03-11 by
 * lemonxah -
 * https://github.com/lemonxah
 * http://stackoverflow.com/users/2919672/lemon-xah 
 */
case class User(username: String, password: String)
case class Response(username: String, status: String, token: Option[String] = None)

trait Api {
  @POST("/register")
  def register(@Body user: User, callback: Callback[Response])

  @POST("/login")
  def login(@Body user: User, callback: Callback[Response])
}
