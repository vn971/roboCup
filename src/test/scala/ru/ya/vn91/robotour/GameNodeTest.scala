package ru.ya.vn91.robotour

import org.scalatest.{FunSuite, Matchers}
import org.scalautils.TypeCheckedTripleEquals
import ru.ya.vn91.robotour.GameNode._

class GameNodeTest extends FunSuite with Matchers with TypeCheckedTripleEquals {

  test("simple") {
    assert(toTree(GameNode("vasya")) === "vasya")
  }

  test("full") {
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

    val string =
      """
        |vasya
        |├── frosya
        |│   ├── petya
        |│   │   └── masha
        |│   └── kolya
        |└── frosya2
      """.stripMargin.trim

    assert(GameNode.toTree(node) === string)
  }

}
