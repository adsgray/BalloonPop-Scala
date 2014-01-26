package com.adsg0186.balloonpop

import android.app.Activity
import android.os.Bundle

class SettingsActivity extends Activity with ActivityUtil {

  override def onCreate(savedInstanceState:Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings);
  }

}