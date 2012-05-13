package ru.ya.vn91.robotour

import akka.actor._

object Main {

	def main(args: Array[String]) {
		val a = Leaf("volodarkropek")
		val b = Leaf("BearLogo")
		val lab = Branch("volodarkropek", List(a, b))
		val c = Leaf("Agent47")
		val lcab = Branch("Agent47", List(lab, c))
		val d = Leaf("Dolf Lundgren")
		val e = Leaf("Brendi")
		val lde = Branch("Dolf Lundgren", List(d, e))
		val lcabde = Branch("Dolf Lundgren", List(lcab, lde))
		val xml = lcabde.toHtml
		val text = xml.toString
		println(xml)
		sys.exit(0)

		//		val system = ActorSystem("MySystem")
		//		val core = system.actorOf(Props(new Core(System.currentTimeMillis() + 1000L * 60 * 5)), name = "core")
	}
}

// 4скрест - хорошо?
// рисовать игры -- таблицей?