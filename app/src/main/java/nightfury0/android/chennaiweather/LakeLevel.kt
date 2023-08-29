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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    private lateinit var updatedTextView: TextView
    private lateinit var lakeLevelContent: HorizontalScrollView

    private suspend fun retrieveData(){
        val values: Elements
        val headerValues: Elements
        val updatedDate: String
        try {
            withContext(Dispatchers.Main){
                lakeLevelContent.visibility = View.INVISIBLE
                updatedTextView.text = resources.getString(R.string.loading_text)
                updatedTextView.visibility = View.VISIBLE
            }
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
                Templates().formTableFromHtml(this@LakeLevel, tableLayout, headerValues, values)
                for (rowIndex in 0 until tableLayout.childCount){
                    val tableRow = tableLayout.getChildAt(rowIndex) as TableRow
                    val firstCell = tableRow.getChildAt(0) as TextView
                    if (rowIndex==0)
                        firstCell.setBackgroundColor(ContextCompat.getColor(this@LakeLevel, R.color.dark_dark_grey))
                    else
                        firstCell.setBackgroundColor(ContextCompat.getColor(this@LakeLevel, R.color.grey))
                    firstCell.text = firstCell.text.toString().replace(" ","\n")
                    firstCell.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                }
                updatedTextView.text = updatedDate
                findViewById<HorizontalScrollView>(R.id.lakeLevelContent).visibility = View.VISIBLE
            }
        }
        catch(e:java.nio.channels.UnresolvedAddressException){
            withContext(Dispatchers.Main){
                updatedTextView.text = getString(R.string.error_internet_failure)
            }
        }
        catch(e:Exception){
            withContext(Dispatchers.Main){
                println("Exception !@!@!")
                println(e.message)
                updatedTextView.text = getString(R.string.error_unable_to_retrieve)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lake_level)

        updatedTextView = findViewById(R.id.updatedTextView)
        lakeLevelContent = findViewById(R.id.lakeLevelContent)

        lifecycleScope.launch {
           retrieveData()
        }

        val lakeLevelRefresh = findViewById<SwipeRefreshLayout>(R.id.lakeLevelRefresh)
        lakeLevelRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                retrieveData()
            }
            lakeLevelRefresh.isRefreshing = false
        }

    }
}