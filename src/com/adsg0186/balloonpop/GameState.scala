package com.adsg0186.balloonpop

import android.util.Log

trait GameState {
  var score = 0
  var ticksLeft = 1000

  def init = {
    score = 0
    ticksLeft = 1000
  }

  def incScore(delta:Int) = score += delta

}

object GameState extends GameState {
  override def incScore(delta:Int) = {
    super.incScore(delta)
    ScoreDisplay.setScore(score)
    Log.d("trace", s"gamestate incscore by ${delta} score is ${score}")
  }
  
  def destroy = init
}