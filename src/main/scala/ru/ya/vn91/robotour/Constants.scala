package ru.ya.vn91.robotour

import code.comet.GlobalStatusSingleton
import code.comet.TournamentStatus.ErrorStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import net.liftweb.util.Props

/** global tournament constants mostly
 */
object Constants {

	// Имя турнира? Например, RoboCup XVII "все возраста". Можно отдельные имена на русском, англ и польском.
	// Дата?
	// Швейцарка? Импорт рейтов? Проходной рейт? Размер поля? Рейтовость?
	// Сек/ход? 4скрест? Время регистрации игроков (в часах) ?
	// Перерыв между партиями (в минутах) ?
	// Имя организатора (например, Vasya Novikov (Вася Новиков), Oleg Anokhin (agent47)) ?

	val registrationHours: Long = Props.getLong("registrationHours").openOrThrowException("")
	val registrationMillis: Long = 1000L * 60 * 60 * registrationHours

	val startingMinutes: Int = Props.getInt("startingMinutes").openOrThrowException("")
	val secondsPerTurn: Int = Props.getInt("secondsPerTurn").openOrThrowException("")

	val tourBrakeTime: Long = 1000L * 60 * Props.getLong("tourBreakMinutes").openOrThrowException("")

	val gameTimeout = 1000L * secondsPerTurn * 600

	val isFourCross = Props.getBool("isFourCross").openOrThrowException("")

	def zagramGameSettings = "3932noT"+(if (isFourCross) "4" else "1") +
		(if (isRated) "R" else "F")+
		"0."+(startingMinutes * 60)+"."+secondsPerTurn

	val createGameWith = Props.get("createGameWith").flatMap(s => if (s.isEmpty) None else Some(s))

	val organizerCodename = Props.get("organizerCodename").openOrThrowException("")
	val tournamentCodename = Props.get("tournamentCodename").openOrThrowException("")

	val sayHiTime: Int = Props.getInt("sayHiTime").openOrThrowException("")

	val isKnockout = false

	val isRated = Props.getBool("isRated").openOrThrowException("")

	val rankLimit: Option[Int] = Props.getInt("rankLimit")

	val importRankInSwiss = false

	val adminPage = Props.get("adminPage").flatMap(s => if (s.isEmpty) None else Some(s))

	val zagramIdGracza = {
		val z = Props.get("zagramIdGracza").flatMap(s => if (s.isEmpty) None else Some(s))
		if (z.isEmpty) GlobalStatusSingleton ! ErrorStatus("zagram idGracza not found!")
		z
	}

	val zagramAssignGamePassword = {
		val z = Props.get("zagramAssignGamePassword").flatMap(s => if (s.isEmpty) None else Some(s))
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
