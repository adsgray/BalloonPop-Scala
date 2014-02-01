package com.adsg0186.balloonpop

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

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

    Tap.collision(source, secondary) // to track how many balloons this tap has popped

    // TODO: move this to TapBlob.pop ?
    secondary match {
      case balloon: Balloon =>
        GameState.incScore(balloon.points)
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
    Tap.remove(source)
    source
  }
}

case class TapBlob(b: BlobIF) extends BlobDecorator(b) {
  var popped = 0
  var points = 0
  val comboBonusPoints = 50

  def pop(b: BlobIF): Unit = b match {
    case balloon: Balloon =>
      popped += 1
      points += balloon.points
    case _ =>
      Log.e("tap", "tried to pop a non-balloon??")
  }

  def done: Unit = {
    Log.d("trace", s"tap got ${popped} balloons for ${points} points")

    // combo bonus points:
    popped match {
      case num if (num > 1) =>
        var comboMsg = s"${popped} COMBO!"
        var bonus = (num - 1) * comboBonusPoints

        if (num >= 5) {
          GameSound.yahoo
          // 5 combos are hard so they DOUBLE your bonus?!?!
          bonus *= 2
          comboMsg += " WOW!"
        } else {
          GameSound.goodJob
        }

        // 2-combo gets you 100 points, 3-combo 200, etc...
        Log.d("trace", s"combo bonus: ${bonus}")

        b.getWorld.addBlobToWorld(flashMessageAtBlob(b, comboMsg))
        GameState.incScore(bonus)
        GameState.incPopped(popped)
      // also play a sound and display a message
      case _ => Unit // no bonus otherwise
    }
  }
}

// If there were multiple Taps we'd have to map from baseBlob to TapBlob
// because the World collision trigger passes baseBlob as source.
object Tap {

  val extentRadius = 30
  val lifeTime = 12 // 12 = half a second, 8 = about 1/3 of a second

  import scala.collection.mutable.{HashMap, SynchronizedMap}
  var tapMap = new HashMap[BlobIF, TapBlob]() with SynchronizedMap[BlobIF, TapBlob]

  // called by GDX input thread
  def apply(pos: BlobPosition, w: WorldIF, r: Renderer): Unit = {
    val tap = createNewTapBlob(pos, w, r)
    tapMap += (tap.baseBlob -> tap)
    Log.d("tap", s"added Tap to map, size now ${tapMap.size}")
  }

  def remove(tapBaseBlob: BlobIF) = {
    val base = tapBaseBlob.baseBlob
    tapMap get base map { t => t.done }
    tapMap -= base
    Log.d("tap", s"removed Tap from list, size now ${tapMap.size}")
  }

  // called by WorldTicker thread
  def collision(tapBaseBlob: BlobIF, b: BlobIF) = tapMap get tapBaseBlob.baseBlob map { t => t.pop(b) }

  def createNewTapBlob(pos: BlobPosition, w: WorldIF, r: Renderer): TapBlob = {
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
    GameState.incPins(1)
    tap
  }

}
