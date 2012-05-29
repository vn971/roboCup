package code
package comet

import net.liftweb.http._
import net.liftweb.actor._
import java.text.SimpleDateFormat
import java.util.TimeZone

/** A singleton that provides chat features to all clients.
 *  It's an Actor so it's thread-safe because only one
 *  message will be processed at once.
 */
object TimeStartSingleton extends LiftActor with ListenerManager {

	private var time = 0L
	val simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH:mm")
	simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

	/** When we update the listeners, what message do we send?
	 *  We send the msgs, which is an immutable data structure,
	 *  so it can be shared with lots of threads without any
	 *  danger or locking.
	 */
	def createUpdate = time

	/** process messages that are sent to the Actor.  In
	 *  this case, we're looking for Strings that are sent
	 *  to the ChatServer.  We append them to our Vector of
	 *  messages, and then update all the listeners.
	 */
	override def lowPriority = {
		case newTime: Long => time = newTime; updateListeners()
	}
}
