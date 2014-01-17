package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.BaseBlob
import com.github.adsgray.gdxtry1.engine.blob.decorator.BlobDecorator
import com.github.adsgray.gdxtry1.engine.position.PositionIF
import com.github.adsgray.gdxtry1.engine.velocity.VelocityIF
import com.github.adsgray.gdxtry1.engine.accel.AccelIF
import com.github.adsgray.gdxtry1.engine.output.Renderer
import com.github.adsgray.gdxtry1.engine.output.Renderer.RenderConfigIF
import com.github.adsgray.gdxtry1.engine.WorldIF
import com.github.adsgray.gdxtry1.engine.util.GameFactory
import com.github.adsgray.gdxtry1.engine.util.BlobFactory
import com.github.adsgray.gdxtry1.engine.util.PositionFactory
import com.github.adsgray.gdxtry1.engine.util.PathFactory
import java.util.Random

// Balloons are worth points when you pop them
trait Balloon {
  def getPoints
  def reactToPop = {
    // replace with explosion animation
    // and play sound
    ???
  }
}

case class SmallBalloon(b:BlobIF) extends BlobDecorator(b) with Balloon {
  override def getPoints = 20
}
case class LargeBalloon(b:BlobIF) extends BlobDecorator(b) with Balloon {
  override def getPoints = 10
}

object Balloons {
  val rnd = new Random

  var renderer:Option[Renderer] = null
  def setRenderer(r:Renderer) = renderer = Some(r)

  def balloonBlob:BlobIF = {
    val r = renderer.get
    val rc = new r.CircleConfig(GameFactory.randomColor, smallBalloonSize)
    val pos = PositionFactory.origin
    val path = PathFactory.stationary
    BlobFactory.circleBlob(pos, path, rc, r)
  }

  def smallBalloonSize = 20
  def smallBalloon:Balloon = SmallBalloon(balloonBlob)

  def largeBalloonSize = 60
  def largeBalloon:Balloon = {
    val r = renderer.get
    val b = balloonBlob
    val rc = new r.CircleConfig(GameFactory.randomColor, largeBalloonSize)
    b.setRenderConfig(rc)
    LargeBalloon(b)
  }

  def coinFlip = rnd.nextInt(100) < 50
  def randomBalloon:Balloon = if (coinFlip) smallBalloon else largeBalloon
}