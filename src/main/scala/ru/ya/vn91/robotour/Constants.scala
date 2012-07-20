package ru.ya.vn91.robotour

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object Constants {

	val registrationLength = 1000L * 60 * 60 * 1

	val secsPerTurn = 2

	val tourBrakeTime = 1000L * 60 * secsPerTurn

	val gameTimeout = 1000L * secsPerTurn * 700

	val zagramGameSettings = "3932noT1F0.60."+secsPerTurn

	val freeInviteTime = 180

	val tournamentName = "RoboCup-X"
	// check html templates also!

	val isKnockout = false

	val rankLimit = 1400

	val importRankInSwiss = true

	def timeLongToString(long: Long) = dateFormatter.format(new Date(long))
	def timeStringToLong(s: String) = dateFormatter.parse(s).getTime

	def timeLongToHours(long: Long) = hoursFormatter.format(new Date(long))

	private val dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm")
	dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

	private val hoursFormatter = new SimpleDateFormat("HH:mm")
	hoursFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

}
