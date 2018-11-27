package ru.ya.vn91.lift.comet

import net.liftweb.http._
import net.liftweb.util.ClearNodes

/** The screen real estate on the browser will be represented
 *  by this component.  When the component changes on the server
 *  the changes are automatically reflected in the browser.
 */
class RegisteredList extends CometActor with CometListener {
	private var msgs = Vector.empty[String]

	def registerWith = RegisteredListSingleton

	@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
	override def lowPriority = {
		case v: Vector[_] => msgs = v.asInstanceOf[Vector[String]]; reRender()
	}

	def render = if (msgs.isEmpty)
		ClearNodes
	else
		"li *" #> msgs
}
