package com.adsg0186.balloonpop

import android.content.Intent
import android.app.Activity
import android.widget.Button
import android.view.View.OnClickListener
import android.content.Context
import android.view.View
import android.widget.Toast
import java.util.Random
import android.os.Bundle


// http://robots.thoughtbot.com/scala-a-better-java-for-android
trait ActivityUtil extends Activity {
  import implicitOps._

  def findView [WidgetType] (id : Int) : WidgetType = {
    findViewById(id).asInstanceOf[WidgetType]
  }

  // the [_ <: Activity] bit means a subclass of Activity
  def goToActivity(arg:Class[_ <: Activity]) = startActivity(new Intent(this, arg))

  /*
  def goToActivity(arg:Class[_ <: Activity], extras:Map[String,Any] = Map[String,Any]()) = 
          startActivity({new Intent(this, arg)}.addExtrasFromMap(extras))
  */
}


// I can see why Scaloid made Context an implicit parameter.

object ClickListener {
  def apply(f: => View => Unit) = new OnClickListener() {
      @Override def onClick(v:View) = f(v)
  }
}

object implicitOps {

  implicit class ExtendedButtonOps(b:Button) {
    def onClick(f: => View => Unit) = b.setOnClickListener(ClickListener(f))
  }

  implicit class ExtentedIntentOps(intent:Intent) {
    def addExtrasFromMap(extras: Map[String, Any]):Intent = {
      extras.keys map { key => 
        intent.putExtra(key, extras.get(key))
      }
      intent
    }

    /*
    def getExtrasToMap():Map[String,Int] = {
      val extras = intent.getExtras()
      extras.
      keys.foldLeft(Map[String,Any]()) {
        case (acc, key) => acc + (key -> intent.)
      }
    }
    * 
    */
  }

}

object ToastMessage {
  lazy val rnd = new Random()
  val messages = List("one", "two", "three")
  def message = messages(rnd.nextInt(messages.length))
  
  def makeToastButton(b:Button, c:Context) = {
    b.setOnClickListener(new OnClickListener() {
      @Override def onClick(v:View) = Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
    })
    b
  }
  
  def makeToastButton2(b:Button, c:Context) = 
    b.setOnClickListener(ClickListener { v => Toast.makeText(c, message, Toast.LENGTH_SHORT).show() })

  def setOnClick(b:Button, f:View => Unit) = b.setOnClickListener(ClickListener(f))
}

class Util {

}
