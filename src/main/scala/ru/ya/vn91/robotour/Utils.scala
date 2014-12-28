package ru.ya.vn91.robotour

import java.io.IOException
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

	def writeToFile(content: String, fileName: String) {
		var file: java.io.FileWriter = null
		try {
			file = new java.io.FileWriter(fileName)
			file.write(content)
		} finally {
			try {
				file.close()
			} catch {
				case e: IOException => e.printStackTrace()
			}
		}
	}

}
