package com.adsg0186.balloonpop

import com.badlogic.gdx.graphics.Color
import com.github.adsgray.gdxtry1.engine.WorldIF
import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobTrigger
import com.github.adsgray.gdxtry1.engine.blob.decorator.BlobDecorator
import com.github.adsgray.gdxtry1.engine.extent.CircleExtent
import com.github.adsgray.gdxtry1.engine.output.Renderer
import com.github.adsgray.gdxtry1.engine.position.BlobPosition
import com.github.adsgray.gdxtry1.engine.util.BlobFactory
import com.github.adsgray.gdxtry1.engine.util.PathFactory

import android.util.Log

object tapCollisionTrigger extends BlobTrigger {
  override def trigger(source: BlobIF, secondary: BlobIF): BlobIF = {
    Log.d("trace", "Tap collided with a balloon!")

    Tap.collision(secondary) // to track how many balloons this tap has popped

    secondary match {
      case balloon: Balloon => 
        GameState.incScore(balloon.points)
        ScoreDisplay.setScore(GameState.score)
        balloon.reactToPop
    }

    source
  }
}

// triggered when TapBlob is finished
object onTapComplete extends BlobTrigger {
  override def trigger(source: BlobIF, secondary: BlobIF): BlobIF = {
    Log.d("trace", "Tap done")
    // play a sound?
    Tap.remove
    source
  }
}

case class TapBlob(b: BlobIF) extends BlobDecorator(b) {
  var popped = 0
  var points = 0
  val comboBonusPoints = 100
  
  def done:Unit = {
    Log.d("trace", s"tap got ${popped} balloons for ${points} points")
    
    // combo bonus points:
    popped match {
      case 1 => // no bonuses
      case num @ _ => 
        // 2-combo gets you 100 points, 3-combo 200, etc...
        GameState.incScore((num - 1) * comboBonusPoints)
        // also play a sound and display a message
    }
  }
}

// If there were multiple Taps we'd have to map from baseBlob to TapBlob
// because the World collision trigger passes baseBlob as source.
object Tap {

  val extentRadius = 50
  val lifeTime = 12 // half a second?
  var currentTap: Option[TapBlob] = None

  def apply(pos: BlobPosition, w: WorldIF, r: Renderer): BlobIF = {
    currentTap match {
      case Some(t) =>
        Log.d("trace", "currentTap still exists")
        currentTap.get
      case None => createNewTapBlob(pos, w, r)
    }
  }

  def remove = {
    currentTap map { t => t.done }
    currentTap = None
  }

  def collision(b:BlobIF) = {
    currentTap map { t => 
      t.popped += 1 
      b match {
        case balloon : Balloon => t.points += balloon.points
      }
    }
  }

  def createNewTapBlob(pos: BlobPosition, w: WorldIF, r: Renderer): BlobIF = {
    //public static BlobIF circleBlob(PositionIF p, BlobPath path, CircleConfig rc, Renderer r) {
    val rc = new r.CircleConfig(Color.WHITE, 1)
    val b = BlobFactory.circleBlob(pos, PathFactory.stationary, rc, r)
    b.setExtent(new CircleExtent(extentRadius))
    b.setLifeTime(lifeTime)
    b.setWorld(w)
    b.registerCollisionTrigger(tapCollisionTrigger)
    b.registerTickDeathTrigger(onTapComplete)
    val tap = TapBlob(b)
    w.addMissileToWorld(tap)
    currentTap = Some(tap)
    tap
  }

}