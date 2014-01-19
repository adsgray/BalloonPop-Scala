package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobSource
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobTransform
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobTrigger
import com.github.adsgray.gdxtry1.engine.blob.NullBlob
import com.github.adsgray.gdxtry1.engine.blob.decorator.BlobScaleDecorator
import com.github.adsgray.gdxtry1.engine.position.BlobPosition
import com.github.adsgray.gdxtry1.engine.util.AccelFactory
import com.github.adsgray.gdxtry1.engine.util.GameFactory
import com.github.adsgray.gdxtry1.engine.util.PositionFactory
import com.github.adsgray.gdxtry1.engine.blob.BaseTextBlob
import com.badlogic.gdx.graphics.Color
import com.github.adsgray.gdxtry1.engine.util.PathFactory

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
  def apply(b:BlobIF, scale:Int = 1500) = {
    val entries = Array(
      Array(scale, 1)
    )
    new BlobScaleDecorator(b, entries)
  }
}

object flashMessageAtBlob {
  // at position of b
  def apply(b:BlobIF, msg: String) = {
    val r = b.getRenderer
    // cannot do new b.getRenderer.TextConfig() because "stable identifier required"
    val rc = new r.TextConfig(Color.WHITE, 2.5f)
    val fm = new BaseTextBlob(new BlobPosition(b.getPosition), GameFactory.zeroVelocity, AccelFactory.zeroAccel, r, rc)
    fm.setPath(PathFactory.squarePath(10, 3))
    fm.setLifeTime(25) // 1 second
    fm.setText(msg)
    fm
  }
}

object BlobengineUtil {

}