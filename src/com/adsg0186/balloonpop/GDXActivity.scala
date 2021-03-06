package com.adsg0186.balloonpop

import java.util.Timer
import java.util.TimerTask

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.github.adsgray.gdxtry1.engine.WorldIF
import com.github.adsgray.gdxtry1.engine.input.SimpleDirectionGestureDetector
import com.github.adsgray.gdxtry1.engine.output.Renderer
import com.github.adsgray.gdxtry1.engine.util.Game
import com.github.adsgray.gdxtry1.engine.util.GameFactory
import com.github.adsgray.gdxtry1.engine.util.LocalHighScore
import com.github.adsgray.gdxtry1.engine.util.WorldTickTask

import android.content.Context
import android.os.Bundle
import android.util.Log

//class GDXActivity extends Activity {
class GDXActivity extends AndroidApplication with ActivityUtil {

  //protected val c = getApplicationContext()

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    // initialize a new instance of your Game class
    initialize(new GDXApp(getApplicationContext), false);
  }

  class GDXApp(context: Context) extends ApplicationListener {
    val CAMERA_WIDTH = 800;
    val CAMERA_HEIGHT = 1422;

    // TODO: fix
    var world: WorldIF = null
    var shapes: ShapeRenderer = null
    var batch: SpriteBatch = null
    var camera: OrthographicCamera = null
    var renderer: Renderer = null
    var game: Game = null

    var worldTimer: Option[Timer] = None
    var worldTick: Option[TimerTask] = None

    def startWorldTicker = {
      // create timer task that will call tick on world every 25 ms
      val ticksPerSecond = 25
      val msBetweenTicks = 1000 / ticksPerSecond

      worldTimer = worldTimer match {
        case t @ Some(timer) => t
        case None => Some(new Timer("worldTickTimer"))
      }

      worldTick = worldTick match {
        case t @ Some(timertask) => t
        case None => Some(WorldTickTask.createInstance(world))
      }

      for {
        timer <- worldTimer
        task <- worldTick
      } yield timer.scheduleAtFixedRate(task, 0, msBetweenTicks)
    }

    def create(): Unit = {
      shapes = new ShapeRenderer()
      batch = new SpriteBatch()
      camera = new OrthographicCamera()
      camera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT); // the camera is like a window into our game world
      Renderer.createRealInstance(shapes, batch)
      renderer = Renderer.getRealInstance
      world = GameFactory.defaultWorld;
      LocalHighScore.createInstance(context)

      if (GamePreferences.get.sound) {
        GameSound(context)
      }
      
      if (GamePreferences.get.vibrate != 0) {
        Vibrate(context)
      }

      Gdx.graphics.setContinuousRendering(false)
      game = new BalloonPopGame(world, renderer)
      game.init
      game.start
      Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(camera, game.asInstanceOf[BalloonPopGame].getDirectionListener))
      startWorldTicker
    }

    def dispose(): Unit = {
      shapes.dispose
      batch.dispose
      Renderer.get().dispose
      GameSound.destroy
      Vibrate.destroy
      game.stop
    }

    def pause(): Unit = {
      worldTick map { t => t.cancel }
      worldTimer map { t => t.cancel; t.purge }
      worldTick = None
      worldTimer = None
      game.save
    }

    def render(): Unit = {
      Gdx.gl.glClearColor(0f, 0f, 0f, 0.4f)
      Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)

      camera.update
      batch.setProjectionMatrix(camera.combined)
      shapes.setProjectionMatrix(camera.combined)
      world.render
    }

    def resize(x1: Int, x2: Int): Unit = {}

    def resume(): Unit = {
      Log.d("trace", "GameScreen resume");
      worldTimer match {
        case None => startWorldTicker
        case _ => Unit
      }
    }

  }
}