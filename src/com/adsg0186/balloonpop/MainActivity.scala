package com.adsg0186.balloonpop

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.adsg0186.balloonpop.implicitOps._
import com.google.example.games.basegameutils.BaseGameActivity
import android.view.View
import android.util.Log

class MainActivity extends BaseGameActivity with ActivityUtil {

  // not really used
  private val REQUEST_LEADERBOARD = 1
  private val REQUEST_ACHIEVEMENTS = 2

  val googleButtonHandler = ClickListener { v => 
    v.getId() match {
      case R.id.sign_in_button => 
        beginUserInitiatedSignIn
      case R.id.sign_out_button =>
        signOut
        findView[Button](R.id.sign_in_button).setVisibility(View.VISIBLE)
        findView[Button](R.id.sign_out_button).setVisibility(View.GONE)
    }
  }

  override def onCreate(savedInstanceState:Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main);
    
    findView[Button](R.id.play_button).onClick { v => goToActivity(classOf[GDXActivity])}
    findView[Button](R.id.settings_button).onClick { v => goToActivity(classOf[SettingsActivity])}

    findView[Button](R.id.sign_in_button).setOnClickListener(googleButtonHandler)
    findView[Button](R.id.sign_out_button).setOnClickListener(googleButtonHandler)
    
    findView[Button](R.id.points_leaderboard).onClick { 
      v => startActivityForResult(getGamesClient.getLeaderboardIntent(getResources.getString(R.string.leaderboard_bestround)), REQUEST_LEADERBOARD)
    }

    findView[Button](R.id.pointsperpin_leaderboard).onClick { 
      v => startActivityForResult(getGamesClient.getLeaderboardIntent(getResources.getString(R.string.leaderboard_pointsperpin)), REQUEST_LEADERBOARD)
    }

    findView[Button](R.id.achievements).onClick { 
      v => startActivityForResult(getGamesClient.getAchievementsIntent(), REQUEST_ACHIEVEMENTS)
    }
    
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
     findView[Button](R.id.points_leaderboard).setVisibility(View.VISIBLE)
     findView[Button](R.id.pointsperpin_leaderboard).setVisibility(View.VISIBLE)
     findView[Button](R.id.achievements).setVisibility(View.VISIBLE)
  }
  
  def signedOutButtons = {
     findView[Button](R.id.sign_in_button).setVisibility(View.VISIBLE)
     findView[Button](R.id.sign_out_button).setVisibility(View.GONE)
     findView[Button](R.id.points_leaderboard).setVisibility(View.GONE)
     findView[Button](R.id.pointsperpin_leaderboard).setVisibility(View.GONE)
     findView[Button](R.id.achievements).setVisibility(View.GONE)
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