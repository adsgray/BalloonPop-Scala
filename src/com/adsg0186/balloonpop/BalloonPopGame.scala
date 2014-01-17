package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.util.Game
import com.github.adsgray.gdxtry1.engine.util.GameCommand
import com.github.adsgray.gdxtry1.engine.WorldIF
import com.github.adsgray.gdxtry1.engine.output.Renderer
import com.github.adsgray.gdxtry1.engine.input.SimpleDirectionGestureDetector.DirectionListener
import com.github.adsgray.gdxtry1.engine.input.DefaultDirectionListener

case class myDirectionListener(world: WorldIF, renderer: Renderer) extends DefaultDirectionListener {
  override def onTap(x: Float, y: Float, count: Int) = {
    // add missile to world at this position with a fixed short lifetime
    // the missile keeps track of how many targets it has killed. It
    // is responsible for playing pop sounds.
    //
    // and at death tells a ScoreManager (or something) how many
    // ScoreManager determines the score/combo bonuses and
    // decided which sounds to play for score/combo
  }
}

class BalloonPopGame(world: WorldIF, renderer: Renderer) extends Game {

  var dl: Option[myDirectionListener] = None
  Balloons.setRenderer(renderer)

  protected def initDirectionListener: DirectionListener = {
    val dl = myDirectionListener(world, renderer)
    this.dl = Some(dl)
    dl
  }

  def getDirectionListener: DirectionListener = dl getOrElse initDirectionListener

  def init(): Unit = {
    initDirectionListener
  }

  def start(): Unit = {
    /*
     * create a nullBlob with a tickdeathtimer loop that at random
     * intervals creates some balloons that come into the world
     * have it look at world.getNumTargets
     */

  }

  def stop(): Unit = ???

  def save(): Unit = ???

  def getDifficultySetter(): GameCommand = ???
  def getFinalScore(): Int = ???
  def getSoundToggle(): GameCommand = ???
  def getVibrateToggle(): GameCommand = ???

}