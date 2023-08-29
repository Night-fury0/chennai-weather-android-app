package nightfury0.android.chennaiweather

import android.content.Context
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.jsoup.select.Elements

class Templates {
    fun formTableCell(context: Context, rowView: TableRow, textValue: String, bgcolor: Int, isBold: Boolean, textAlign:Int){
        val textView = TextView(context)
        textView.textSize = 15.toFloat()
        textView.setBackgroundColor(ContextCompat.getColor(context, bgcolor))
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        layoutParams.weight = 1.0f
        layoutParams.marginStart = 2
        layoutParams.marginEnd = 2
        layoutParams.bottomMargin = 2
        layoutParams.topMargin = 2
        textView.layoutParams = layoutParams
        textView.textAlignment = textAlign
        textView.gravity = android.view.Gravity.CENTER
        if (isBold) textView.typeface = android.graphics.Typeface.DEFAULT_BOLD
        textView.setPadding(5,5,5,5)
        textView.text = textValue
        rowView.addView(textView)
    }

    fun formTableFromHtml(context: Context, tableLayout: TableLayout, headerValues: Elements, values:Elements){
        tableLayout.removeAllViews()
        val noOfRows = (values.size/headerValues.size).toInt()
        val tableHeaderRow = TableRow(context)
        tableHeaderRow.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        val rowLayoutParams = TableLayout.LayoutParams()
        rowLayoutParams.weight = 1.0f
        tableHeaderRow.layoutParams = rowLayoutParams
        for (i in 0 until headerValues.size){
            Templates().formTableCell(
                context = context,
                rowView = tableHeaderRow,
                textValue = headerValues[i].text().replace(" ","\n"),
                bgcolor = R.color.dark_dark_grey,
                textAlign = View.TEXT_ALIGNMENT_CENTER,
                isBold = true
            )
        }
        tableLayout.addView(tableHeaderRow)
        var k = 0
        for (i in 0 until noOfRows){
            val tableRow = TableRow(context)
            tableHeaderRow.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            tableRow.layoutParams = rowLayoutParams
            for (j in 0 until headerValues.size){
                Templates().formTableCell(
                    context = context,
                    rowView = tableRow,
                    textValue = values[k++].text(),
                    bgcolor = R.color.light_grey,
                    textAlign = View.TEXT_ALIGNMENT_CENTER,
                    isBold = false
                )
            }
            tableLayout.addView(tableRow)
        }
    }

    fun formTableForCurrentWeather(context: Context, tableLayout: TableLayout, values: List<String>, valuesPerRow:Int){
        var k = 0
        for (i in 0 until values.size/valuesPerRow) {
            val tableRow = TableRow(context)
            tableRow.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            val rowLayoutParams = TableLayout.LayoutParams()
            rowLayoutParams.weight = 1.0f
            tableRow.layoutParams = rowLayoutParams

            for (j in 0 until valuesPerRow) {
                Templates().formTableCell(
                    context = context,
                    rowView = tableRow,
                    textValue = values[k++],
                    bgcolor = if (j == 0) R.color.grey else R.color.light_grey,
                    textAlign = if (j == 0) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_CENTER,
                    isBold = false
                )
            }
            for (l in 0 until valuesPerRow){
                val textView = tableRow.getChildAt(l) as TextView
                textView.setPadding(5,0,5,0)
            }
            tableLayout.addView(tableRow)
        }
    }
    fun formTableFromList(context: Context, tableLayout: TableLayout, headerValues: List<String>, values:List<String>){
        tableLayout.removeAllViews()
        val noOfRows = (values.size/headerValues.size).toInt()
        val tableHeaderRow = TableRow(context)
        tableHeaderRow.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        val rowLayoutParams = TableLayout.LayoutParams()
        rowLayoutParams.weight = 1.0f
        tableHeaderRow.layoutParams = rowLayoutParams
        for (i in 0 until headerValues.size){
            Templates().formTableCell(
                context = context,
                rowView = tableHeaderRow,
                textValue = headerValues[i].replace(" ","\n"),
                bgcolor = R.color.dark_dark_grey,
                textAlign = if (i==0) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_CENTER,
                isBold = true
            )
        }
        tableLayout.addView(tableHeaderRow)
        var k = 0
        for (i in 0 until noOfRows){
            val tableRow = TableRow(context)
            tableHeaderRow.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            tableRow.layoutParams = rowLayoutParams
            for (j in 0 until headerValues.size){
                Templates().formTableCell(
                    context = context,
                    rowView = tableRow,
                    textValue = values[k++],
                    bgcolor = if (j==0) R.color.grey else R.color.light_grey,
                    textAlign = if (j==0) View.TEXT_ALIGNMENT_TEXT_START else View.TEXT_ALIGNMENT_CENTER,
                    isBold = false
                )
            }
            tableLayout.addView(tableRow)
        }
    }

}