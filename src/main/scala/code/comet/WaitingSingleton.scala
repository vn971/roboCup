package code.comet

import net.liftweb.actor.LiftActor
import net.liftweb.common.Loggable
import net.liftweb.http.ListenerManager
import ru.ya.vn91.robotour.GameNode

/** A singleton that provides chat features to all clients.
 *  It's an Actor so it's thread-safe because only one
 *  message will be processed at once.
 */
object WaitingSingleton extends LiftActor with ListenerManager with Loggable {
	private var waiting = List[GameNode]()

	/** When we update the listeners, what message do we send?
	 *  We send the msgs, which is an immutable data structure,
	 *  so it can be shared with lots of threads without any
	 *  danger or locking.
	 */
	def createUpdate = waiting

	/** process messages that are sent to the Actor.  In
	 *  this case, we're looking for Strings that are sent
	 *  to the ChatServer.  We append them to our Vector of
	 *  messages, and then update all the listeners.
	 */
	override def lowPriority = {
		case l: List[_] =>
			logger.debug(s"received message $l")
			waiting = l.asInstanceOf[List[GameNode]]
			updateListeners()
		case s =>
			System.err.println("received unknown message: " + s)
	}
}
