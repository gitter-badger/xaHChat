package com.xah.chat.utils

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/19
 * Time: 7:47 PM
 */
class BooleanExtractor[T](f: T => Boolean) {
  def unapply(t: T) = f(t)
}
