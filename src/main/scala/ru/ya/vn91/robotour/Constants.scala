package ru.ya.vn91.robotour

import code.comet.GlobalStatusSingleton
import code.comet.TournamentStatus.ErrorStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import net.liftweb.util.Props
import scala.concurrent.duration._

/** global tournament constants mostly
 */
object Constants {

	// Имя турнира? Например, RoboCup XVII "все возраста". Можно отдельные имена на русском, англ и польском.
	// Дата?
	// Швейцарка? Импорт рейтов? Проходной рейт? Размер поля? Рейтовость?
	// Сек/ход? 4скрест? Время регистрации игроков (в часах) ?
	// Перерыв между партиями (в минутах) ?
	// Имя организатора (например, Vasya Novikov (Вася Новиков), Oleg Anokhin (agent47)) ?

	val registrationTime = Props.getLong("registrationHours").openOrThrowException("").hours

	val startingTime = Props.getInt("startingMinutes").openOrThrowException("").minutes

	val perTurnTime = Props.getInt("secondsPerTurn").openOrThrowException("").seconds

	val gameTimeout = startingTime * 2 + perTurnTime * 9999

	val breakTime = Props.getLong("tourBreakMinutes").openOrThrowException("").minutes

	val expectedTourTime = {
		val turn = perTurnTime min (perTurnTime + 10.seconds) / 2
		startingTime * 3 / 2 + breakTime + turn * 300
	}

	val expectedGameTime = {
		val turn = perTurnTime min (perTurnTime + 10.seconds) / 2
		startingTime + turn * 130
	}

	val isFourCross = Props.getBool("isFourCross").openOrThrowException("")

	val fieldSize = Props.get("fieldSize", "3932")

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
