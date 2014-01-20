package com.adsg0186.balloonpop

import java.util.Random
import com.github.adsgray.gdxtry1.engine.BlobCluster
import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.BlobPath
import com.github.adsgray.gdxtry1.engine.blob.decorator.BlobDecorator
import com.github.adsgray.gdxtry1.engine.extent.CircleExtent
import com.github.adsgray.gdxtry1.engine.output.Renderer
import com.github.adsgray.gdxtry1.engine.position.BlobPosition
import com.github.adsgray.gdxtry1.engine.util.AccelFactory
import com.github.adsgray.gdxtry1.engine.util.BlobFactory
import com.github.adsgray.gdxtry1.engine.util.GameFactory
import com.github.adsgray.gdxtry1.engine.util.PathFactory
import com.github.adsgray.gdxtry1.engine.util.PositionFactory
import com.github.adsgray.gdxtry1.engine.velocity.BlobVelocity
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobTransform
import android.util.Log
import com.github.adsgray.gdxtry1.engine.position.PositionIF
import com.github.adsgray.gdxtry1.engine.accel.LinearAccel

// Balloons are worth points when you pop them
trait Balloon extends BlobDecorator {
  def points: Int

  def leaveCluster = if (getCluster != null) getCluster.leaveCluster(baseBlob)
  def leaveWorld = getWorld.removeBlobFromWorld(this)
  def leave = {
    leaveCluster
    leaveWorld
  }

  val postExplosionTrigger = blobTrigger { (b, _) =>
    b.clearTickDeathTriggers
    b.setLifeTime(0)
    b
  }

  def popSound = GameSound.pop

  def explosion = {
    leave
    val exploding = fastGrower(getComponent)
    exploding.clearTickDeathTriggers
    exploding.registerTickDeathTrigger(postExplosionTrigger)
    exploding.setLifeTime(5)
    getWorld.addBlobToWorld(exploding)
    exploding
  }

  // make this a def so it is easily override-able
  def vibrateLength = 5

  def reactToPop: Unit = {
    explosion
    popSound
    Vibrate.vibrate(vibrateLength)
  }
}

// when you pop this type of balloon it breaks into a bunch
// of smaller balloons in similar locations. Just override reactToPop
case class BlobOffset(val x: Int, val y: Int);
trait asteroidBalloonTrait extends Balloon {

  val offset = 50
  val childOffsets = List(
    // top row
    BlobOffset(offset, offset),
    BlobOffset(0, offset),
    BlobOffset(-offset, offset),

    // middle
    BlobOffset(offset, 0),
    BlobOffset(-offset, 0),

    // bottom row
    BlobOffset(-offset, -offset),
    BlobOffset(0, -offset),
    BlobOffset(offset, -offset))

  // normalized velocity along the vector
  val velocityMultiplier = 20
  def velocityFromOffset(bo: BlobOffset) = bo match {
    case BlobOffset(x, y) => new BlobVelocity(x / offset * velocityMultiplier, y / offset * velocityMultiplier)
  }

  val accelMultiplier = -1
  def accelFromOffset(bo: BlobOffset) = bo match {
    case BlobOffset(x, y) => new LinearAccel(x / offset * accelMultiplier, y / offset * accelMultiplier)
  }

  override def popSound = GameSound.asteroidBam

  override def vibrateLength = 10

  override def reactToPop = {
    // explode ourselves
    super.reactToPop

    // then spew some small balloons out
    childOffsets map {
      case bo @ BlobOffset(x, y) =>
        val pos = new BlobPosition(getPosition.getX, getPosition.getY)
        pos.setX(pos.getX + x)
        pos.setY(pos.getY + y)

        val balloon = Balloons.smallBalloon
        balloon.setPosition(pos)
        balloon.setVelocity(velocityFromOffset(bo))
        // for some reason had to set this accel here otherwise the
        // whole game would get messed up as if the accel generated
        // by Balloons.smallBalloon lived on forever across invocations
        // of game.{init,start}?
        balloon.setAccel(accelFromOffset(bo))
        balloon.setLifeTime(125) // 5 seconds

        getWorld.addTargetToWorld(balloon)
    }

    // replace this balloon with a bunch of smaller balloons
    // and play a different sound
  }
}

case class SmallBalloon(b: BlobIF) extends BlobDecorator(b) with Balloon {
  override def points = 20
}
case class LargeBalloon(b: BlobIF) extends BlobDecorator(b) with Balloon {
  override def points = 10
}
case class AsteroidBalloon(b: BlobIF) extends BlobDecorator(b) with asteroidBalloonTrait {
  override def points = 15
}

object Balloons {
  val rnd = new Random
  val maxXPos = 150
  val maxYPos = 150
  val minXPos = 50
  val minYPos = 50

  var renderer: Option[Renderer] = null
  def setRenderer(r: Renderer) = renderer = Some(r)
  def balloonColor = GameFactory.randomColor

  // These are always relative to a cluster:
  def balloonPosition = {
    val xpos = rnd.nextInt(2 * maxXPos) - maxXPos + minXPos
    val ypos = rnd.nextInt(2 * maxYPos) - maxYPos + minYPos
    new BlobPosition(xpos, ypos)
  }

  def balloonPath = BalloonPath.chooseClosedPath

  def balloonBlob: BlobIF = {
    val r = renderer.get
    val size = smallBalloonSize
    val rc = new r.CircleConfig(balloonColor, size)
    val blob = BlobFactory.circleBlob(balloonPosition, balloonPath, rc, r)
    blob.setExtent(new CircleExtent(size))
    blob
  }

  def smallBalloonSize = 20 + rnd.nextInt(20) - 10
  def smallBalloon: Balloon = SmallBalloon(balloonBlob)

  def largeBalloonSize = 60 + rnd.nextInt(30) - 15
  def largeBalloon: Balloon = {
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
  def asteroidBalloon: Balloon = largeBalloon match {
    case LargeBalloon(b) =>
      import BlobFactory._
      AsteroidBalloon(rainbowColorCycler(b, 3))
  }

  // TODO: allow a small chance of asteroidBalloon
  // might need a map...

  val balloonChoices = List[() => Balloon](
    // hack to make it more likely to get
    // largeBaloon and smallBallon than asteroidBalloon
    largeBalloon _,
    largeBalloon _,
    largeBalloon _,

    smallBalloon _,
    smallBalloon _,

    asteroidBalloon _)

  def randomBalloon: Balloon = balloonChoices(rnd.nextInt(balloonChoices.size))()

}

object BalloonPath {

  def speed = 10 + Balloons.rnd.nextInt(20) - 10
  def interval = 5 + Balloons.rnd.nextInt(5)

  // paths that loop
  val closedPaths = List(
    PathFactory.backAndForth(speed, interval),
    PathFactory.backAndForthLeft(speed, interval),
    PathFactory.squarePath(speed, interval),
    PathFactory.squarePathClockwise(speed, interval),
    PathFactory.upperTriangle(speed, interval),
    PathFactory.upAndDown(speed, interval))

  // choose one of the above paths randomly
  def chooseClosedPath = closedPaths(Balloons.rnd.nextInt(closedPaths.size))

}

// starting position and path for a BalloonCluster
// pair them because they are not independent
// eg start at top right and go down and to the left...
case class BalloonClusterOrigin(pos: PositionIF, path: BlobPath)
object BalloonClusterOrigin {
  // TODO choose cluster position and path randomly
  def randomPosition = {
    new BlobPosition(10, 10)
  }

  def pathFromVel(xvel: Int, yvel: Int) = {
    val v = new BlobVelocity(xvel, yvel)
    val a = AccelFactory.zeroAccel
    new BlobPath(v, a)
  }

  def xvel = 5 + Balloons.rnd.nextInt(3)
  def yvel = 5 + Balloons.rnd.nextInt(3)

  // make these into functions so that they are executed anew
  // each time we choose one
  // TODO: generate these programatically instead of hard-coding them?
  val origins = List(
    // starting from bottom:
    () => BalloonClusterOrigin(new BlobPosition(10, 10), pathFromVel(xvel, yvel)),
    () => BalloonClusterOrigin(new BlobPosition(GameFactory.BOUNDS_X - 10, 10), pathFromVel(-xvel, yvel)),
    () => BalloonClusterOrigin(new BlobPosition(GameFactory.BOUNDS_X / 2, 10), pathFromVel(0, yvel)),

    // starting from top:
    () => BalloonClusterOrigin(new BlobPosition(10, GameFactory.BOUNDS_Y - 10), pathFromVel(xvel, -yvel)),
    () => BalloonClusterOrigin(new BlobPosition(GameFactory.BOUNDS_X - 10, GameFactory.BOUNDS_Y - 10), pathFromVel(-xvel, -yvel)),
    () => BalloonClusterOrigin(new BlobPosition(GameFactory.BOUNDS_X / 2, GameFactory.BOUNDS_Y - 10), pathFromVel(0, -yvel)),

    // starting from sides:
    () => BalloonClusterOrigin(new BlobPosition(10, GameFactory.BOUNDS_Y / 2), pathFromVel(xvel, 0)),
    () => BalloonClusterOrigin(new BlobPosition(GameFactory.BOUNDS_X - 10, GameFactory.BOUNDS_Y / 2), pathFromVel(-xvel, 0)))

  def random = origins(Balloons.rnd.nextInt(origins.size))()
}

object BalloonCluster {

  def balloonCluster(num: Int, t: BlobTransform): BlobIF = {
    val r = Balloons.renderer.get
    val start = BalloonClusterOrigin.random
    val cluster = new BlobCluster(start.pos, start.path, r)
    // add num blobs to cluster
    (1 to num) map { i =>
      val b = Balloons.randomBalloon
      b.setLifeTime(500)
      // ugh, mutates b:
      PathFactory.composePositions(b, cluster)
      // bug in BlobCluster -- it doesn't perform the transformation
      // we can do it here...
      cluster.absorbBlob(t.transform(b))
      b.setCluster(cluster)
    }
    cluster.setDebugStr(s"cluster ${num}")
    cluster
  }

  val clusterSizes = List(1, 2, 3, 4, 5)
  def randomCluster(t: BlobTransform): BlobIF = {
    balloonCluster(clusterSizes(Balloons.rnd.nextInt(clusterSizes.size)), t)
  }
}