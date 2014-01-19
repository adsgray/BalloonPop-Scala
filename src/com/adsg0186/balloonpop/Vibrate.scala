package com.adsg0186.balloonpop

import android.content.Context
import android.os.Vibrator

class Vibrate(context: Context) {
  // have to do this cast/asInstanceOf:
  val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]
  def vibrate(ms: Long) = vibrator.vibrate(ms)
}

object Vibrate {
  var instance: Option[Vibrate] = None

  def apply(context: Context) = {
    instance = Some(new Vibrate(context))
    instance
  }

  def vibrate(ms: Long) = instance map { v => v.vibrate(ms) }
  def get = instance
  def destroy = { instance = None }
}