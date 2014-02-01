package com.adsg0186.balloonpop

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object AchievementTracker {
  // use sharedpreferences to store
  // 1. number of games completed
  // 2. high score points
  // 3. high score points/pin
  // 4. other achievements as necessary

  var store:Option[SharedPreferences] = None
  var c:Context = null // ugh
  
  def init(c:Context) = {
    store = Some(PreferenceManager.getDefaultSharedPreferences(c))
    this.c = c
  }
  
  def key(s:String) = s"achievement_$s"
  
  def incGamesPlayed = store map { s =>
    val k = key("gamesplayed")
    val played = s.getInt(k, 0)
    s.edit.putInt(k, played + 1).commit
  }
  
  def saveScore(score:Int) = store map { s =>
    val k = key("highscore")
    val highscore = s.getInt(k, 0)
    if (score > highscore) {
      s.edit.putInt(k, score).commit
    }
  }
  
  def saveScorePerPin(spp:Float) = store map { s =>
    val k = key("scoreperpin")
    val prev = s.getFloat(k, 0.0f)
    if (spp > prev) {
      s.edit.putFloat(k, spp).commit
    }
  }
  
  // set boolean achievements
  // must match achievements set up on the Play store
  def processScore(score:Int) = store map { s =>
    
    if (score <= 10) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_wallflower), true)
    }
    
    // see processScorePerPin for why these are in reverse order with if-elses
    if (score >= 50000) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_score50000), true)
    }
    else if (score >= 30000) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_score30000), true)
    }
    else if (score >= 20000) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_score20000), true)
    }
    else if (score >= 10000) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_score10000), true)
    }
  }
  
  // set boolean achievements
  // must match achievements set up on the Play store
  def processScorePerPin(spp:Float) = store map { s =>
    if (spp < 10.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_rapidpin), true)
    }
 
    // give these out in reverse order.
    // eg. if you get spp >= 100, you only get the top achievement.
    // even though technically you have achieved all of the lower ones.
    // You'll get the lower ones when you achieve them specifically...
    // The "else ifs" achieve this.
    if (spp >= 150.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_ultrapin), true)
    }
    else if (spp >= 100.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_propin), true)
    }
    else if (spp >= 75.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_greatpin), true)
    }
    else if (spp >= 50.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_goodpin), true)
    }

  }
  
  def processGamesPlayed = store map { s =>
    val k = key("gamesplayed")
    val played = s.getInt(k, 0)
    
    if (played >= 10) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_played10), true)
    }

    if (played >= 100) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_played100), true)
    }
  }
  
  def gameCompleted(score:Int, scorePerPin:Float) {
    incGamesPlayed
    saveScore(score)
    saveScorePerPin(scorePerPin)

    processGamesPlayed
    processScore(score)
    processScorePerPin(scorePerPin)
  }
}