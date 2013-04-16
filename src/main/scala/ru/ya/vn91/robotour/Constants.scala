package ru.ya.vn91.robotour

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/** global tournament constants mostly
 */
object Constants {

	// Имя турнира? Например, RoboCup XVII "все возраста". Можно отдельные имена на русском, англ и польском.
	// Дата?
	// Швейцарка? Импорт рейтов? Проходной рейт? Размер поля? Рейтовость?
	// Сек/ход? 4скрест? Время регистрации игроков (в часах) ?
	// Перерыв между партиями (в минутах) ?
	// Имя организатора (например, Vasya Novikov (Вася Новиков), Oleg Anokhin (agent47)) ?

	val registrationHours: Long = 3
	val registrationMillis: Long = 1000L * 60 * 60 * registrationHours

	val secsPerTurn: Int = 12

	val tourBrakeTime: Long = 1000L * 60 * 5

	val gameTimeout = 1000L * secsPerTurn * 600

	val isFourCross = true

	def zagramGameSettings = "3932noT"+(if (isFourCross) "4" else "1") +
		(if (isRated) "R" else "F")+
		"0."+(startingMinutes * 60)+"."+secsPerTurn

	val organizerNickname = "Вася Новиков" // a "chat" room will be created with this nickname
	val organizerName = "Вася Новиков"
	val tournamentName = "RoboCup XIX league-start" // check resources(localizations)

	val timeWaitingOpponent: Int = 60
	val startingMinutes: Int = 5

	val isKnockout = false

	val isRated = true

	val rankLimit: Int = 1100

	val importRankInSwiss = false

	def timeLongToString(long: Long) = dateFormatter.format(new Date(long))
	def timeStringToLong(s: String) = dateFormatter.parse(s).getTime

	def timeLongToHours(long: Long) = hoursFormatter.format(new Date(long))

	private val dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm")
	dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

	private val hoursFormatter = new SimpleDateFormat("HH:mm")
	hoursFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

}
