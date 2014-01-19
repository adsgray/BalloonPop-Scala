package com.adsg0186.balloonpop

import android.util.Log

trait GameState {
  var score = 0
  var pins = 0
  var ticksLeft = 1000

  def init = {
    score = 0
    pins = 0
    ticksLeft = 1000
  }

  def incScore(delta:Int) = score += delta
  def incPins(delta:Int) = pins += delta

}

object GameState extends GameState {
  override def incScore(delta:Int) = {
    super.incScore(delta)
    ScoreDisplay.refreshText
    Log.d("trace", s"gamestate incscore by ${delta} score is ${score}")
  }
  
  override def incPins(delta:Int) = {
    super.incPins(delta)
    ScoreDisplay.refreshText
    Log.d("trace", s"gamestate incpins by ${delta} pins is ${pins}")
  }
  
  def destroy = init
}