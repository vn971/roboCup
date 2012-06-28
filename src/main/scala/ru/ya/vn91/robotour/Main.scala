package ru.ya.vn91.robotour

import akka.actor._

object Main {

	def mainnn(args: Array[String]) {
		val i1 = Leaf("*Бам Бам Бигелоу")
		val i2 = Leaf("bartek")
		val i3 = Leaf("Denver")
		val i4 = Leaf("*r5")
		val i5 = Leaf("*zizibo")
		val i6 = Leaf("inDI")
		val i7 = Leaf("Вася Новиков")
		val i8 = Leaf("*wojtas saper")
		val i9 = Leaf("ysipysi")
		val i10 = Leaf("KvanTTT")
		val i11 = Leaf("agent47")
		val i12 = Leaf("Tem13")
		val i13 = Leaf("Andersen")
		val i14 = Leaf("keij")
		val i15 = Leaf("Nnoitora")
		val i16 = Leaf("Pont")
		val i17 = Leaf("andreyko")
		val i18 = Leaf("Putin")

		val i19 = Branch("*Бам Бам Бигелоу", List(i2, i1))
		val i20 = Branch("*r5", List(i3, i4))

		val i21 = Branch("*zizibo", List(i5, i6))
		val i22 = Branch("Вася Новиков", List(i7, i8))
		val i23 = Branch("ysipysi", List(i9, i1))
		val i24 = Branch("agent47", List(i10, i11))
		val i25 = Branch("Tem13", List(i12, i13))
		val i26 = Branch("*r5", List(i4, i14))
		val i27 = Branch("Nnoitora", List(i15, i16))
		val i28 = Branch("Putin", List(i17, i18))

		val i29 = Branch("Putin", List(i26, i28))
		val i30 = Branch("Nnoitora", List(i27, i24))
		val i31 = Branch("ysipysi", List(i25, i23))
		val i32 = Branch("Вася Новиков", List(i21, i22))

		val i33 = Branch("Вася Новиков", List(i30, i32))
		val i34 = Branch("ysipysi", List(i31, i29))

		val i35 = Branch("Вася Новиков", List(i33, i34))

		println(i35.toString)

		//		val a = Leaf("volodarkropek")
		//		val b = Leaf("BearLogo")
		//		val lab = Branch("volodarkropek", List(a, b))
		//		val c = Leaf("Agent47")
		//		val lcab = Branch("Agent47", List(lab, c))
		//		val d = Leaf("Dolf Lundgren")
		//		val e = Leaf("Brendi")
		//		val lde = Branch("Dolf Lundgren", List(d, e))
		//		val lcabde = Branch("Dolf Lundgren", List(lcab, lde))
		//		val xml = lcabde.toHtml
		//		val text = xml.toString
		//		println(xml)
	}
}
