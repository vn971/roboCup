package ru.ya.vn91.robotour

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import net.liftweb.common.Loggable
import org.scalatest.FunSuite
import org.scalatest.Matchers
import ru.ya.vn91.robotour.zagram.PlayerInfo

class ActorTesting extends FunSuite with Matchers with Loggable {

	ignore("registration works OK") {
		implicit val system = ActorSystem("test")
		val actorRef = TestActorRef[SwissCore]

		actorRef ! StartRegistration(0L)
		actorRef ! StartRegistrationReally(0L)
		actorRef ! TryRegister(PlayerInfo("nick", 0, 0, 0, 0))
		actorRef ! TryRegister(PlayerInfo("nick2", 0, 0, 0, 0))

		val actor = actorRef.underlyingActor
		assert(actor.openGames.size == 0)
		assert(actor.registered.size == 2)
		system.shutdown()
	}

}
