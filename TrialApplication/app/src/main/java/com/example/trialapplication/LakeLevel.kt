package com.example.trialapplication

import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


class LakeLevel : AppCompatActivity() {

    suspend fun retrieveData(){

        val table: String
        val updatedDate: String
        try {
            withContext(Dispatchers.IO) {
                val url = "https://cmwssb.tn.gov.in/lake-level"
                val client = HttpClient(CIO)
                val response: HttpResponse = client.request(url) {
                    method = HttpMethod.Get
                }
                val doc: Document = Jsoup.parse(response.bodyAsText());
                val tableElement: Element =
                    doc.getElementsByClass("lack-view table table-responsive table-striped table-bordered")[0];
                tableElement.attr("border", "2")
                table = tableElement.outerHtml();
                updatedDate =
                    doc.getElementsByAttributeValue("style", "font-size: 18px;")[0].text();
            }
            withContext(Dispatchers.Main) {
                val webView = findViewById<WebView>(R.id.webView)
                val updatedTextView = findViewById<TextView>(R.id.updatedTextView)
                webView.loadData(table, "text/html; charset=utf-8", "UTF-8")
                updatedTextView.text = updatedDate
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
        val webView = findViewById<WebView>(R.id.webView)
    }
}