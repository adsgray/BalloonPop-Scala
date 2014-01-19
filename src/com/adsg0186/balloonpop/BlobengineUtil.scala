package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobSource
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobTransform
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobTrigger
import com.github.adsgray.gdxtry1.engine.blob.NullBlob
import com.github.adsgray.gdxtry1.engine.util.AccelFactory
import com.github.adsgray.gdxtry1.engine.util.GameFactory
import com.github.adsgray.gdxtry1.engine.util.PositionFactory
import com.github.adsgray.gdxtry1.engine.blob.decorator.BlobScaleDecorator

object blobTrigger {
  def apply(f: => (BlobIF,  BlobIF) => BlobIF) = new BlobTrigger() {
    override def trigger(source:BlobIF, secondary:BlobIF) = { f(source, secondary) }
  }
}

object blobTransform {
  def apply(f: => BlobIF => BlobIF) = new BlobTransform() {
    override def transform(b:BlobIF) = { f(b) }
  }
}

object blobSource {
  def apply(f: => BlobIF => BlobIF) = new BlobSource() {
    override def generate(source:BlobIF) = { f(source) }
  }
}

object nullBlob {
  def create = new NullBlob(PositionFactory.origin, GameFactory.zeroVelocity(), AccelFactory.zeroAccel(), Balloons.renderer.get)
}

object fastGrower {
  def apply(b:BlobIF) = {
    val entries = Array(
      Array(1500, 1)
    )
    new BlobScaleDecorator(b, entries)
  }
}

object BlobengineUtil {

}