package nightfury0.android.chennaiweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    private lateinit var stationsContent: NestedScrollView
    private suspend fun retrieveData(url: String){
        val values: Elements
        val headerValues: Elements
        try {
            withContext(Dispatchers.Main){
                stationsContent.visibility = View.INVISIBLE
                stationsTextView.text = resources.getString(R.string.loading_text)
                stationsTextView.visibility = View.VISIBLE
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
                val tableLayout = findViewById<TableLayout>(R.id.stationsTableLayout)
                Templates().formTableFromHtml(this@Stations, tableLayout, headerValues, values)
                stationsTextView.visibility = View.INVISIBLE
                stationsContent.visibility = View.VISIBLE
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

    private suspend fun spinnerTypePopulation(spinnerType: Spinner){
        try {
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@Stations,
                    android.R.layout.simple_spinner_item,
                    resources.getStringArray(R.array.stations_type)
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerType.adapter = adapter
                spinnerType.setSelection(adapter.getPosition(resources.getString(R.string.stations_type_default)))
                spinnerType.visibility = View.VISIBLE
            }
        }catch(e:Exception){
            println("Exception !@!@! ${e.message}")
            stationsTextView.text = resources.getString(R.string.error_unable_to_retrieve)
            stationsTextView.visibility = View.VISIBLE
        }
    }

    private fun spinnerTypeOnSelect(spinnerType:Spinner, spinner1:Spinner, spinner2:Spinner, spinner3:Spinner, position:Int){
        try{
            val type = spinnerType.adapter.getItem(position)
                ?: resources.getString(R.string.default_state)
            val spinner1Url = resources.getString(R.string.stations_base_url) +
                    "/${resources.getString(R.string.stations_states)}" +
                    "?types=${type}"
            val statesList = ArrayList<String>()
            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val response = HttpClient(CIO).request<HttpResponse>(spinner1Url) {
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
                            this@Stations,
                            android.R.layout.simple_spinner_item,
                            statesList
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner1.adapter = adapter
                        spinner1.setSelection(adapter.getPosition(resources.getString(R.string.default_state)))
                        spinner1.visibility = View.VISIBLE
                    }
                }catch(e:java.nio.channels.UnresolvedAddressException){
                    stationsTextView.text = resources.getString(R.string.error_internet_failure)
                    stationsTextView.visibility = View.VISIBLE
                    spinner1.visibility = View.INVISIBLE
                    spinner2.visibility = View.INVISIBLE
                    spinner3.visibility = View.INVISIBLE
                }
                catch(e:Exception){
                    println("Exception !@!@! ${e.message}")
                    stationsTextView.text = resources.getString(R.string.error_unable_to_retrieve)
                    stationsTextView.visibility = View.VISIBLE
                    spinner1.visibility = View.INVISIBLE
                    spinner2.visibility = View.INVISIBLE
                    spinner3.visibility = View.INVISIBLE
                }
            }
        }catch(e:java.nio.channels.UnresolvedAddressException){
            stationsTextView.text = resources.getString(R.string.error_internet_failure)
            stationsTextView.visibility = View.VISIBLE
            spinner1.visibility = View.INVISIBLE
            spinner2.visibility = View.INVISIBLE
            spinner3.visibility = View.INVISIBLE
        }
        catch(e:Exception){
            println("Exception !@!@! ${e.message}")
            stationsTextView.text = resources.getString(R.string.error_unable_to_retrieve)
            stationsTextView.visibility = View.VISIBLE
            spinner1.visibility = View.INVISIBLE
            spinner2.visibility = View.INVISIBLE
            spinner3.visibility = View.INVISIBLE
        }
    }

    private fun spinner1OnSelect(spinnerType: Spinner, spinner1:Spinner, spinner2:Spinner, spinner3:Spinner, position:Int){
        try{
            val type = spinnerType.selectedItem
            val state = spinner1.adapter.getItem(position)
                ?: resources.getString(R.string.default_state)
            val spinner2Url = resources.getString(R.string.stations_base_url) +
                    "/${resources.getString(R.string.stations_districts)}" +
                    "?types=${type}" +
                    "&states=${state}"
            val districtsList = ArrayList<String>()
            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val response = HttpClient(CIO).request<HttpResponse>(spinner2Url) {
                            method = HttpMethod.Get
                        }
                        val jsonObject = JSONObject(response.readText())
                        val jsonArray = jsonObject.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            districtsList.add(jsonArray.getString(i))
                        }
                    }
                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(
                            this@Stations,
                            android.R.layout.simple_spinner_item,
                            districtsList
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

    private fun spinner2OnSelect(spinnerType: Spinner, spinner1:Spinner, spinner2:Spinner, spinner3:Spinner, position:Int){
        try {
            val type = spinnerType.selectedItem
            val state = spinner1.selectedItem
            val district = spinner2.adapter.getItem(position)
                ?: resources.getString(R.string.default_district)
            val spinner3Url = resources.getString(R.string.stations_base_url) +
                    "/${resources.getString(R.string.stations_stations)}" +
                    "?types=${type}" +
                    "&states=${state}" +
                    "&disc=${district}"
            val stationsList = ArrayList<String>()
            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val response = HttpClient(CIO).request<HttpResponse>(spinner3Url) {
                            method = HttpMethod.Get
                        }
                        val jsonObject = JSONObject(response.readText())
                        val jsonArray = jsonObject.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            stationsList.add(jsonArray.getString(i))
                        }
                    }
                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(
                            this@Stations,
                            android.R.layout.simple_spinner_item,
                            stationsList
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

    private fun spinner3OnSelect(spinnerType:Spinner, spinner1:Spinner, spinner2:Spinner, spinner3: Spinner, position:Int, formattedDate:String){
        try {
            val type = spinnerType.selectedItem
            val state = spinner1.selectedItem
            val district = spinner2.selectedItem
            val station = spinner3.adapter.getItem(position)
                ?: resources.getString(R.string.default_station)
            val url = resources.getString(R.string.stations_base_url) +
                    "/${resources.getString(R.string.stations_dataview)}" +
                    "?a=${type}" +
                    "&b=${state}" +
                    "&c=${district}" +
                    "&d=${station}" +
                    "&e=${formattedDate}" +
                    "&f=${formattedDate}" +
                    "&g=ALL_HOUR" +
                    "&h=ALL_MINUTE"
            lifecycleScope.launch{
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stations)

        stationsTextView = findViewById(R.id.stationsTextView)
        stationsContent = findViewById(R.id.stationsContent)
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone(resources.getString(R.string.stations_timezone)))
        val year = utcCalendar.get(Calendar.YEAR)
        val month = utcCalendar.get(Calendar.MONTH) + 1 // Month is 0-based
        val day = utcCalendar.get(Calendar.DAY_OF_MONTH)
        val formattedDate = String.format(resources.getString(R.string.stations_dateformat), year, month, day)

        val spinnerTypes = findViewById<Spinner>(R.id.stationsSpinnerType)
        val spinnerStates = findViewById<Spinner>(R.id.stationsSpinnerStates)
        val spinnerDistricts = findViewById<Spinner>(R.id.stationsSpinnerDistricts)
        val spinnerStations = findViewById<Spinner>(R.id.stationsSpinnerStations)

        val adapter = ArrayAdapter(
            this@Stations,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.stations_type)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTypes.adapter = adapter
        spinnerTypes.setSelection(adapter.getPosition(resources.getString(R.string.stations_type_default)))
        spinnerTypes.visibility = View.VISIBLE

        lifecycleScope.launch {
            spinnerTypePopulation(spinnerTypes)
        }

        spinnerTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerTypeOnSelect(spinnerTypes, spinnerStates, spinnerDistricts, spinnerStations, position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spinnerStates.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinner1OnSelect(spinnerTypes, spinnerStates, spinnerDistricts, spinnerStations, position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spinnerDistricts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinner2OnSelect(spinnerTypes, spinnerStates, spinnerDistricts, spinnerStations, position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spinnerStations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinner3OnSelect(spinnerTypes, spinnerStates,spinnerDistricts,spinnerStations,position, formattedDate)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val stationsRefresh = findViewById<SwipeRefreshLayout>(R.id.stationsRefresh)
        stationsRefresh.setOnRefreshListener {
            if (spinnerStations.visibility == View.VISIBLE){
                spinner3OnSelect(spinnerTypes, spinnerStates,spinnerDistricts,spinnerStations,spinnerStations.selectedItemPosition,formattedDate)
            }
            else if (spinnerDistricts.visibility == View.VISIBLE){
                spinner2OnSelect(spinnerTypes, spinnerStates, spinnerDistricts, spinnerStations, spinnerDistricts.selectedItemPosition)
            }
            else if (spinnerStates.visibility == View.VISIBLE){
                spinner1OnSelect(spinnerTypes, spinnerStates, spinnerDistricts, spinnerStations, spinnerStates.selectedItemPosition)
            }
            else if (spinnerTypes.visibility == View.VISIBLE){
                spinnerTypeOnSelect(spinnerTypes, spinnerStates, spinnerDistricts, spinnerStations, spinnerTypes.selectedItemPosition)
            }
            else{
                lifecycleScope.launch {
                    spinnerTypePopulation(spinnerTypes)
                }
            }
            stationsRefresh.isRefreshing = false
        }

    }
}