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
    private lateinit var stationsTextView: TextView
    private suspend fun retrieveData(url: String){
        val values: Elements
        val header_values: Elements
        try {
            withContext(Dispatchers.Main){
                stationsTextView.text = resources.getString(R.string.loading_text)
                stationsTextView.visibility = View.VISIBLE
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
                    Templates().formTableCell(
                        context = this@Stations,
                        rowView = tableHeaderRow,
                        textValue = header_values[i].text().replace(" ","\n"),
                        bgcolor = R.color.grey,
                        textAlign = View.TEXT_ALIGNMENT_CENTER ,
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
                        Templates().formTableCell(
                            context = this@Stations,
                            rowView = tableRow,
                            textValue = values[k++].text().replace(" ", "\n"),
                            bgcolor = R.color.light_grey,
                            textAlign = View.TEXT_ALIGNMENT_CENTER,
                            isBold = false
                        )
                    }
                    tableLayout.addView(tableRow)
                }
                stationsTextView.visibility = View.INVISIBLE
                findViewById<ScrollView>(R.id.stationsContent).visibility = View.VISIBLE
            }
        }
        catch(e:java.nio.channels.UnresolvedAddressException){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_internet_failure)
                stationsTextView.text = failedMessage
            }
        }
        catch(e:Exception){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_unable_to_retrieve)
                println("Exception !@!@!")
                println(e.message)
                stationsTextView.text = failedMessage
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stations)

        stationsTextView = findViewById(R.id.stationsTextView)
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone(resources.getString(R.string.stations_timezone)))
        val year = utcCalendar.get(Calendar.YEAR)
        val month = utcCalendar.get(Calendar.MONTH) + 1 // Month is 0-based
        val day = utcCalendar.get(Calendar.DAY_OF_MONTH)
        val formattedDate = String.format(resources.getString(R.string.stations_dateformat), year, month, day)

        val spinner1 = findViewById<Spinner>(R.id.stationsSpinner1)
        val spinner2 = findViewById<Spinner>(R.id.stationsSpinner2)
        val spinner3 = findViewById<Spinner>(R.id.stationsSpinner3)

        lifecycleScope.launch {
            try {
                val states_list = ArrayList<String>()
                val spinner1Url = resources.getString(R.string.stations_base_url) +
                        "/${resources.getString(R.string.stations_states)}" +
                        "?types=${resources.getString(R.string.stations_type)}"
                withContext(Dispatchers.IO) {
                    val response = HttpClient(CIO).request<HttpResponse>(spinner1Url) {
                        method = HttpMethod.Get
                    }
                    val jsonObject = JSONObject(response.readText())
                    val jsonArray = jsonObject.getJSONArray("data")
                    for (i in 0 until jsonArray.length()) {
                        states_list.add(jsonArray.getString(i))
                    }
                }
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@Stations,
                        android.R.layout.simple_spinner_item,
                        states_list
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner1.adapter = adapter
                    spinner1.setSelection(adapter.getPosition(resources.getString(R.string.default_state)))
                    spinner1.visibility = View.VISIBLE
                }
            }catch(e:java.nio.channels.UnresolvedAddressException){
                stationsTextView.text = resources.getString(R.string.error_internet_failure)
                stationsTextView.visibility = View.VISIBLE
            }
            catch(e:Exception){
                println("Exception !@!@! ${e.message}")
                stationsTextView.text = resources.getString(R.string.error_unable_to_retrieve)
                stationsTextView.visibility = View.VISIBLE
            }
        }

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                try{
                    val state = spinner1.adapter.getItem(position)
                        ?: resources.getString(R.string.default_state)
                    val spinner2Url = resources.getString(R.string.stations_base_url) +
                                "/${resources.getString(R.string.stations_districts)}" +
                                "?types=${resources.getString(R.string.stations_type)}" +
                                "&states=${state}"
                    val district_list = ArrayList<String>()
                    lifecycleScope.launch {
                        try {
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
                        }catch(e:java.nio.channels.UnresolvedAddressException){
                            stationsTextView.text = resources.getString(R.string.error_internet_failure)
                            stationsTextView.visibility = View.VISIBLE
                            spinner2.visibility = View.INVISIBLE
                            spinner3.visibility = View.INVISIBLE
                        }
                        catch(e:Exception){
                            println("Exception !@!@! ${e.message}")
                            stationsTextView.text = resources.getString(R.string.error_unable_to_retrieve)
                            stationsTextView.visibility = View.VISIBLE
                            spinner2.visibility = View.INVISIBLE
                            spinner3.visibility = View.INVISIBLE
                        }
                    }
                }catch(e:java.nio.channels.UnresolvedAddressException){
                    stationsTextView.text = resources.getString(R.string.error_internet_failure)
                    stationsTextView.visibility = View.VISIBLE
                    spinner2.visibility = View.INVISIBLE
                    spinner3.visibility = View.INVISIBLE
                }
                catch(e:Exception){
                    println("Exception !@!@! ${e.message}")
                    stationsTextView.text = resources.getString(R.string.error_unable_to_retrieve)
                    spinner2.visibility = View.INVISIBLE
                    spinner3.visibility = View.INVISIBLE
                    stationsTextView.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                try {
                    val state = spinner1.selectedItem
                    val district = spinner2.adapter.getItem(position)
                        ?: resources.getString(R.string.default_district)
                    val spinner3Url = resources.getString(R.string.stations_base_url) +
                                "/${resources.getString(R.string.stations_stations)}" +
                                "?types=${resources.getString(R.string.stations_type)}" +
                                "&states=${state}&disc=${district}"
                    val stations_list = ArrayList<String>()
                    lifecycleScope.launch {
                        try {
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
                        } catch (e: java.nio.channels.UnresolvedAddressException) {
                            stationsTextView.text =
                                resources.getString(R.string.error_internet_failure)
                            stationsTextView.visibility = View.VISIBLE
                            spinner3.visibility = View.INVISIBLE
                        } catch (e: Exception) {
                            println("Exception !@!@! ${e.message}")
                            stationsTextView.text =
                                resources.getString(R.string.error_unable_to_retrieve)
                            stationsTextView.visibility = View.VISIBLE
                            spinner3.visibility = View.INVISIBLE
                        }
                    }
                }catch (e: java.nio.channels.UnresolvedAddressException) {
                    stationsTextView.text =
                        resources.getString(R.string.error_internet_failure)
                    stationsTextView.visibility = View.VISIBLE
                    spinner3.visibility = View.INVISIBLE
                } catch (e: Exception) {
                    println("Exception !@!@! ${e.message}")
                    stationsTextView.text =
                        resources.getString(R.string.error_unable_to_retrieve)
                    stationsTextView.visibility = View.VISIBLE
                    spinner3.visibility = View.INVISIBLE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                try {
                    val state = spinner1.selectedItem
                    val district = spinner2.selectedItem
                    val station = spinner3.adapter.getItem(position)
                        ?: resources.getString(R.string.default_station)
                    val url = resources.getString(R.string.stations_base_url) +
                                "/${resources.getString(R.string.stations_dataview)}" +
                                "?a=${resources.getString(R.string.stations_type)}" +
                                "&b=${state}" +
                                "&c=${district}" +
                                "&d=${station}" +
                                "&e=${formattedDate}" +
                                "&f=${formattedDate}" +
                                "&g=ALL_HOUR" +
                                "&h=ALL_MINUTE"
                    lifecycleScope.launch() {
                        withContext(Dispatchers.IO) {
                            retrieveData(url)
                        }
                    }
                }catch(e:java.nio.channels.UnresolvedAddressException){
                    stationsTextView.text = resources.getString(R.string.error_internet_failure)
                    stationsTextView.visibility = View.VISIBLE
                }
                catch(e:Exception){
                    println("Exception !@!@! ${e.message}")
                    stationsTextView.text = resources.getString(R.string.error_unable_to_retrieve)
                    stationsTextView.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }
}