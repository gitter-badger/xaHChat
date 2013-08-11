package com.xah.chat.ui.activities

import android.app.Activity
import android.os.Bundle
import com.xah.chat.R
import com.xah.chat.ui.fragments.ContactsFragment
import com.xah.chat.datamodel.tables.Contacts
import android.content.ContentValues
import com.xah.chat.datamodel.tables.ContactFields
import android.util.Log

class MainActivity extends Activity {
	val TAG = "MainActivity"
	override def onCreate(data : Bundle) : Unit = {
		super.onCreate(data)
		setContentView(R.layout.activity_main)
		getFragmentManager().beginTransaction()
			.add(R.id.content_frame, new ContactsFragment())
			.commit()
//		for (i <- 1 to 500) {
//			val values = new ContentValues
//			values.put(ContactFields.Name.toString(), "test Name " + i)
//			values.put(ContactFields.MCName.toString(), "testMCName" + i)
//			values.put(ContactFields.Status.toString(), "status messages " + i)
//			val uri = getContentResolver().insert(Contacts.CONTENT_URI, values)
//		}
	}
}
