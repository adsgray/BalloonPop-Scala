package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.WorldIF
import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobTransform
import com.github.adsgray.gdxtry1.engine.input.DefaultDirectionListener
import com.github.adsgray.gdxtry1.engine.input.SimpleDirectionGestureDetector.DirectionListener
import com.github.adsgray.gdxtry1.engine.output.Renderer
import com.github.adsgray.gdxtry1.engine.position.BlobPosition
import com.github.adsgray.gdxtry1.engine.util.Game
import com.github.adsgray.gdxtry1.engine.util.GameCommand

import android.util.Log

case class myDirectionListener(world: WorldIF, renderer: Renderer) extends DefaultDirectionListener {
  var enabled = true
  override def onTap(x: Float, y: Float, count: Int) = {
    // add missile to world at this position with a fixed short lifetime
    // the missile keeps track of how many targets it has killed. It
    // is responsible for playing pop sounds.
    //
    // and at death tells a ScoreManager (or something) how many
    // ScoreManager determines the score/combo bonuses and
    // decided which sounds to play for score/combo

    //super.onTap(x, y, count)
    if (enabled) Tap(new BlobPosition(x, y), world, renderer)
  }

  def disable = { enabled = false }
  def enable = { enabled = true }
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
    Log.d("trace", "BalloonPopGame init")
    initDirectionListener
    // throw away return value as it's creating a singleton
    world.addBlobToWorld(ScoreDisplay(renderer))
    ScoreDisplay.refreshText
    BalloonCreator(world) // adds itself to the world
    GameTimer(world, {
      // end game code
      BalloonCreator.destroy

      // disable input handler
      dl map { d => d.disable }
      // show final score with message "press back"
      FinalScoreDisplay(world)
    })
  }

  def start(): Unit = {
    Log.d("trace", "BalloonPopGame start")

  }

  def stop(): Unit = {
    Log.d("trace", "BallonPopGame stop")
    ScoreDisplay.destroy
    GameState.destroy
    FinalScoreDisplay.destroy

    // kill the balloonCreator
    BalloonCreator.destroy
  }

  def save(): Unit = {
    Log.d("trace", "BalloonPopGame save")
    // TODO
  }

  def getDifficultySetter(): GameCommand = ???
  def getFinalScore(): Int = ???
  def getSoundToggle(): GameCommand = ???
  def getVibrateToggle(): GameCommand = ???

}