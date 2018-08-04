package ru.ya.vn91.robotour

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FunSuite, Matchers}

class GameNodeTest extends FunSuite with Matchers with TypeCheckedTripleEquals {

	test("empty") {
		assert(GameNode("").toTree === "")
	}

	test("simple") {
		assert(GameNode("vasya").toTree === "vasya")
	}

	test("full") {
		val string =
			"""
				|vasya
				|├── frosya
				|│   ├── petya
				|│   │   └── masha
				|│   └── kolya
				|└── frosya2
      """.stripMargin.trim

		val node = GameNode("vasya",
			GameNode("frosya",
				GameNode("petya",
					GameNode("masha")
				),
				GameNode("kolya")
			),
			GameNode("frosya2")
		)
		// println(GameNode.legacyPrint(node))
		// println(GameNode.print(node))
		assert(node.toTree === string)
	}

}
