package com.adsg0186.balloonpop

import android.app.Activity
import android.view.Menu
import android.os.Bundle
import android.widget.Button
import com.adsg0186.balloonpop.implicitOps._
import com.google.example.games.basegameutils.BaseGameActivity

class HighScoreActivity extends BaseGameActivity with ActivityUtil {

  // not really used
  private val REQUEST_LEADERBOARD = 1
  private val REQUEST_ACHIEVEMENTS = 2

  override def onCreate(savedInstanceState:Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_high_score)
    
    findView[Button](R.id.points_leaderboard).onClick { 
      v => startActivityForResult(getGamesClient.getLeaderboardIntent(getResources.getString(R.string.leaderboard_bestround)), REQUEST_LEADERBOARD)
    }

    findView[Button](R.id.pointsperpin_leaderboard).onClick { 
      v => startActivityForResult(getGamesClient.getLeaderboardIntent(getResources.getString(R.string.leaderboard_pointsperpin)), REQUEST_LEADERBOARD)
    }

    findView[Button](R.id.balloons_leaderboard).onClick { 
      v => startActivityForResult(getGamesClient.getLeaderboardIntent(getResources.getString(R.string.leaderboard_balloonspopped)), REQUEST_LEADERBOARD)
    }

    findView[Button](R.id.achievements).onClick { 
      v => startActivityForResult(getGamesClient.getAchievementsIntent(), REQUEST_ACHIEVEMENTS)
    }

  }

   def onSignInSucceeded(): Unit = {
   }

   def onSignInFailed(): Unit = {
   } 

}