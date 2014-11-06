package ru.ya.vn91.robotour

case class GameNode(name: String, children: GameNode*)


object GameNode {
	def toTree(node: GameNode): String = toTree(node, "", "")

	private def toTree(node: GameNode, prefix: String, childrenPrefix: String): String = {
		val firstLine = prefix + node.name

		val headChildren = node.children.dropRight(1).map { case child =>
			toTree(child, childrenPrefix + "├── ", childrenPrefix + "│   ")
		}
		val lastChild = node.children.takeRight(1).map { case child =>
			toTree(child, childrenPrefix + "└── ", childrenPrefix + "    ")
		}
		(firstLine +: headChildren ++: lastChild).mkString("\n")
//		val subNodes = node.subNodes.zipWithIndex.map { case (sub, index) =>
//			val isTail = index == node.subNodes.indices.last
//			val childPrefix = childrenPrefix + (if (isTail) "└── " else "├── ")
//			val childChildPrefix = childrenPrefix + (if (isTail) "    " else "│   ")
//			goodPrint(sub, childPrefix, childChildPrefix)
//		}
//		(firstLine +: subNodes).mkString("\n")
	}


	private def legacyPrint(node: GameNode, prefix: String = "", isTail: Boolean = true): String = {
		val firstLine = prefix + (if (isTail) "└── " else "├── ") + node.name

		val childrenPrefix = prefix + (if (isTail) "    " else "│   ")
		val children = for (i <- node.children.indices) yield {
			legacyPrint(node.children(i),childrenPrefix, i == node.children.indices.last)
		}

		(firstLine +: children).mkString("\n")
	}
}
