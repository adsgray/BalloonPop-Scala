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

// Balloons are worth points when you pop them
trait Balloon extends BlobDecorator {
  def points:Int

  def leaveCluster = if (getCluster != null) getCluster.leaveCluster(baseBlob)
  def leaveWorld = getWorld.removeBlobFromWorld(this)

  def reactToPop:Unit = {
    Log.d("trace", "balloon pop, removing from world")
    leaveCluster
    leaveWorld
    // explosion animations and sounds decided by subclasses
  }
}

// when you pop this type of balloon it breaks into a bunch
// of smaller balloons in similar locations. Just override reactToPop
trait asteroidBalloonTrait extends Balloon {
  override def reactToPop = {
    super.reactToPop
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
  val maxXPos = 50
  val maxYPos = 50
  
  var renderer: Option[Renderer] = null
  def setRenderer(r: Renderer) = renderer = Some(r)
  def balloonColor = GameFactory.randomColor

  // These are always relative to a cluster:
  def balloonPosition = {
    val xpos = rnd.nextInt(2 * maxXPos) - maxXPos
    val ypos = rnd.nextInt(2 * maxYPos) - maxYPos
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

  def smallBalloonSize = 20
  def smallBalloon: Balloon = SmallBalloon(balloonBlob)

  def largeBalloonSize = 60
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
    case LargeBalloon(b) => AsteroidBalloon(BlobFactory.rainbowColorCycler(b, 3))
  }

  // TODO: allow a small chance of asteroidBalloon
  // might need a map...

  val balloonChoices = List[() => Balloon](
    smallBalloon _,
    largeBalloon _,
    asteroidBalloon _)

  def randomBalloon: Balloon = balloonChoices(rnd.nextInt(balloonChoices.size))()
  
}

object BalloonPath {

  def speed = 10
  def interval = 5

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
case class BalloonClusterOrigin(pos:PositionIF, path:BlobPath) 
object BalloonClusterOrigin {
  // TODO choose cluster position and path randomly
  def randomPosition = {
    new BlobPosition(10,10)
  }
   
  def pathFromVel(xvel:Int, yvel:Int) = {
    val v = new BlobVelocity(xvel, yvel)
    val a = AccelFactory.zeroAccel
    new BlobPath(v, a)
  }
  
  // make these into functions so that they are executed anew
  // each time we choose one
  val origins = List(
      // starting from bottom:
      () => BalloonClusterOrigin(new BlobPosition(10,10), pathFromVel(5,5)),
      () => BalloonClusterOrigin(new BlobPosition(GameFactory.BOUNDS_X - 10,10), pathFromVel(-5,5)),
      () => BalloonClusterOrigin(new BlobPosition(GameFactory.BOUNDS_X / 2,10), pathFromVel(0,5)),
      
      // starting from top:
      () => BalloonClusterOrigin(new BlobPosition(10,GameFactory.BOUNDS_Y - 10), pathFromVel(5,-5)),
      () => BalloonClusterOrigin(new BlobPosition(GameFactory.BOUNDS_X - 10,GameFactory.BOUNDS_Y - 10), pathFromVel(-5,-5)),
      () => BalloonClusterOrigin(new BlobPosition(GameFactory.BOUNDS_X / 2,GameFactory.BOUNDS_Y - 10), pathFromVel(0,-5))
  )

  def random = origins(Balloons.rnd.nextInt(origins.size))()
}

object BalloonCluster {
  
  def balloonCluster(num:Int, t:BlobTransform):BlobIF = {
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
  
  val clusterSizes = List(1,2,3,4,5)
  def randomCluster(t:BlobTransform):BlobIF = {
    balloonCluster(clusterSizes(Balloons.rnd.nextInt(clusterSizes.size)), t)
  }
}