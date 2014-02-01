package com.adsg0186.balloonpop

import com.google.android.gms.games.GamesClient
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

object AchievementTracker {
  // use sharedpreferences to store
  // 1. number of games completed
  // 2. high score points
  // 3. high score points/pin
  // 4. other achievements as necessary

  var store:Option[SharedPreferences] = None
  var c:Context = null // ugh
  var dirty:Boolean = true
  
  def init(c:Context) = {
    Log.d("scores", "AchievementTracker initialized")
    store = Some(PreferenceManager.getDefaultSharedPreferences(c))
    this.c = c
  }
  
  def key(s:String) = s"achievement_$s"
  def sentKey(s:String) = s"achievement_sent_$s"
  
  def incGamesPlayed = store map { s =>
    Log.d("scores", "inc games played")
    val k = key("gamesplayed")
    val played = s.getInt(k, 0)
    s.edit.putInt(k, played + 1).commit
  }
  
  def saveScore(score:Int) = store map { s =>
    Log.d("scores", s"save score $score")
    val k = key("highscore")
    val highscore = s.getInt(k, 0)
    if (score > highscore) {
      s.edit.putInt(k, score).commit
    }
  }
  
  def saveScorePerPin(spp:Float) = store map { s =>
    Log.d("scores", f"save scoreperpin $spp%.2f")
    val k = key("scoreperpin")
    val prev = s.getFloat(k, 0.0f)
    if (spp > prev) {
      s.edit.putFloat(k, spp).commit
    }
  }
  
  def savePopped(popped:Int) = store map { s =>
    Log.d("scores", s"save popped $popped")
    val k = key("popped")
    val prev = s.getInt(k, 0)
    s.edit.putInt(k, prev + popped).commit
  }
  
  // set boolean achievements
  // must match achievements set up on the Play store
  def processScore(score:Int) = store map { s =>
    
    Log.d("scores", s"process score $score")
    if (score <= 10) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_wallflower), true).commit
    }
    
    // see processScorePerPin for why these are in reverse order with if-elses
    if (score >= 50000) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_score50000), true).commit
    }
    else if (score >= 30000) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_score30000), true).commit
    }
    else if (score >= 20000) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_score20000), true).commit
    }
    else if (score >= 10000) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_score10000), true).commit
    }
  }
  
  // set boolean achievements
  // must match achievements set up on the Play store
  def processScorePerPin(spp:Float) = store map { s =>
    Log.d("scores", f"process score per pin $spp%.2f")
    if (spp < 10.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_rapidpin), true).commit
    }
 
    // give these out in reverse order.
    // eg. if you get spp >= 100, you only get the top achievement.
    // even though technically you have achieved all of the lower ones.
    // You'll get the lower ones when you achieve them specifically...
    // The "else ifs" achieve this.
    if (spp >= 150.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_ultrapin), true).commit
    }
    else if (spp >= 100.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_propin), true).commit
    }
    else if (spp >= 75.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_greatpin), true).commit
    }
    else if (spp >= 50.0f) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_goodpin), true).commit
    }

  }
  
  def processPopped = store map { s =>
    val popped = s.getInt(key("popped"), 0)

    if (popped > 100) s.edit.putBoolean(c.getResources.getString(R.string.achievement_popped_100_balloons), true).commit
    if (popped > 500) s.edit.putBoolean(c.getResources.getString(R.string.achievement_popped_500_balloons), true).commit
    if (popped > 1000) s.edit.putBoolean(c.getResources.getString(R.string.achievement_popped_1000_balloons), true).commit
    if (popped > 2500) s.edit.putBoolean(c.getResources.getString(R.string.achievement_popped_2500_balloons), true).commit
    if (popped > 5000) s.edit.putBoolean(c.getResources.getString(R.string.achievement_popped_5000_balloons), true).commit
    if (popped > 20000) s.edit.putBoolean(c.getResources.getString(R.string.achievement_popped_20000_balloons), true).commit
    if (popped > 50000) s.edit.putBoolean(c.getResources.getString(R.string.achievement_popped_50000_balloons), true).commit
    if (popped > 100000) s.edit.putBoolean(c.getResources.getString(R.string.achievement_popped_100000_balloons), true).commit
  }
  
  def processGamesPlayed = store map { s =>
    Log.d("scores", "process games played")
    val k = key("gamesplayed")
    val played = s.getInt(k, 0)
    
    Log.d("scores", s"games played=$played")
    
    if (played >= 10) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_played10), true).commit
    }

    if (played >= 100) {
      s.edit.putBoolean(c.getResources().getString(R.string.achievement_played100), true).commit
    }
  }
  
  def gameCompleted(score:Int, scorePerPin:Float, popped:Int) {
    Log.d("scores", "game completed")
    incGamesPlayed
    saveScore(score)
    saveScorePerPin(scorePerPin)
    savePopped(popped)

    processGamesPlayed
    processScore(score)
    processScorePerPin(scorePerPin)
    processPopped
    dirty = true;
  }
  
  def saveToGoogle(gc:GamesClient) = store map { s => 
    if (dirty) {
      Log.d("scores", "scores dirty, submitting")

      // submit scores
      val score = s.getInt(key("highscore"), 0)
      Log.d("scores", s"submitting score=$score")
      gc.submitScore(c.getResources.getString(R.string.leaderboard_bestround), score)
      // once submitted, zero out the local one
      s.edit.putInt(key("highscore"), 0).commit

      val perpinAsInt = (s.getFloat(key("scoreperpin"), 0.0f) * 100).round
      Log.d("scores", s"submitting pointsperpin=$perpinAsInt")
      gc.submitScore(c.getResources.getString(R.string.leaderboard_pointsperpin), perpinAsInt)
      // once submitted, zero out the local one
      s.edit.putFloat(key("scoreperpin"), 0).commit

      val popped = s.getInt(key("popped"), 0)
      Log.d("scores", s"submitting balloonspopped=$popped")
      gc.submitScore(c.getResources.getString(R.string.leaderboard_balloonspopped), popped)
      // popped keeps accumulating FOREVER
      
      // unlock achievements
      List(
          c.getResources.getString(R.string.achievement_wallflower),
          c.getResources.getString(R.string.achievement_rapidpin),
          c.getResources.getString(R.string.achievement_goodpin),
          c.getResources.getString(R.string.achievement_greatpin),
          c.getResources.getString(R.string.achievement_propin),
          c.getResources.getString(R.string.achievement_ultrapin),
          c.getResources.getString(R.string.achievement_played10),
          c.getResources.getString(R.string.achievement_played100),
          c.getResources.getString(R.string.achievement_score10000),
          c.getResources.getString(R.string.achievement_score20000),
          c.getResources.getString(R.string.achievement_score30000),
          c.getResources.getString(R.string.achievement_score50000),
          c.getResources.getString(R.string.achievement_popped_100_balloons),
          c.getResources.getString(R.string.achievement_popped_500_balloons),
          c.getResources.getString(R.string.achievement_popped_1000_balloons),
          c.getResources.getString(R.string.achievement_popped_2500_balloons),
          c.getResources.getString(R.string.achievement_popped_5000_balloons),
          c.getResources.getString(R.string.achievement_popped_20000_balloons),
          c.getResources.getString(R.string.achievement_popped_50000_balloons),
          c.getResources.getString(R.string.achievement_popped_100000_balloons)
          
          // TODO: popped achievements
          ) map { achString =>
            // only send achievements the first time they are earned
            if (s.getBoolean(achString, false) && !s.getBoolean(sentKey(achString), false)) {
              Log.d("scores", s"unlocking achievement $achString")
              gc.unlockAchievement(achString)
              s.edit.putBoolean(sentKey(achString), true).commit
            }
      }

      dirty = false;
    }
  }
}