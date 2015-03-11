package com.xah.chat.comms

import retrofit.RestAdapter

/**
 * Project: xaHChat
 * Created on 2015-03-11 by
 * lemonxah -
 * https://github.com/lemonxah
 * http://stackoverflow.com/users/2919672/lemon-xah 
 */
object RestClient {
  lazy val xahbox: Api = new RestAdapter.Builder().setEndpoint("http://xahbox.com:5000").build().create(Class[Api])
}
