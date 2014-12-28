package code.comet

import code.comet.GameResultEnumeration._
import net.liftweb.http.{ CometActor, CometListener }

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
				<abbr title={ user }>{ user.take(5) + ".." }</abbr>
			else
				xml.Text(user)

		def gameToHtml(game: Game) = game.result match {
			case Win => <font color="green">{ shortenName(game.opponent) }</font>
			case Loss => <font color="red">{ shortenName(game.opponent) }</font>
			case Draw => shortenName(game.opponent)
			case NotFinished => <font color="grey">{ shortenName(game.opponent) }</font>
		}

		def rowToHtml(p: (Player, Int)) = {
			//			<td><b>{ index }</b></td>
			val (player, index) = p
			<tr>
				<td><b>{ player.name }</b></td>
				{ player.games.map(g => <td>{ gameToHtml(g) }</td>) }
				{ (player.games.size until table.numberOfTours).map(i => <td><font color="grey">?</font></td>) }
				<td>{ player.score }</td>
			</tr>
		}

		"tr" #> (table.rows zip (1 to table.rows.size)).map(rowToHtml)
	}
}
