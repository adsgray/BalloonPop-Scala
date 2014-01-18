package com.adsg0186.balloonpop

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.adsg0186.balloonpop.implicitOps._

class MainActivity extends Activity with ActivityUtil {

  override def onCreate(savedInstanceState:Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main);
    
    findView[Button](R.id.play_button).onClick { v => goToActivity(classOf[GDXActivity])}
  }
}