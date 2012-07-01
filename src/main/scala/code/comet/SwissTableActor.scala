package code.comet

import code.comet.GameResultEnumeration._
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import scala.collection.mutable.ListBuffer

class SwissTableActor extends CometActor with CometListener {

	private var table = SwissTableData(0, List[Player]())

	def registerWith = SwissTableSingleton

	override def lowPriority = {
		case t: SwissTableData =>
			table = t
			reRender()
	}

	def render = {

		def shortenName(user: String) =
			if (user.length > 7)
				<abbr title={ user }>{ user.take(5)+".." }</abbr>
			else
				xml.Text(user)

		def gameToHtml(game: Game) = game.result match {
			case Win => <font color="green">{ shortenName(game.opponent) }</font>
			case Loss => <font color="red">{ shortenName(game.opponent) }</font>
			case Draw => shortenName(game.opponent)
			case NotFinished => <font color="grey">{ shortenName(game.opponent) }</font>
		}

		def rowToHtml(player: Player) =
			<tr>
				<td>{ player.name }</td>
				{ player.games.map(g => <td>{ gameToHtml(g) }</td>) }
				{ (player.games.size until table.numberOfTours).map(i => <td>?</td>) }
				<td>{ "%.1f" format player.score }</td>
			</tr>

		"tr" #> table.rows.map(rowToHtml)
	}
}
