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
  override def trigger(source:BlobIF, secondary:BlobIF):BlobIF = {
    Log.d("trace", "Tap collided with a balloon!")
    //val tap = source.asInstanceOf[TapBlob]
    //tap.popped += 1
    source
  }
}

// triggered when TapBlob is finished
object onTapComplete extends BlobTrigger {
  override def trigger(source:BlobIF, secondary:BlobIF):BlobIF = {
    Log.d("trace", "Tap done")
    val tap = source.asInstanceOf[TapBlob]
    // tell some kind of game controller how many balloons this Tap popped
    // for the purpose of score/bonus calculation
    // play a sound?
    source
  }
}


case class TapBlob(b:BlobIF) extends BlobDecorator(b) {
  var popped = 0
}

object Tap {
  
  val extentRadius = 50
  val lifeTime = 20 // half a second?

  def apply(pos:BlobPosition, w: WorldIF, r: Renderer):BlobIF = {
    //public static BlobIF circleBlob(PositionIF p, BlobPath path, CircleConfig rc, Renderer r) {
    val rc = new r.CircleConfig(Color.WHITE, 1)
    val b = BlobFactory.circleBlob(pos, PathFactory.stationary, rc, r)
    b.setExtent(new CircleExtent(extentRadius))
    b.setWorld(w)
    b.registerCollisionTrigger(tapCollisionTrigger)
    val tap = TapBlob(b)
    w.addMissileToWorld(tap)
    tap
  }

}