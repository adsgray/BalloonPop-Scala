package com.adsg0186.balloonpop

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.adsg0186.balloonpop.implicitOps._
import com.google.example.games.basegameutils.BaseGameActivity

class MainActivity extends BaseGameActivity with ActivityUtil {

  override def onCreate(savedInstanceState:Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main);
    
    findView[Button](R.id.play_button).onClick { v => goToActivity(classOf[GDXActivity])}
    findView[Button](R.id.settings_button).onClick { v => goToActivity(classOf[SettingsActivity])}
    
    GamePreferences.init(getApplicationContext)
  }
  
  
   def onSignInFailed(): Unit = ??? 

   def onSignInSucceeded(): Unit = ???
}