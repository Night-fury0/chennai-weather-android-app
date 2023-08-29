package nightfury0.android.chennaiweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.Calendar
import java.util.TimeZone


class Rainfall : AppCompatActivity() {

    private lateinit var rainfallTextView: TextView
    private suspend fun retrieveData(url: String){
        val values: Elements
        val headerValues: Elements
        try {
            withContext(Dispatchers.Main){
                rainfallTextView.text = resources.getString(R.string.loading_text)
                rainfallTextView.visibility = View.VISIBLE
            }
            withContext(Dispatchers.IO) {
                val client = HttpClient(CIO)
                val response: HttpResponse = client.request(url) {
                    method = HttpMethod.Get
                }
                val doc: Document = Jsoup.parse(response.readText())
//                val doc: Document = Jsoup.parse(response.bodyAsText());
                val tableElement: Element = doc.getElementsByTag("table")[0]
                values = tableElement.getElementsByTag("td")
                headerValues = tableElement.getElementsByTag("th")
            }
            withContext(Dispatchers.Main) {
                val tableLayout = findViewById<TableLayout>(R.id.rainfallTableLayout)
                tableLayout.removeAllViews()
                val noOfRows = (values.size/headerValues.size)
                val tableHeaderRow = TableRow(this@Rainfall)
                tableHeaderRow.setBackgroundColor(ContextCompat.getColor(this@Rainfall, R.color.black))
                val rowLayoutParams = TableLayout.LayoutParams()
                rowLayoutParams.weight = 1.0f
                tableHeaderRow.layoutParams = rowLayoutParams
                for (i in 0 until headerValues.size){
                    Templates().formTableCell(
                        context = this@Rainfall,
                        rowView = tableHeaderRow,
                        textValue = headerValues[i].text().replace(" ","\n"),
                        bgcolor = R.color.grey,
                        textAlign = View.TEXT_ALIGNMENT_CENTER,
                        isBold = true
                    )
                }
                tableLayout.addView(tableHeaderRow)
                var k = 0
                for (i in 0 until noOfRows){
                    val tableRow = TableRow(this@Rainfall)
                    tableHeaderRow.setBackgroundColor(ContextCompat.getColor(this@Rainfall, R.color.black))
                    tableRow.layoutParams = rowLayoutParams
                    for (j in 0 until headerValues.size){
                        Templates().formTableCell(
                            context = this@Rainfall,
                            rowView = tableRow,
                            textValue = values[k++].text().replace(" ", "\n"),
                            bgcolor = R.color.light_grey,
                            textAlign = View.TEXT_ALIGNMENT_CENTER,
                            isBold = false
                        )
                    }
                    tableLayout.addView(tableRow)
                }
                rainfallTextView.visibility = View.INVISIBLE
                findViewById<ScrollView>(R.id.rainfallContent).visibility = View.VISIBLE
            }
        }
        catch(e:java.nio.channels.UnresolvedAddressException){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_internet_failure)
                rainfallTextView.text = failedMessage
            }
        }
        catch(e:Exception){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_unable_to_retrieve)
                println("Exception !@!@!")
                println(e.message)
                rainfallTextView.text = failedMessage
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rainfall)
        rainfallTextView = findViewById(R.id.rainfallTextView)

        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone(resources.getString(R.string.stations_timezone)))
        val year = utcCalendar.get(Calendar.YEAR)
        val month = utcCalendar.get(Calendar.MONTH) + 1 // Month is 0-based
        val day = utcCalendar.get(Calendar.DAY_OF_MONTH)
        val formattedDate = String.format(resources.getString(R.string.stations_dateformat), year, month, day)

        val spinner = findViewById<Spinner>(R.id.stateSpinner)

        lifecycleScope.launch {
            try {
                val statesList = ArrayList<String>()
                val statesUrl = resources.getString(R.string.stations_base_url) +
                        "/${resources.getString(R.string.stations_states)}" +
                        "?types=${resources.getString(R.string.rainfall_stations_type)}"
                withContext(Dispatchers.IO) {
                    val response = HttpClient(CIO).request<HttpResponse>(statesUrl) {
                        method = HttpMethod.Get
                    }
                    val jsonObject = JSONObject(response.readText())
                    val jsonArray = jsonObject.getJSONArray("data")
                    for (i in 0 until jsonArray.length()) {
                        statesList.add(jsonArray.getString(i))
                    }
                }
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@Rainfall,
                        android.R.layout.simple_spinner_item,
                        statesList
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                    spinner.setSelection(adapter.getPosition(resources.getString(R.string.default_state)))
                    spinner.visibility = View.VISIBLE
                }
            }catch(e:java.nio.channels.UnresolvedAddressException){
                rainfallTextView.text = resources.getString(R.string.error_internet_failure)
                rainfallTextView.visibility = View.VISIBLE
            }
            catch(e:Exception){
                println("Exception !@!@! ${e.message}")
                rainfallTextView.text = resources.getString(R.string.error_unable_to_retrieve)
                rainfallTextView.visibility = View.VISIBLE
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val state = spinner.adapter.getItem(position)
                    ?: resources.getString(R.string.default_state)
                val url =
                    resources.getString(R.string.stations_base_url) +
                            "/${resources.getString(R.string.rainfall_dataview)}" +
                            "?a=${resources.getString(R.string.rainfall_stations_type)}" +
                            "&b=${state}" +
                            "&c=${formattedDate}"
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        retrieveData(url)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }
}