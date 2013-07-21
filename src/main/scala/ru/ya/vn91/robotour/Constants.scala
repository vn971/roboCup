package ru.ya.vn91.robotour

import code.comet.GlobalStatusSingleton
import code.comet.TournamentStatus.ErrorStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import net.liftweb.util.Props
import scala.concurrent.duration._

object Constants {

	val registrationTime = Props.getLong("registrationHours").openOrThrowException("").hours

	val startingTime = Props.getInt("startingMinutes").openOrThrowException("").minutes

	val perTurnTime = Props.getInt("secondsPerTurn").openOrThrowException("").seconds

	val breakTime = Props.getLong("tourBreakMinutes").openOrThrowException("").minutes

	val fieldSizeX = Props.getInt("fieldSizeX", 39)
	val fieldSizeY = Props.getInt("fieldSizeY", 32)
	val fieldSize = fieldSizeX.toString + fieldSizeY.toString

	val gameTimeout = startingTime * 2 + perTurnTime * fieldSizeX * fieldSizeY

	val expectedTourTime = {
		val turn = perTurnTime min (perTurnTime + 10.seconds) / 2
		(startingTime * 3 / 2 + breakTime + turn * 300) * fieldSizeX * fieldSizeY / 30 / 30
	}

	val expectedGameTime = {
		val turn = perTurnTime min (perTurnTime + 10.seconds) / 2
		(startingTime + turn * 130) * fieldSizeX * fieldSizeY / 30 / 30
	}

	val isFourCross = Props.getBool("isFourCross").openOrThrowException("")

	def zagramGameSettings(
			start: FiniteDuration = startingTime,
			turn: FiniteDuration = perTurnTime) =
		fieldSize + "noT" + (if (isFourCross) "4" else "1") +
				(if (isRated) "r" else "F") +
				"0." + start.toSeconds + "." + turn.toSeconds

	val createGameWith = Props.get("createGameWith")

	val organizerCodename = Props.get("organizerCodename").openOrThrowException("")
	val tournamentCodename = Props.get("tournamentCodename").openOrThrowException("")

	val sayHiTime = 60.seconds

	val isKnockout = false

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

	def timeLongToString(long: Long) = dateFormatter.format(new Date(long))
	def timeStringToLong(s: String) = dateFormatter.parse(s).getTime

	def timeLongToHours(long: Long) = hoursFormatter.format(new Date(long))

	private val dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm")
	dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

	private val hoursFormatter = new SimpleDateFormat("HH:mm")
	hoursFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

}
