package com.example.trialapplication

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
//import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


class LakeLevel : AppCompatActivity() {

    suspend fun retrieveData(){

        val values: Elements
        val updatedDate: String
        try {
            withContext(Dispatchers.IO) {
                val url = "https://cmwssb.tn.gov.in/lake-level"
                val client = HttpClient(CIO)
                val response: HttpResponse = client.request(url) {
                    method = HttpMethod.Get
                }
                val doc: Document = Jsoup.parse(response.readText());
//                val doc: Document = Jsoup.parse(response.bodyAsText());
                val tableElement: Element =
                    doc.getElementsByClass("lack-view table table-responsive table-striped table-bordered")[0];
                values = tableElement.getElementsByTag("td")

//                tableElement.attr("border", "2")
//                table = tableElement.outerHtml();
                updatedDate =
                    doc.getElementsByAttributeValue("style", "font-size: 18px;")[0].text();
            }
            withContext(Dispatchers.Main) {

                findViewById<TextView>(R.id.lake1Value1).text = values[0].text()
                findViewById<TextView>(R.id.lake1Value2).text = values[1].text()
                findViewById<TextView>(R.id.lake1Value3).text = values[2].text()
                findViewById<TextView>(R.id.lake1Value4).text = values[3].text()
                findViewById<TextView>(R.id.lake1Value5).text = values[4].text()
                findViewById<TextView>(R.id.lake1Value6).text = values[5].text()
                findViewById<TextView>(R.id.lake1Value7).text = values[6].text()
                findViewById<TextView>(R.id.lake1Value8).text = values[7].text()
                findViewById<TextView>(R.id.lake1Value9).text = values[8].text()
                findViewById<TextView>(R.id.lake1Value10).text = values[9].text()

                findViewById<TextView>(R.id.lake2Value1).text = values[10].text()
                findViewById<TextView>(R.id.lake2Value2).text = values[11].text()
                findViewById<TextView>(R.id.lake2Value3).text = values[12].text()
                findViewById<TextView>(R.id.lake2Value4).text = values[13].text()
                findViewById<TextView>(R.id.lake2Value5).text = values[14].text()
                findViewById<TextView>(R.id.lake2Value6).text = values[15].text()
                findViewById<TextView>(R.id.lake2Value7).text = values[16].text()
                findViewById<TextView>(R.id.lake2Value8).text = values[17].text()
                findViewById<TextView>(R.id.lake2Value9).text = values[18].text()
                findViewById<TextView>(R.id.lake2Value10).text = values[19].text()

                findViewById<TextView>(R.id.lake3Value1).text = values[20].text()
                findViewById<TextView>(R.id.lake3Value2).text = values[21].text()
                findViewById<TextView>(R.id.lake3Value3).text = values[22].text()
                findViewById<TextView>(R.id.lake3Value4).text = values[23].text()
                findViewById<TextView>(R.id.lake3Value5).text = values[24].text()
                findViewById<TextView>(R.id.lake3Value6).text = values[25].text()
                findViewById<TextView>(R.id.lake3Value7).text = values[26].text()
                findViewById<TextView>(R.id.lake3Value8).text = values[27].text()
                findViewById<TextView>(R.id.lake3Value9).text = values[28].text()
                findViewById<TextView>(R.id.lake3Value10).text = values[29].text()

                findViewById<TextView>(R.id.lake4Value1).text = values[30].text().replace(" ","\n")
                findViewById<TextView>(R.id.lake4Value2).text = values[31].text()
                findViewById<TextView>(R.id.lake4Value3).text = values[32].text()
                findViewById<TextView>(R.id.lake4Value4).text = values[33].text()
                findViewById<TextView>(R.id.lake4Value5).text = values[34].text()
                findViewById<TextView>(R.id.lake4Value6).text = values[35].text()
                findViewById<TextView>(R.id.lake4Value7).text = values[36].text()
                findViewById<TextView>(R.id.lake4Value8).text = values[37].text()
                findViewById<TextView>(R.id.lake4Value9).text = values[38].text()
                findViewById<TextView>(R.id.lake4Value10).text = values[39].text()

                findViewById<TextView>(R.id.lake5Value1).text = values[40].text()
                findViewById<TextView>(R.id.lake5Value2).text = values[41].text()
                findViewById<TextView>(R.id.lake5Value3).text = values[42].text()
                findViewById<TextView>(R.id.lake5Value4).text = values[43].text()
                findViewById<TextView>(R.id.lake5Value5).text = values[44].text()
                findViewById<TextView>(R.id.lake5Value6).text = values[45].text()
                findViewById<TextView>(R.id.lake5Value7).text = values[46].text()
                findViewById<TextView>(R.id.lake5Value8).text = values[47].text()
                findViewById<TextView>(R.id.lake5Value9).text = values[48].text()
                findViewById<TextView>(R.id.lake5Value10).text = values[49].text()

                findViewById<TextView>(R.id.lake6Value1).text = values[50].text()
                findViewById<TextView>(R.id.lake6Value2).text = values[51].text()
                findViewById<TextView>(R.id.lake6Value3).text = values[52].text()
                findViewById<TextView>(R.id.lake6Value4).text = values[53].text()
                findViewById<TextView>(R.id.lake6Value5).text = values[54].text()
                findViewById<TextView>(R.id.lake6Value6).text = values[55].text()
                findViewById<TextView>(R.id.lake6Value7).text = values[56].text()
                findViewById<TextView>(R.id.lake6Value8).text = values[57].text()
                findViewById<TextView>(R.id.lake6Value9).text = values[58].text()
                findViewById<TextView>(R.id.lake6Value10).text = values[59].text()

                findViewById<TextView>(R.id.totalValue1).text = values[60].text()
                findViewById<TextView>(R.id.totalValue2).text = values[61].text()
                findViewById<TextView>(R.id.totalValue3).text = values[62].text()
                findViewById<TextView>(R.id.totalValue4).text = values[63].text()
                findViewById<TextView>(R.id.totalValue5).text = values[64].text()
                findViewById<TextView>(R.id.totalValue6).text = values[65].text()
                findViewById<TextView>(R.id.totalValue7).text = values[66].text()
                findViewById<TextView>(R.id.totalValue8).text = values[67].text()
                findViewById<TextView>(R.id.totalValue9).text = values[68].text()
                findViewById<TextView>(R.id.totalValue10).text = values[69].text()
//                webView.loadData(table, "text/html; charset=utf-8", "UTF-8")
                findViewById<TextView>(R.id.updatedTextView).text = updatedDate
                findViewById<HorizontalScrollView>(R.id.lakeLevelContent).visibility = View.VISIBLE
            }
        }
        catch(e:java.nio.channels.UnresolvedAddressException){
            withContext(Dispatchers.Main){
                val failedMessage = "Check your Internet Connection."
                findViewById<TextView>(R.id.updatedTextView).text = failedMessage
            }
        }
        catch(e:Exception){
            withContext(Dispatchers.Main){
                val failedMessage = "Failed to load data."
                println("Exception !@!@!")
                println(e.message)
                println(e)
                findViewById<TextView>(R.id.updatedTextView).text = failedMessage
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lake_level_table)
        lifecycleScope.launch {
           retrieveData()
        }
    }
}