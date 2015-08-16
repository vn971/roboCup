package ru.ya.vn91.robotour

case class GameNode(name: String, children: GameNode*)

object GameNode {
	def toTree(node: GameNode): String = toTree(node, "", "").mkString("\n")

	private def toTree(node: GameNode, prefix: String, childrenPrefix: String): Seq[String] = {
		val firstLine = prefix + node.name

		val headChildren = node.children.dropRight(1).flatMap { child =>
			toTree(child, childrenPrefix + "├── ", childrenPrefix + "│   ")
		}
		val lastChild = node.children.takeRight(1).flatMap { child =>
			toTree(child, childrenPrefix + "└── ", childrenPrefix + "    ")
		}
		firstLine +: headChildren ++: lastChild
	}

}
