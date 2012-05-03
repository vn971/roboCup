package ru.ya.vn91.robotour
import java.net.URLEncoder
import scala.io.Source

object Utils {
	def getLinkContent(url: String): String = {
		//		log.info("getting "+url)
		val buffer = new StringBuilder
		Source.fromURL(url, "UTF-8").foreach(buffer+)
		//		log.info(" = "+buffer.toString)
		buffer.toString
	}

	def getServerEncoded(s: String) =
		// .replaceAll("@", "@A")
		// .replaceAll("/", "@S")
		URLEncoder.encode(s, "UTF-8")

	def getServerDecoded(s: String) = s.
		replaceAll("@S", "/").
		replaceAll("@A", "@").
		replaceAll("&#60;", "<").
		replaceAll("&#62;", ">").
		replaceAll("&#39;", "'").
		replaceAll("&#34;", "\"").
		replaceAll("&#45;", "-")

	def writeToFile(content: String, fileName: String) = {
		val file = new java.io.PrintStream(new java.io.FileOutputStream(fileName))
		file.println(content)
		file.close
	}
}