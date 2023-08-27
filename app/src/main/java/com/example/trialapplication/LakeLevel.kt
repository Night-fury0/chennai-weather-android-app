package com.example.trialapplication

import android.os.Bundle
import android.view.View
//import android.webkit.WebView
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
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

    private fun formTableCell(rowView: TableRow, text_value: String, bgcolor: Int, isBold: Boolean, text_align: Int){
        val textView = TextView(this@LakeLevel)
        textView.textSize = 15.toFloat()
        textView.setBackgroundColor(ContextCompat.getColor(this@LakeLevel, bgcolor))
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
    private suspend fun retrieveData(){
        val values: Elements
        val header_values: Elements
        val updatedDate: String
        try {
            withContext(Dispatchers.IO) {
                val url = getString(R.string.lake_level_url)
                val client = HttpClient(CIO)
                val response: HttpResponse = client.request(url) {
                    method = HttpMethod.Get
                }
                val doc: Document = Jsoup.parse(response.readText());
//                val doc: Document = Jsoup.parse(response.bodyAsText());
                val tableElement: Element =
                    doc.getElementsByClass(getString(R.string.lake_level_table_class_name))[0];
                values = tableElement.getElementsByTag("td")
                header_values = tableElement.getElementsByTag("th")
//                tableElement.attr("border", "2")
//                table = tableElement.outerHtml();
                updatedDate =
                    doc.getElementsByAttributeValue("style", "font-size: 18px;")[0].text();
            }
            withContext(Dispatchers.Main) {
                val tableLayout = findViewById<TableLayout>(R.id.lakeLevelTableLayout)
                tableLayout.removeAllViews()
                val no_of_rows = (values.size/header_values.size).toInt()
                val tableHeaderRow = TableRow(this@LakeLevel)
                tableHeaderRow.setBackgroundColor(ContextCompat.getColor(this@LakeLevel, R.color.black))
                val rowLayoutParams = TableLayout.LayoutParams()
                rowLayoutParams.weight = 1.0f
                tableHeaderRow.layoutParams = rowLayoutParams
                for (i in 0 until header_values.size){
                    formTableCell(
                        rowView = tableHeaderRow,
                        text_value = header_values[i].text().replace(" ","\n"),
                        bgcolor = R.color.dark_dark_grey,
                        text_align = if (i==0) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_CENTER ,
                        isBold = true
                    )
                }
                tableLayout.addView(tableHeaderRow)
                var k = 0
                for (i in 0 until no_of_rows){
                    val tableRow = TableRow(this@LakeLevel)
                    tableHeaderRow.setBackgroundColor(ContextCompat.getColor(this@LakeLevel, R.color.black))
                    val rowLayoutParams = TableLayout.LayoutParams()
                    rowLayoutParams.weight = 1.0f
                    tableRow.layoutParams = rowLayoutParams
                    for (j in 0 until header_values.size){
                        formTableCell(
                            rowView = tableRow,
                            text_value = values[k++].text().replace(" ", "\n"),
                            bgcolor = if (j==0) R.color.grey else R.color.light_grey,
                            text_align = if (j==0) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_CENTER,
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
        setContentView(R.layout.lake_level_table)
        lifecycleScope.launch {
           retrieveData()
        }
    }
}