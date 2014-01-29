package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.util.LocalHighScore
import android.content.Context
import com.github.adsgray.gdxtry1.engine.util.LocalHighScore.ScoreRecord


object HighScores {
  
  protected var local:Option[LocalHighScore] = None;

  def apply(c:Context) = {
    local = Some(new LocalHighScore(c))
  }

  def submitScore(key:String, score:Int) = {
    // store locally
    local map { l => l.submitScore(key, score) }
    
    // and also optionally to Google Play
  }
  
  def submitScore(key:String, score:Float) = {
    // to store locally as an int must multiply by 100
    local map {
      l =>
        val asInt = (score * 100).round
        l.submitScore(key, asInt)
    }
    
    // and also optionally to Google Play
  }
  
  import scala.collection.JavaConverters._
  def getLocalScoreRecords(labels:List[String], keys: List[String]):Option[List[ScoreRecord]] =  {
    local map { l => l.getScoreRecords(labels.toArray, keys.toArray).asScala.toList }
  }

}