package com.adsg0186.balloonpop

import android.util.Log

trait GameState {
  var score = 0
  var pins = 0
  var popped = 0;

  def init = {
    score = 0
    pins = 0
    popped = 0;
  }

  def incScore(delta:Int) = score += delta
  def incPins(delta:Int) = pins += delta
  def incPopped(delta:Int) = popped += delta
  def scorePerPin = if (pins > 0) score.toFloat / pins.toFloat else 0.0f
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
  
  // TODO: add balloons popped to score display?
  
  def destroy = init
}