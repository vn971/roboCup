package code.comet

import code.comet.GameResultEnumeration._
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener

class SwissTableActor extends CometActor with CometListener {

	private var table = SwissTableData(0, List[Row]())

	def registerWith = SwissTableSingleton

	override def lowPriority = {
		case t: SwissTableData =>
			table = t
			reRender()
	}

	def render = {
		def shortenName(user: String) =
			if (user.length > 7)
				<acronym title={ user }>{ user.take(5)+".." }</acronym>
			else
				xml.Text(user)

		def gameToHtml(game: Game) = game.result match {
			case Win => <font color="green">{ shortenName(game.opponent) }</font>
			case Loss => <font color="red">{ shortenName(game.opponent) }</font>
			case Draw => shortenName(game.opponent)
		}

		def rowToHtml(row: Row) =
			<tr>
				<td>{ row.user }</td>
				{ row.games.map(g => <td>gameToHtml(g)</td>) }
				{ (row.games.size until table.numberOfTours).map(<td></td>) }
			</tr>

		val result = <table><tr></tr>{ table.rows.map(rowToHtml) }</table>
		println(result)
		"table *" #> result
	}
}
