package ru.ya.vn91.robotour

import akka.actor._

object Main {

	val findTransactions =
		"""
	select trn.*,
		(case when ?=0 then 0 else (select account_sum(trn.transaction_id, account_id) from account where visitor_id=? and account_type=srcacc.account_type) end) as asum, 
		(select title from transaction_type where type_id=trn.type_id) as type_title, 
		(select login from visitor where visitor_id=trn.visitor_id) as visitor_login, 
		(select login from visitor where visitor_id=srcacc.visitor_id) as source_visitor, 
		(select login from visitor where visitor_id=dstacc.visitor_id) as destination_visitor, 
		(select currency from bank where bank_id=trn.bank_id) as bank_currency, 
		(select title from bank where bank_id=trn.bank_id) as bank_title 
	from transactions trn, account srcacc, account dstacc 
	where srcacc.account_id=trn.source_id 
		and dstacc.account_id=trn.destination_id 
		and (? in (0, srcacc.visitor_id, dstacc.visitor_id) or trn.visitor_id=?) 
		and (? or trans_time>=?) 
		and (? or trans_time<=?) 
		and (?=0 or (?=1 and trn.type_id in (3, 4, 5, 6, 7, 8, 9, 10, 11, 14)) or (?=2 and trn.type_id in (12))) 
	order by trans_time desc, transaction_id desc
	offset ? limit ?
		"""

	def main(args: Array[String]) {
		val a = Leaf("volodarkropek")
		val b = Leaf("BearLogo")
		val lab = Branch("volodarkropek", List(a, b))
		val c = Leaf("Agent47")
		val lcab = Branch("Agent47", List(lab, c))
		val d = Leaf("Dolf Lundgren")
		val e = Leaf("Brendi")
		val lde = Branch("Dolf Lundgren", List(d, e))
		val lcabde = Branch("Dolf Lundgren", List(lcab, lde))
		val xml = lcabde.toHtml
		val text = xml.toString
		println(xml)
		sys.exit(0)

		//		val system = ActorSystem("MySystem")
		//		val core = system.actorOf(Props(new Core(System.currentTimeMillis() + 1000L * 60 * 5)), name = "core")
	}
}