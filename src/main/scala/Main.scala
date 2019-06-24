import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model._
import spark.Spark._
import com.google.gson._
import collection.JavaConverters._

object Main extends App {
	val browser = JsoupBrowser()
	val doc = browser.get("http://holtankoljak.hu")
	val prices_tmp1 = (doc >> elementList("table td strong"))
					

	val prices_tmp2 = prices_tmp1
		.map(_ >> allText("strong"))
		.map(_.replaceAll(" Ft", ""))

	val prices = Map(
		"b95" -> prices_tmp2.slice(0, 3),
		"b98" -> prices_tmp2.slice(6, 9),
		"d" -> prices_tmp2.slice(12, 15),
		"b_prem" -> prices_tmp2.slice(18, 21),
		"d_prem" -> prices_tmp2.slice(24, 27),
		"lpg" -> prices_tmp2.slice(30, 33),
		"cng" -> prices_tmp2.slice(36, 39)
	)

	port(9002)
	get("/", (req, res) => {
		res.header("Content-Type", "application/json")
		res.header("Access-Control-Allow-Origin", "*")
		val resp = prices
				.map(el => (el._1, el._2.asJava))
				.asJava
		new Gson().toJson(resp)
	})
}
