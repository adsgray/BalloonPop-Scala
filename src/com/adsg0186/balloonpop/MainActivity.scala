package com.adsg0186.balloonpop

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.adsg0186.balloonpop.implicitOps._

class MainActivity extends Activity with ActivityUtil {

  override def onCreate(savedInstanceState:Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main);
    
    //ToastMessage.makeToastButton(findView[Button](R.id.toastbutton), getApplicationContext())
    /*
    findView[Button](R.id.testbutton).onClick { v => goToActivity(classOf[TestActivity]) }
    findView[Button](R.id.gdxbutton).onClick { v => goToActivity(classOf[GDXActivity])}
    * 
    */
  }
}