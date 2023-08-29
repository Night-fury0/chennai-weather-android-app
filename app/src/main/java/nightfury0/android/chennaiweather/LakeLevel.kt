package nightfury0.android.chennaiweather

import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    private suspend fun retrieveData(){
        val values: Elements
        val headerValues: Elements
        val updatedDate: String
        try {
            withContext(Dispatchers.IO) {
                val url = getString(R.string.lake_level_url)
                val client = HttpClient(CIO)
                val response: HttpResponse = client.request(url) {
                    method = HttpMethod.Get
                }
                val doc: Document = Jsoup.parse(response.readText())
//                val doc: Document = Jsoup.parse(response.bodyAsText());
                val tableElement: Element =
                    doc.getElementsByClass(getString(R.string.lake_level_table_class_name))[0]
                values = tableElement.getElementsByTag("td")
                headerValues = tableElement.getElementsByTag("th")
                updatedDate =
                    doc.getElementsByAttributeValue("style", "font-size: 18px;")[0].text()
            }
            withContext(Dispatchers.Main) {
                val tableLayout = findViewById<TableLayout>(R.id.lakeLevelTableLayout)
                tableLayout.removeAllViews()
                val noOfRows = (values.size/headerValues.size).toInt()
                val tableHeaderRow = TableRow(this@LakeLevel)
                tableHeaderRow.setBackgroundColor(ContextCompat.getColor(this@LakeLevel, R.color.black))
                val rowLayoutParams = TableLayout.LayoutParams()
                rowLayoutParams.weight = 1.0f
                tableHeaderRow.layoutParams = rowLayoutParams
                for (i in 0 until headerValues.size){
                    Templates().formTableCell(
                        context = this@LakeLevel,
                        rowView = tableHeaderRow,
                        textValue = headerValues[i].text().replace(" ","\n"),
                        bgcolor = R.color.dark_dark_grey,
                        textAlign = if (i==0) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_CENTER ,
                        isBold = true
                    )
                }
                tableLayout.addView(tableHeaderRow)
                var k = 0
                for (i in 0 until noOfRows){
                    val tableRow = TableRow(this@LakeLevel)
                    tableHeaderRow.setBackgroundColor(ContextCompat.getColor(this@LakeLevel, R.color.black))
                    tableRow.layoutParams = rowLayoutParams
                    for (j in 0 until headerValues.size){
                        Templates().formTableCell(
                            context = this@LakeLevel,
                            rowView = tableRow,
                            textValue = values[k++].text().replace(" ", "\n"),
                            bgcolor = if (j==0) R.color.grey else R.color.light_grey,
                            textAlign = if (j==0) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_CENTER,
                            isBold = false
                        )
                    }
                    tableLayout.addView(tableRow)
                }
                findViewById<TextView>(R.id.updatedTextView).text = updatedDate
                findViewById<HorizontalScrollView>(R.id.lakeLevelContent).visibility = View.VISIBLE
            }
        }
        catch(e:java.nio.channels.UnresolvedAddressException){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_internet_failure)
                findViewById<TextView>(R.id.updatedTextView).text = failedMessage
            }
        }
        catch(e:Exception){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_unable_to_retrieve)
                println("Exception !@!@!")
                println(e.message)
                findViewById<TextView>(R.id.updatedTextView).text = failedMessage
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lake_level)
        lifecycleScope.launch {
           retrieveData()
        }
    }
}