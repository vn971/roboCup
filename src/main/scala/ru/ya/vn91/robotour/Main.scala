package ru.ya.vn91.robotour


object Main {

	def mainnn(args: Array[String]) {
		val i1 = GameNode("*Бам Бам Бигелоу")
		val i2 = GameNode("bartek")
		val i3 = GameNode("Denver")
		val i4 = GameNode("*r5")
		val i5 = GameNode("*zizibo")
		val i6 = GameNode("inDI")
		val i7 = GameNode("Вася Новиков")
		val i8 = GameNode("*wojtas saper")
		val i9 = GameNode("ysipysi")
		val i10 = GameNode("KvanTTT")
		val i11 = GameNode("agent47")
		val i12 = GameNode("Tem13")
		val i13 = GameNode("Andersen")
		val i14 = GameNode("keij")
		val i15 = GameNode("Nnoitora")
		val i16 = GameNode("Pont")
		val i17 = GameNode("andreyko")
		val i18 = GameNode("Putin")

		val i19 = GameNode("*Бам Бам Бигелоу", i2, i1)
		val i20 = GameNode("*r5", i3, i4)

		val i21 = GameNode("*zizibo", i5, i6)
		val i22 = GameNode("Вася Новиков", i7, i8)
		val i23 = GameNode("ysipysi", i9, i1)
		val i24 = GameNode("agent47", i10, i11)
		val i25 = GameNode("Tem13", i12, i13)
		val i26 = GameNode("*r5", i4, i14)
		val i27 = GameNode("Nnoitora", i15, i16)
		val i28 = GameNode("Putin", i17, i18)

		val i29 = GameNode("Putin", i26, i28)
		val i30 = GameNode("Nnoitora", i27, i24)
		val i31 = GameNode("ysipysi", i25, i23)
		val i32 = GameNode("Вася Новиков", i21, i22)

		val i33 = GameNode("Вася Новиков", i30, i32)
		val i34 = GameNode("ysipysi", i31, i29)

		val i35 = GameNode("Вася Новиков", i33, i34)


		println(i35.toTreeString)

	}
}
