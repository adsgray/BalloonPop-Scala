package com.adsg0186.balloonpop

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.adsg0186.balloonpop.implicitOps._
import com.google.example.games.basegameutils.BaseGameActivity
import android.view.View
import android.util.Log

class MainActivity extends BaseGameActivity with ActivityUtil {

  val googleButtonHandler = ClickListener { v => 
    v.getId() match {
      case R.id.sign_in_button => 
        beginUserInitiatedSignIn
      case R.id.sign_out_button =>
        signOut
        signedOutButtons
    }
  }

  override def onCreate(savedInstanceState:Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main);
    
    findView[Button](R.id.play_button).onClick { v => goToActivity(classOf[GDXActivity])}
    findView[Button](R.id.settings_button).onClick { v => goToActivity(classOf[SettingsActivity])}
    findView[Button](R.id.highscores).onClick { v => goToActivity(classOf[HighScoreActivity])}

    findView[Button](R.id.sign_in_button).setOnClickListener(googleButtonHandler)
    findView[Button](R.id.sign_out_button).setOnClickListener(googleButtonHandler)
    
    GamePreferences.init(getApplicationContext)
    AchievementTracker.init(getApplicationContext)
  }
  
  /*
  override def onResume = {
    super.onResume
    AchievementTracker.saveToGoogle(getGamesClient)
  }
  * 
  */
  
  def signedInButtons = {
     findView[Button](R.id.sign_in_button).setVisibility(View.GONE)
     findView[Button](R.id.sign_out_button).setVisibility(View.VISIBLE)
     findView[Button](R.id.highscores).setVisibility(View.VISIBLE)
  }
  
  def signedOutButtons = {
     findView[Button](R.id.sign_in_button).setVisibility(View.VISIBLE)
     findView[Button](R.id.sign_out_button).setVisibility(View.GONE)
     findView[Button](R.id.highscores).setVisibility(View.GONE)
  }

   def onSignInSucceeded(): Unit = {
     signedInButtons
     // (your code here: update UI, enable functionality that depends on sign in, etc)
     Log.d("scores", "submitting scores")
     AchievementTracker.saveToGoogle(getGamesClient)
   }

   def onSignInFailed(): Unit = {
     signedOutButtons
   } 

}