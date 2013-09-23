package com.xah.chat.comms

import android.os.Binder

class XBinder(service: XService) extends Binder {
	def getService() = {
	  service
	}
}