package com.adsg0186.balloonpop

import com.github.adsgray.gdxtry1.engine.output.SoundIF
import android.content.Context
import com.github.adsgray.gdxtry1.engine.output.SoundPoolPlayer

class GameSound(context: Context) {
  import GameSound.SoundId._

  val soundpool: SoundIF = new SoundPoolPlayer(context);

  val sounds = Map[SoundId, Int](
    blop -> soundpool.load(R.raw.blop),
    plop -> soundpool.load(R.raw.plop),
    honk -> soundpool.load(R.raw.honk),
    bamwong -> soundpool.load(R.raw.bamwong),
    boum -> soundpool.load(R.raw.boum),
    bien -> soundpool.load(R.raw.bien),
    beautravail -> soundpool.load(R.raw.beautravail),
    yahoo1 -> soundpool.load(R.raw.yahoo1),
    yahoo2 -> soundpool.load(R.raw.yahoo2),
    yahoo3 -> soundpool.load(R.raw.yahoo3),
    tickSound -> soundpool.load(R.raw.tick))

  def playSound(id: SoundId) = sounds get id map { resid => soundpool.play(resid) }
}

object GameSound {
  var instance: Option[GameSound] = None

  def apply(context: Context) = {
    instance = Some(new GameSound(context))
    instance
  }

  object SoundId extends Enumeration {
    type SoundId = Value
    val blop, bamwong, boum, bien, beautravail, yahoo1, yahoo2, yahoo3, tickSound, plop, honk = Value
  }

  import SoundId._
  def playSound(id: SoundId) = instance map { gs => gs.playSound(id) }

  val popList = List(plop, blop)
  def pop = playSound(popList(Balloons.rnd.nextInt(popList.size)))

  val yahooList = List(yahoo1, yahoo2, yahoo3)
  def yahoo = playSound(yahooList(Balloons.rnd.nextInt(yahooList.size)))
  
  val goodJobList = List(bien, beautravail)
  def goodJob = playSound(goodJobList(Balloons.rnd.nextInt(goodJobList.size)))
  
  val asteroidList = List(bamwong)
  def asteroidBam = playSound(asteroidList(Balloons.rnd.nextInt(asteroidList.size)))

  def get = instance
  def destroy = { instance = None }
}