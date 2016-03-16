package ru.ya.vn91.robotour

case class GameNode(name: String, children: GameNode*) {

	def toTree: String = toTree("", "").mkString("\n")

	private def toTree(prefix: String, childrenPrefix: String): Seq[String] = {
		val firstLine = prefix + this.name

		val headChildren = this.children.dropRight(1).flatMap { child =>
			child.toTree(childrenPrefix + "├── ", childrenPrefix + "│   ")
		}
		val lastChild = this.children.takeRight(1).flatMap { child =>
			child.toTree(childrenPrefix + "└── ", childrenPrefix + "    ")
		}
		firstLine +: headChildren ++: lastChild
	}

}
