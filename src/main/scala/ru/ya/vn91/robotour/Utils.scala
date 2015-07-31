package ru.ya.vn91.robotour

import scala.util.Try

object Utils {

	def getLinkContent(url: String) =
		Try {
			val source = io.Source.fromURL(url, "UTF-8")
			val result = source.mkString
			source.close()
			result
		}

	def readFromFile(fileName: String) =
		Try {
			val source = io.Source.fromFile(fileName, "UTF-8")
			val content = source.mkString
			source.close()
			content
		}

	def getZagramDecoded(s: String) = s.
		replaceAll("@S", "/").
		replaceAll("@A", "@").
		replaceAll("&#60;", "<").
		replaceAll("&#62;", ">").
		replaceAll("&#39;", "'").
		replaceAll("&#34;", "\"").
		replaceAll("&#45;", "-")

	/** Usable for "WartRemover" suppression, the `NonUnitStatements` warning. */
	implicit class SuppressWartRemover(val any: Any) extends AnyRef {
		def suppressWartRemover() = ()
	}

}
