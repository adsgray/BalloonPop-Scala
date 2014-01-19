package com.adsg0186.balloonpop

import com.badlogic.gdx.graphics.Color
import com.github.adsgray.gdxtry1.engine.WorldIF
import com.github.adsgray.gdxtry1.engine.blob.BaseTextBlob
import com.github.adsgray.gdxtry1.engine.blob.BlobPath
import com.github.adsgray.gdxtry1.engine.position.BlobPosition
import com.github.adsgray.gdxtry1.engine.position.PositionIF
import com.github.adsgray.gdxtry1.engine.util.AccelFactory
import com.github.adsgray.gdxtry1.engine.util.GameFactory
import com.github.adsgray.gdxtry1.engine.util.PathFactory

object FinalScoreDisplay {

  def msgDisplay(w: WorldIF, pos: PositionIF, path: BlobPath, msg: String) = {
    val r = Balloons.renderer.get
    val rc = new r.TextConfig(Color.WHITE, 5.0f)
    val fm = new BaseTextBlob(pos, GameFactory.zeroVelocity, AccelFactory.zeroAccel, r, rc)
    fm.setImmortal(true)
    fm.setPath(path)
    fm.setText(msg)
    fm.setWorld(w)
    w.addBlobToWorld(fm)
    fm
  }

  def apply(w: WorldIF) = {
    msgDisplay(w, new BlobPosition(100, 1000), PathFactory.squarePathClockwise(10, 5), "Good Job!");
    msgDisplay(w, new BlobPosition(100, 700), PathFactory.squarePath(10, 5), "Press BACK");
    import GameSound.SoundId._
    GameSound.playSound(beautravail)
  }

  def destroy = {}
}