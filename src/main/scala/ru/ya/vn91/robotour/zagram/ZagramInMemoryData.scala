package ru.ya.vn91.robotour.zagram

import scala.collection.immutable

object ZagramInMemoryData {

	/**
	 * UGLY way to store in-memory data.
	 * Correctness is currently obtained by simplicity of usages...
	 */
	@volatile var playerSet = immutable.HashMap[String, PlayerInfo]()

}
