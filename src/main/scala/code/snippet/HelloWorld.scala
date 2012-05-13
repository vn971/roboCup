package code
package snippet

import scala.xml.{ NodeSeq, Text }
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import code.lib._
import Helpers._
import code.comet.ChatServer

class HelloWorld {
	//	lazy val str: Box[String] = DependencyFactory.inject[String] // inject the date
	//	def registered = "#registered *" #> "Вася \nФрося \nОлоло\n"

	lazy val ran = math.random
	//	lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

	// replace the contents of the element with id "time" with the date
	def random1 = "#ran *" #> ran.toString
	def random2 = "#ran *" #> ran.toString
	def timezone = "#zone *" #> ChatServer.dateFormatter.getTimeZone.getDisplayName

	/*
	 lazy val date: Date = DependencyFactory.time.vend // create the date via factory
	 def howdy = "#time *" #> date.toString
	*/
}

