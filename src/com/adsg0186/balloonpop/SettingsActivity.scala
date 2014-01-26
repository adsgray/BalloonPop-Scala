package com.adsg0186.balloonpop

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.CheckBox
import com.adsg0186.balloonpop.implicitOps._

class SettingsActivity extends Activity with ActivityUtil {

  override def onCreate(savedInstanceState:Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    initVibrateRadioButtons()
    initSoundCheckBox()
  }
  
  /*
  override def onResume = {
    super.onResume
    initVibrateRadioButtons()
    initSoundCheckBox()
  }
  * *
  */

  def onRadioButtonClicked(rb:View) = {
    // ugh
    val checked = rb.asInstanceOf[RadioButton].isChecked
    
    val vibratePref = rb.getId() match {
      case R.id.vibrate_off => 0
      case R.id.vibrate_low => 1
      case R.id.vibrate_high => 2
    }
    
    if (checked) {
      GamePreferences.setVibrate(vibratePref)
    }
  }
  
  // set sound checkbox state from saved preferences
  // and set up onCheckedChangeListener
  def initSoundCheckBox() = {
    val checkbox = findView[CheckBox](R.id.soundcheckbox)
    checkbox.setChecked(GamePreferences.getSound)
    checkbox.onCheckedChange { (b, checked) => GamePreferences.setSound(checked) }
  }
  
  // set the radio button state from saved preferences
  def initVibrateRadioButtons() = {
    //val vibratePref = GamePreferences.getVibrate

    // disable all radio buttons
    List(R.id.vibrate_off, R.id.vibrate_low, R.id.vibrate_high) map { 
      id => findView[RadioButton](id).setChecked(false)
    }
    
    // enable the one from preferences
    val rbId = GamePreferences.getVibrate match {
      case 0 => R.id.vibrate_off
      case 1 => R.id.vibrate_low
      case 2 => R.id.vibrate_high
    }
    
    findView[RadioButton](rbId).setChecked(true)
  }
}