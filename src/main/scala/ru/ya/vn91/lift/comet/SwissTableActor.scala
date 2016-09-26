package ru.ya.vn91.lift.comet

import net.liftweb.http.{ CometActor, CometListener }
import ru.ya.vn91.lift.comet.GameResultEnumeration._
import ru.ya.vn91.robotour.SwissCore
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

		def shortNameWithAbbr(user: String): Node = if (user.length > 7) {
			<abbr title={ user }> { user.take(5) + ".." } </abbr>
		} else {
			xml.Text(user)
		}

		def gameToHtml(player: Player, game: Game): Node = {
			val short = shortNameWithAbbr(game.opponent)
			val htmlClass = game.result.toString.toLowerCase

			// TODO: remove the `emptyPlayer` hack
			val noHyperlink = game.result == NotFinished ||
				player.name == SwissCore.emptyPlayer ||
				game.opponent == SwissCore.emptyPlayer

			if (noHyperlink) {
				<div class={ htmlClass }>{ short }</div>
			} else {
				val url = dispatch.url("http://eidokropki.reaktywni.pl/games-adv.phtml")
					.addQueryParameter("tourn", "RoboCup")
					.addQueryParameter("a", player.name)
					.addQueryParameter("b", game.opponent).url
				<a href={ url } target="_blank" class={ htmlClass }>{ short }</a>
			}
		}

		def rowToHtml(player: Player) = {
			<tr>
				<td><b>{ player.name }</b></td>
				{ player.games.map(game => <td>{ gameToHtml(player, game) }</td>) }
				{ (player.games.size until table.numberOfTours).map(i => <td><font color="grey">?</font></td>) }
				<td>{ player.score }</td>
			</tr>
		}

		"tr" #> table.rows.map(rowToHtml)
	}
}
