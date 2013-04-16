package code.comet

import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.util.ClearClearable
import ru.ya.vn91.robotour.Branch
import ru.ya.vn91.robotour.GameNode

/** The screen real estate on the browser will be represented
 *  by this component.  When the component changes on the server
 *  the changes are automatically reflected in the browser.
 */
class Playing extends CometActor with CometListener {
	private var playing = List[(GameNode, GameNode)]()

	/** When the component is instantiated, register as
	 *  a listener with the ChatServer
	 */
	def registerWith = PlayingSingleton

	/** The CometActor is an Actor, so it processes messages.
	 *  In this case, we're listening for Vector[String],
	 *  and when we get one, update our private state
	 *  and reRender() the component.  reRender() will
	 *  cause changes to be sent to the browser.
	 */
	override def lowPriority = {
		case v: List[(GameNode, GameNode)] => playing = v; reRender()
	}

	/** Put the messages in the li elements and clear
	 *  any elements that have the clearable class.
	 */
	def gameToHtml(game: (GameNode, GameNode)) = {
		val question = Branch("???", game._1 :: game._2 :: Nil)
		//		<br/>,
		//		question.toHtml
		//			<br/>,
		xml.NodeSeq.fromSeq(Seq(
			<pre>{ question.toString }</pre>))
	}

	def render = {
		val nodeSeqList = playing.map(gameToHtml)
		"li *" #> nodeSeqList & ClearClearable
	}
	//	& ClearClearable
}
