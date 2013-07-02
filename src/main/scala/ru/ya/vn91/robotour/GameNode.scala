package ru.ya.vn91.robotour

case class GameNode(name: String, subNodes: GameNode*) {

	private def tree(prefix: String, isTail: Boolean): String = {
		val firstLine = prefix + (if (isTail) "└── " else "├── ") + name + '\n'

		val childrenPrefix = prefix + (if (isTail) "    " else "│   ")
		val children = for (i <- subNodes.indices) yield {
			subNodes(i).tree(childrenPrefix, i == subNodes.indices.last)
		}

		firstLine + children.mkString
	}

	def toTreeString = tree("", isTail = true)
}
