package com.adsg0186.balloonpop

import java.util.Random

import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.decorator.BlobDecorator
import com.github.adsgray.gdxtry1.engine.extent.CircleExtent
import com.github.adsgray.gdxtry1.engine.output.Renderer
import com.github.adsgray.gdxtry1.engine.util.BlobFactory
import com.github.adsgray.gdxtry1.engine.util.GameFactory
import com.github.adsgray.gdxtry1.engine.util.PathFactory
import com.github.adsgray.gdxtry1.engine.util.PositionFactory

// Balloons are worth points when you pop them
trait Balloon {
  def points
  def reactToPop = {
    // replace with explosion animation
    // and play sound
    ???
  }
}

// when you pop this type of balloon it breaks into a bunch
// of smaller balloons in similar locations. Just override reactToPop
trait asteroidBalloonTrait extends Balloon {
  override def reactToPop = {
    // replace this balloon with a bunch of smaller balloons
    ???
  }
}

case class SmallBalloon(b:BlobIF) extends BlobDecorator(b) with Balloon {
  override def points = 20
}
case class LargeBalloon(b:BlobIF) extends BlobDecorator(b) with Balloon {
  override def points = 10
}
case class AsteroidBalloon(b:BlobIF) extends BlobDecorator(b) with asteroidBalloonTrait {
  override def points = 15
}


object Balloons {
  val rnd = new Random

  var renderer:Option[Renderer] = null
  def setRenderer(r:Renderer) = renderer = Some(r)
  def balloonColor = GameFactory.randomColor

  // TODO: make these random-ish:
  def balloonPosition = {
    PositionFactory.origin
  }

  def balloonPath = {
    PathFactory.stationary
  }

  def balloonBlob:BlobIF = {
    val r = renderer.get
    val size = smallBalloonSize
    val rc = new r.CircleConfig(balloonColor, size)
    val blob = BlobFactory.circleBlob(balloonPosition, balloonPath, rc, r)
    blob.setExtent(new CircleExtent(size))
    blob
  }

  def smallBalloonSize = 20
  def smallBalloon:Balloon = SmallBalloon(balloonBlob)

  def largeBalloonSize = 60
  def largeBalloon:Balloon = {
    val r = renderer.get
    val b = balloonBlob
    val size = largeBalloonSize
    val rc = new r.CircleConfig(balloonColor, size)
    b.setRenderConfig(rc)
    b.setExtent(new CircleExtent(size))
    LargeBalloon(b)
  }
  
  // get a largeBalloon and re-wrap it as an AsteroidBalloon
  // also make it rainbow-y
  def asteroidBalloon:Balloon = largeBalloon match {
    case LargeBalloon(b) => AsteroidBalloon(BlobFactory.rainbowColorCycler(b, 10))
  }

  // TODO: allow a small chance of asteroidBalloon
  // might need a map...
  def coinFlip = rnd.nextInt(100) < 50
  def randomBalloon:Balloon = if (coinFlip) smallBalloon else largeBalloon
}