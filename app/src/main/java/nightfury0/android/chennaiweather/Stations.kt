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

class Stations : AppCompatActivity() {

    private fun formTableCell(rowView: TableRow, text_value: String, bgcolor: Int, isBold: Boolean, text_align: Int){
        val textView = TextView(this@Stations)
        textView.textSize = 15.toFloat()
        textView.setBackgroundColor(ContextCompat.getColor(this@Stations, bgcolor))
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        layoutParams.weight = 1.0f
        layoutParams.marginStart = 1
        layoutParams.marginEnd = 1
        layoutParams.bottomMargin = 1
        layoutParams.topMargin = 1
        layoutParams.weight = 1.0f
        textView.layoutParams = layoutParams
        textView.textAlignment = text_align
        textView.gravity = android.view.Gravity.CENTER
        if (isBold) textView.typeface = android.graphics.Typeface.DEFAULT_BOLD
        textView.setPadding(5,5,5,5)
        textView.text = text_value
        rowView.addView(textView)
    }
    private suspend fun retrieveData(url: String){
        val values: Elements
        val header_values: Elements
        try {
            withContext(Dispatchers.Main){
                findViewById<TextView>(R.id.stationsTextView).text = resources.getString(R.string.loading_text)
                findViewById<TextView>(R.id.stationsTextView).visibility = View.VISIBLE
            }
            withContext(Dispatchers.IO) {
                val client = HttpClient(CIO)
                val response: HttpResponse = client.request(url) {
                    method = HttpMethod.Get
                }
                val doc: Document = Jsoup.parse(response.readText());
//                val doc: Document = Jsoup.parse(response.bodyAsText());
                val tableElement: Element = doc.getElementsByTag("table")[0];
                values = tableElement.getElementsByTag("td")
                header_values = tableElement.getElementsByTag("th")
            }
            withContext(Dispatchers.Main) {
                val tableLayout = findViewById<TableLayout>(R.id.rainfallTableLayout)
                tableLayout.removeAllViews()
                val no_of_rows = (values.size/header_values.size).toInt()
                val tableHeaderRow = TableRow(this@Stations)
                tableHeaderRow.setBackgroundColor(ContextCompat.getColor(this@Stations, R.color.black))
                val rowLayoutParams = TableLayout.LayoutParams()
                rowLayoutParams.weight = 1.0f
                tableHeaderRow.layoutParams = rowLayoutParams
                for (i in 0 until header_values.size){
                    formTableCell(
                        rowView = tableHeaderRow,
                        text_value = header_values[i].text().replace(" ","\n"),
                        bgcolor = R.color.grey,
                        text_align = View.TEXT_ALIGNMENT_CENTER ,
                        isBold = true
                    )
                }
                tableLayout.addView(tableHeaderRow)
                var k = 0
                for (i in 0 until no_of_rows){
                    val tableRow = TableRow(this@Stations)
                    tableHeaderRow.setBackgroundColor(ContextCompat.getColor(this@Stations, R.color.black))
                    tableRow.layoutParams = rowLayoutParams
                    for (j in 0 until header_values.size){
                        formTableCell(
                            rowView = tableRow,
                            text_value = values[k++].text().replace(" ", "\n"),
                            bgcolor = R.color.light_grey,
                            text_align = View.TEXT_ALIGNMENT_CENTER,
                            isBold = false
                        )
                    }
                    tableLayout.addView(tableRow)
                }
                findViewById<TextView>(R.id.stationsTextView).visibility = View.INVISIBLE
                findViewById<ScrollView>(R.id.stationsContent).visibility = View.VISIBLE
            }
        }
        catch(e:java.nio.channels.UnresolvedAddressException){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_internet_failure)
                findViewById<TextView>(R.id.stationsTextView).text = failedMessage
            }
        }
        catch(e:Exception){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_unable_to_retrieve)
                println("Exception !@!@!")
                println(e.message)
                findViewById<TextView>(R.id.stationsTextView).text = failedMessage
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stations)

        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = utcCalendar.get(Calendar.YEAR)
        val month = utcCalendar.get(Calendar.MONTH) + 1 // Month is 0-based
        val day = utcCalendar.get(Calendar.DAY_OF_MONTH)
        val formattedDate = String.format("%04d-%02d-%02d", year, month, day)

        val spinner1 = findViewById<Spinner>(R.id.stationsSpinner1)
        val spinner2 = findViewById<Spinner>(R.id.stationsSpinner2)
        val spinner3 = findViewById<Spinner>(R.id.stationsSpinner3)

        lifecycleScope.launch {
            val states_list = ArrayList<String>()
            val spinner1Url = "http://aws.imd.gov.in:8091/AWS/sta.php?types=AWSAGRO"
            withContext(Dispatchers.IO) {
                val response = HttpClient(CIO).request<HttpResponse>(spinner1Url) {
                    method = HttpMethod.Get
                }
                val jsonObject = JSONObject(response.readText())
                val jsonArray = jsonObject.getJSONArray("data")
                for (i in 0 until jsonArray.length()){
                    states_list.add(jsonArray.getString(i))
                }
            }
            withContext(Dispatchers.Main){
                val adapter = ArrayAdapter(this@Stations, android.R.layout.simple_spinner_item, states_list)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner1.adapter = adapter
                spinner1.setSelection(adapter.getPosition(resources.getString(R.string.default_state)))
                spinner1.visibility = View.VISIBLE
            }
        }

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val state = spinner1.adapter.getItem(position)?:resources.getString(R.string.default_state)
                val spinner2Url = "http://aws.imd.gov.in:8091/AWS/dis.php?types=AWSAGRO&states=${state}"
                val district_list = ArrayList<String>()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val response = HttpClient(CIO).request<HttpResponse>(spinner2Url) {
                            method = HttpMethod.Get
                        }
                        val jsonObject = JSONObject(response.readText())
                        val jsonArray = jsonObject.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            district_list.add(jsonArray.getString(i))
                        }
                    }
                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(
                            this@Stations,
                            android.R.layout.simple_spinner_item,
                            district_list
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner2.adapter = adapter
                        spinner2.setSelection(adapter.getPosition(resources.getString(R.string.default_district)))
                        spinner2.visibility = View.VISIBLE
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val state = spinner1.selectedItem
                val district = spinner2.adapter.getItem(position)?:resources.getString(R.string.default_district)
                val spinner3Url = "http://aws.imd.gov.in:8091/AWS/stat.php?types=AWSAGRO&states=${state}&disc=${district}"
                val stations_list = ArrayList<String>()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val response = HttpClient(CIO).request<HttpResponse>(spinner3Url) {
                            method = HttpMethod.Get
                        }
                        val jsonObject = JSONObject(response.readText())
                        val jsonArray = jsonObject.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            stations_list.add(jsonArray.getString(i))
                        }
                    }
                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(
                            this@Stations,
                            android.R.layout.simple_spinner_item,
                            stations_list
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner3.adapter = adapter
                        spinner3.setSelection(adapter.getPosition(resources.getString(R.string.default_station)))
                        spinner3.visibility = View.VISIBLE
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val state = spinner1.selectedItem
                val district = spinner2.selectedItem
                val station = spinner3.adapter.getItem(position)?:resources.getString(R.string.default_station)
                val url = "http://aws.imd.gov.in:8091/AWS/dataview.php?a=AWSAGRO&b=${state}&c=${district}&d=${station}&e=${formattedDate}&f=${formattedDate}&g=ALL_HOUR&h=ALL_MINUTE"
                lifecycleScope.launch() {
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