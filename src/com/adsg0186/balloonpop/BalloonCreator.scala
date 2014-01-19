package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.decorator.BlobDecorator
import com.github.adsgray.gdxtry1.engine.blob.NullBlob

class BalloonCreator(b:BlobIF, interval:Int) extends BlobDecorator(b) {
  var count = 0

  override def tick = {
    count += 1
    
    if (count >= interval) {
      count = 0;
      // produce some balloons
    }

    // tick the component and return whatever it returns.
    // it should be immortal...
    component.tick
  }
}

object BalloonCreator {
  var instance:Option[BalloonCreator] = None

  // default is 3 seconds
  def apply(interval:Int = 75) = instance match {
    case None =>
      instance = Some(new BalloonCreator(nullBlob.create, interval))
      instance.get
    case Some(bc) => bc
  }
}