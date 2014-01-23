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

  def msgDisplay(w: WorldIF, pos: PositionIF, path: BlobPath, msg: String, scale: Float = 5.0f) = {
    val r = Balloons.renderer.get
    val rc = new r.TextConfig(Color.WHITE, scale)
    val fm = new BaseTextBlob(pos, GameFactory.zeroVelocity, AccelFactory.zeroAccel, r, rc)
    fm.setImmortal(true)
    fm.setPath(path)
    fm.setText(msg)
    fm.setWorld(w)
    w.addBlobToWorld(fm)
    fm
  }

  def apply(w: WorldIF) = {
    msgDisplay(w, new BlobPosition(100, 1300), PathFactory.squarePathClockwise(10, 5), "Good Job!");
    msgDisplay(w, new BlobPosition(100, 900), PathFactory.squarePath(10, 5), "Press BACK");
    msgDisplay(w, new BlobPosition(50, 500), PathFactory.squarePath(10, 5), f"Score/Pins: ${GameState.scorePerPin}%.2f", 4.0f);
    import GameSound.SoundId._
    GameSound.playSound(honk)
  }

  def destroy = {}
}