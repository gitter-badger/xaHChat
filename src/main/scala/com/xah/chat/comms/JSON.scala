package com.xah.chat.comms

/**
 * Created with IntelliJ IDEA.
 * User: lemonxah
 * Date: 2013/11/04
 * Time: 1:11 PM
 */

trait JSON
case class JStr(s: String) extends JSON
case class JNum(n: Long) extends JSON
case class JObj(m: Map[String, JSON]) extends JSON
case class JBool(b: Boolean) extends JSON
case class JSeq(l: List[JSON]) extends JSON
case class JNull() extends JSON

