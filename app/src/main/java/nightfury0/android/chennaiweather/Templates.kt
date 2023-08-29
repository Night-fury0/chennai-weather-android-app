package nightfury0.android.chennaiweather

import android.content.Context
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat

class Templates {
    fun formTableCell(context: Context, rowView: TableRow, textValue: String, bgcolor: Int, isBold: Boolean, textAlign: Int){
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

}