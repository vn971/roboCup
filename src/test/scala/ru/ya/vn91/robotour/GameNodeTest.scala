package ru.ya.vn91.robotour

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{ FunSuite, Matchers }
import ru.ya.vn91.robotour.GameNode._

class GameNodeTest extends FunSuite with Matchers with TypeCheckedTripleEquals {

	test("empty") {
		assert(toTree(GameNode("")) === "")
	}

	test("simple") {
		assert(toTree(GameNode("vasya")) === "vasya")
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
		assert(GameNode.toTree(node) === string)
	}

}
