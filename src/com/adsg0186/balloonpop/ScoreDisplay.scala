package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.blob.BaseTextBlob
import com.github.adsgray.gdxtry1.engine.output.Renderer
import com.github.adsgray.gdxtry1.engine.position.BlobPosition
import com.github.adsgray.gdxtry1.engine.util.GameFactory
import com.github.adsgray.gdxtry1.engine.util.PathFactory
import com.badlogic.gdx.graphics.Color

/*
    public BaseTextBlob(PositionIF posin, VelocityIF velin,
            AccelIF accel, Renderer gdx, RenderConfigIF rc) {
        super(0, posin, velin, accel, gdx);
        renderConfig = rc;
    }
    * 
    */

class ScoreDisplay(val r:Renderer) extends BaseTextBlob(null, null, null, r, null) {
  // at the bottom:
  setPosition(new BlobPosition(10,100))
  setPath(PathFactory.stationary)
  val rc = new r.TextConfig(Color.WHITE, 2.5f)
  setRenderConfig(rc)
}

object ScoreDisplay {

  var display:Option[ScoreDisplay] = None

  def apply(r:Renderer):ScoreDisplay = display match {
    case None => 
      display = Some(new ScoreDisplay(r))
      display.get
    case Some(d) => 
      display.get
  }
  
  def refreshText = display match {
    case Some(d) => d.setText(s"Score: ${GameState.score}    Pins used: ${GameState.pins}")
    case None => Unit // error
  }

  // braces for clarity
  def destroy = { display = None }
  
}