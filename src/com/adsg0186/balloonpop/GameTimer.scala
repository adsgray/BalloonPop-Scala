package com.adsg0186.balloonpop

import com.badlogic.gdx.graphics.Color
import com.github.adsgray.gdxtry1.engine.WorldIF
import com.github.adsgray.gdxtry1.engine.blob.BaseTextBlob
import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.decorator.BlobDecorator
import com.github.adsgray.gdxtry1.engine.position.BlobPosition
import com.github.adsgray.gdxtry1.engine.util.AccelFactory
import com.github.adsgray.gdxtry1.engine.util.GameFactory

class GameTimer(b: BlobIF, endGame: => Unit, timeLimit:Int) extends BlobDecorator(b) {
  // TODO put 25 somewhere global 
  val ticksPerSecond = 25
  val tickTimeLimit = timeLimit * ticksPerSecond

  def timerFlashMessage(msg: String) = {
    val r = getRenderer
    val rc = new r.TextConfig(Color.WHITE, 2.5f)
    val fm = new BaseTextBlob(new BlobPosition(getPosition), GameFactory.zeroVelocity, AccelFactory.zeroAccel, r, rc)
    fm.setLifeTime(20) // just under 1 second
    fm.setText(msg)
    fastGrower(fm, 1200)
  }

  val tickMessages = Map[Int, String](
    tickTimeLimit - 5 * ticksPerSecond -> "5",
    tickTimeLimit - 4 * ticksPerSecond -> "4",
    tickTimeLimit - 3 * ticksPerSecond -> "3",
    tickTimeLimit - 2 * ticksPerSecond -> "2",
    tickTimeLimit - 1 * ticksPerSecond -> "1")

  override def tick = {
    ticks += 1
    var ret = component.tick

    if (ticks >= tickTimeLimit) {
      endGame
      // remove ourselves from the world
      GameTimer.destroy
      ret = false
    }

    import GameSound.SoundId._
    tickMessages.get(ticks) match {
      case Some(str) => 
        getWorld.addBlobToWorld(timerFlashMessage(str))
        GameSound.playSound(tickSound)
      case _ => Unit
    }

    ret
  }

}

object GameTimer {
  var instance: Option[GameTimer] = None

  // default is 3 seconds
  def apply(w: WorldIF, endGame: => Unit, timeLimit:Int = 60) = instance match {
    case None =>
      instance = Some(new GameTimer(nullBlob.create, endGame, timeLimit))
      instance map { b =>
        b.setPosition(new BlobPosition(50, GameFactory.BOUNDS_Y - 50))
        b setWorld w
        b setImmortal true
        w addBlobToWorld b
      }
      instance.get
    case Some(bc) => bc
  }

  def destroy = { instance = None }
  def get = instance

}