package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.blob.BlobIF
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobTrigger
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobTransform
import com.github.adsgray.gdxtry1.engine.blob.BlobIF.BlobSource

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

object BlobengineUtil {

}