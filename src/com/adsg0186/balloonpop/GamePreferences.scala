package com.adsg0186.balloonpop

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

case class Preferences(sound: Boolean, vibrate: Int)

object GamePreferences {

  protected var store: Option[SharedPreferences] = None
  protected var cache: Option[Preferences] = None

  def init(c: Context) = { store = Some(PreferenceManager.getDefaultSharedPreferences(c)) }

  // return cached preferences
  def get = {
    cache match {
      case Some(p) => p
      case None =>
        cache = Some(Preferences(getSound, getVibrate))
        cache.get
    }
  }

  def createKey(s: String) = s"pref_$s"

  def getSound = {
    store match {
      case Some(s) => s.getBoolean(createKey("sound"), true)
      case None => true
    }
  }

  def setSound(snd: Int) = {
    store map { s =>
      val editor = s.edit
      editor.putBoolean(createKey("sound"), snd == 1)
      editor.commit
    }

    // update local cache
    cache = cache map { p => p.copy(sound = snd == 1) }
  }

  def getVibrate = {
    store match {
      case Some(s) => s.getInt(createKey("vibrate"), 1)
      case None => 1
    }
  }

  // map vibrate setting to vibrate length value
  def getVibrateLength = cache map {
    p =>
      p.vibrate match {
        case 0 => 0
        case 1 => 10
        case 2 => 20
      }
  }

  def setVibrate(vib: Int) = {
    store map { s =>
      val editor = s.edit
      editor.putInt(createKey("vibrate"), vib)
      editor.commit
    }

    cache = cache map { p => p.copy(vibrate = vib) }
  }

}