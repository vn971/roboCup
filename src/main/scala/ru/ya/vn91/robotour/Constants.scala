package ru.ya.vn91.robotour

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object Constants {

	// Имя турнира?
	// Дата?
	// Швейцарка? Импорт рейтов? Проходной рейт? Размер поля? Рейтовость?
	// Сек/ход? Время ожидания соперника на 1ый ход? 4скрест?
	// Начальное время? Время регистрации игроков?

	val registrationLength: Long = 1000L * 60 * 60 * 4

	val secsPerTurn: Int = 10

	val tourBrakeTime: Long = 1000L * 60 * 6

	val gameTimeout = 1000L * secsPerTurn * 500

	val fourCross = false

	val zagramGameSettings = "3030noT"+(if (fourCross) "4" else "1") + (if (assignRatedGames) "R" else "F")+"0.180."+secsPerTurn

	val organizatorName = "Vanya Geyko (Ваня Гейко)"
	val tournamentName = """RoboCup XV GIP"""
	// check html templates also!

	val timeWaitingOpponent: Int = 60

	val isKnockout = true

	val assignRatedGames = false

	val rankLimit: Int = 0

	val importRankInSwiss = false

	def timeLongToString(long: Long) = dateFormatter.format(new Date(long))
	def timeStringToLong(s: String) = dateFormatter.parse(s).getTime

	def timeLongToHours(long: Long) = hoursFormatter.format(new Date(long))

	private val dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm")
	dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

	private val hoursFormatter = new SimpleDateFormat("HH:mm")
	hoursFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

}
