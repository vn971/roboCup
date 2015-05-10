package ru.ya.vn91.robotour

import code.comet.GlobalStatusSingleton
import code.comet.TournamentStatus.ErrorStatus
import net.liftweb.common.Loggable
import net.liftweb.util.Props
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormat
import scala.concurrent.duration._

object Constants extends Loggable {

	val registrationPeriod = Props.getLong("registrationHours").openOrThrowException("").hours

	val startingTime = Props.getInt("startingMinutes").openOrThrowException("").minutes

	val perTurnTime = Props.getInt("secondsPerTurn").openOrThrowException("").seconds

	val breakTime = Props.getLong("tourBreakMinutes", 5).minutes

	val withTerritory = Props.getBool("withTerritory").openOr(false)

	val crossesCount = Props.getInt("crossesCount").openOrThrowException("")

	val fieldSizeX = Props.getInt("fieldSizeX", 39)
	val fieldSizeY = Props.getInt("fieldSizeY", 32)

	val gameTimeout = startingTime * 2 + perTurnTime * fieldSizeX * fieldSizeY +
		(if (Props.devMode) 10.seconds else 10.minutes)

	/** "tour time" means maximum for all games in a tour
	 */
	val (expectedGameTime, expectedTourTime) = {
		def dotCount(power: Double) = math.pow(fieldSizeX * fieldSizeY, power)
		val tourDotCount = if (withTerritory) dotCount(1.0) else dotCount(0.75)
		val gameDotCount = if (withTerritory) dotCount(1.0) * 0.8 else dotCount(0.75) * 0.5

		logger.info(s"gameDotCount: ${gameDotCount.toInt}, tour: ${tourDotCount.toInt}")
		val realTurnTime = perTurnTime min (perTurnTime + 10.seconds) / 2
		val game = startingTime + realTurnTime * gameDotCount
		val tour = startingTime * 1.5 + breakTime + realTurnTime * tourDotCount
		(game, tour)
	}

	/** @see http://zagram.org/doc.html */
	def zagramGameSettings(isInfiniteTime: Boolean) = {
		val x = fieldSizeX
		val y = fieldSizeY
		val territory = if (withTerritory) "t" else "n"
		val instantWin = "0" // disable
		val crosses = if (crossesCount == 0) "" else crossesCount.toString
		val rated = if (isRated) "R" else "F"
		val infinite = if (isInfiniteTime) "n" else "a"
		val start = if (isInfiniteTime) "" else startingTime.toSeconds.toString
		val turn = if (isInfiniteTime) "" else perTurnTime.toSeconds.toString
		s"$x$y$territory$instantWin.$crosses.a.$rated.$infinite.$start.$turn.."
	}

	val createGameWith = Props.get("createGameWith")

	val organizerCodename = Props.get("organizerCodename").openOrThrowException("")
	val tournamentCodename = Props.get("tournamentCodename").openOrThrowException("")

	val sayHiTime = 30.seconds

	val isSwiss = Props.getBool("isSwiss").openOr(true)

	val isRated = Props.getBool("isRated").openOr(true)

	val rankLimit = Props.getInt("rankLimit")

	val importRankInSwiss = false

	val adminPage = Props.get("adminPage")

	val zagramIdGracza = {
		val z = Props.get("zagramIdGracza")
		if (z.isEmpty) GlobalStatusSingleton ! ErrorStatus("zagram idGracza not found!")
		z
	}

	val zagramAssignGamePassword = {
		val z = Props.get("zagramAssignGamePassword")
		if (z.isEmpty) GlobalStatusSingleton ! ErrorStatus("zagram gameAssignPass not found!")
		z
	}

	val zagramTournamentCodename = Props.get("zagramTournamentCodename").openOrThrowException("")

	def timeLongToString(long: Long) = datetimeFormatter.print(new DateTime(long))

	def stringToDateTime(s: String) = datetimeFormatter.parseDateTime(s)

	def timeLongToHours(long: Long) = hoursFormatter.print(new DateTime(long))

	private val datetimeFormatter = DateTimeFormat.forPattern("yyyy.MM.dd HH:mm").withZone(DateTimeZone.forID("Europe/Moscow"))

	private val hoursFormatter = DateTimeFormat.forPattern("HH:mm").withZone(DateTimeZone.forID("Europe/Moscow"))

}
