package ru.ya.vn91.robotour

case class Branch(name: String, subNodes: List[GameNode]) extends GameNode

case class Leaf(name: String) extends GameNode

sealed abstract class GameNode {
	val name: String

	override def toString = treeToString("", isTail = true)

	def treeToString(prefix: String, isTail: Boolean): String = this match {
		case l: Leaf => {
			prefix +
				(if (isTail) "└── " else "├── ") +
				name+"\n"
		}
		case Branch(bName, subNodes) => {
			prefix +
				(if (isTail) "└── " else "├── ") +
				bName+"\n"+
				subNodes.dropRight(1).map(_.treeToString(
					prefix + (if (isTail) "    " else "│   "), isTail = false)).fold("")(_ + _) +
				subNodes.last.treeToString(prefix + (if (isTail) "    " else "│   "), isTail = true)
		}
	}

	/*
 * head = <tr><td><td><td>
 * tail = </tr> <tr> td-td-td </tr> <tr> td-td-td </tr> ...
 * height = height of the matrix
 * 
 * this construction allows us to build a table for the tournament game
 */
	class HtmlTable(val head: String, val tail: String,
		//		val headName: String,
		val height: Int)

	private def treeToHtmlEntry: HtmlTable = this match {
		case l: Leaf => new HtmlTable("<tr><td>"+xml.Text(name).toString+"</td>", "</tr>\n",
			//			name, 
			1)
		case Branch(bName, subNodes) => {
			val subHtml = subNodes.map(_.treeToHtmlEntry)
			val height = subHtml.map(_.height).reduce(_ + _)
			val head = subHtml(0).head+"<td rowspan=\""+height+"\">"+xml.Text(bName).toString+"</td>"
			val tail = subHtml(0).tail + subHtml.drop(1).map(y => y.head + y.tail).fold("")(_ + _)
			//			def rowspan(i: Int) = if (i <= 1) "" else " rowspan="+i
			//				"<td"+rowspan(y.height)+">"+y.headName+"</td>"+
			new HtmlTable(head, tail,
				//				bName,
				height)
		}
	}

	def toHtml: xml.Node = {
		val entry = treeToHtmlEntry
		<table border="1">
			{ xml.Unparsed(entry.head + entry.tail) }
		</table>
		// "<table border=1>\n"+entry.head + entry.tail+"</table>"
	}

}
