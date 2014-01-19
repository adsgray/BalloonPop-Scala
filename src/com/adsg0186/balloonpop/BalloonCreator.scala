package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.decorator.BlobDecorator
import com.github.adsgray.gdxtry1.engine.blob.NullBlob
import android.util.Log
import com.github.adsgray.gdxtry1.engine.WorldIF

class BalloonCreator(b: BlobIF, num: Int, interval: Int) extends BlobDecorator(b) {
  var count = 0
  Log.d("trace", "BalloonCreator created")

  val generator = blobSource { source =>
    // produce a balloon cluster
    // The transform passed to randomCluster is what to do
    // with each member of the cluster. In this case, make it a 'target' 
    val bc = BalloonCluster.randomCluster(blobTransform { blob =>
      Log.d("trace", "adding target to world")
      blob.setWorld(b.getWorld)
      b.getWorld.addTargetToWorld(blob)
      blob
    })

    bc.setLifeTime(500)
    bc.setWorld(b.getWorld)
    b.getWorld.addBlobToWorld(bc)
    source
  }

  override def tick = {
    count += 1

    if (count >= interval) {
      count = 0
      (1 to num) map { i => generator.get(b) }
    }

    // tick the component and return whatever it returns.
    // it should be immortal...
    component.tick
  }

  def destroy = {
    getWorld.removeBlobFromWorld(this)
    setLifeTime(0)
  }
}

object BalloonCreator {
  var instance: Option[BalloonCreator] = None

  // default is 3 seconds
  def apply(w: WorldIF, num: Int = 3, interval: Int = 75) = instance match {
    case None =>
      instance = Some(new BalloonCreator(nullBlob.create, num, interval))
      instance map { b =>
        b setWorld w
        b setImmortal true
        w addBlobToWorld b
      }
      instance.get
    case Some(bc) => bc
  }
}