package ru.ya.vn91.lift.comet

import net.liftweb.http.{ CometActor, CometListener }
import ru.ya.vn91.lift.comet.GameResultEnumeration._
import scala.xml._

class SwissTableActor extends CometActor with CometListener {

	private var table = SwissTableData(0, List[Player]())

	def registerWith = SwissTableSingleton

	override def lowPriority = {
		case t: SwissTableData =>
			table = t
			reRender()
	}

	def render = {

		def shortenName(user: String): Node =
			if (user.length > 7)
				<abbr title={ user }>{ user.take(5) + ".." }</abbr>
			else
				xml.Text(user)

		def gameToHtml(game: Game): Node = game.result match {
			case Win => <font color="green">{ shortenName(game.opponent) }</font>
			case Loss => <font color="red">{ shortenName(game.opponent) }</font>
			case Draw => shortenName(game.opponent)
			case NotFinished => <font color="grey">{ shortenName(game.opponent) }</font>
		}

		def rowToHtml(player: Player) = {
			<tr>
				<td><b>{ player.name }</b></td>
				{ player.games.map(game => <td>{ gameToHtml(game) }</td>) }
				{ (player.games.size until table.numberOfTours).map(i => <td><font color="grey">?</font></td>) }
				<td>{ player.score }</td>
			</tr>
		}

		"tr" #> table.rows.map(rowToHtml)
	}
}
