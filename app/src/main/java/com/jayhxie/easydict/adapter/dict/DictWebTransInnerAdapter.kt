package com.jayhxie.easydict.adapter.dict

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jayhxie.easydict.R
import com.jayhxie.easydict.ui.activity.dict.WebTrans
import com.jayhxie.easydict.utils.common.ColorUtils
import com.jayhxie.easydict.utils.factory.fixTextSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("NotifyDataSetChanged")
class DictWebTransInnerAdapter(private val mItemList: ArrayList<WebTrans>) :
    RecyclerView.Adapter<DictWebTransInnerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dict_web_trans_inner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: WebTrans = mItemList[position]
        val num = position + 1
        holder.textNum.text = num.toString()

        holder.textValue.apply {
            text = item.value
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            visibility = if (item.value.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        val l = mutableListOf<String>()
        item.summary?.line?.let {
            for (i in it.indices){
                l.add(it[i])
            }
        }
        l.let {
            if (it.isNotEmpty()){
                val lineSentenceText = Html.fromHtml("\r" + it.joinToString("\n\r"), Html.FROM_HTML_MODE_COMPACT)
                val lineSentenceSpannable = SpannableString(lineSentenceText)
                for (span in lineSentenceText.getSpans(0, lineSentenceText.length, Any::class.java)){
                    if (span is StyleSpan && span.style == Typeface.BOLD){
                        lineSentenceSpannable.removeSpan(span)
                        lineSentenceSpannable.setSpan(
                            ForegroundColorSpan(ColorUtils.colorPrimary(holder.itemView.context)),
                            lineSentenceText.getSpanStart(span), lineSentenceText.getSpanEnd(span), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                holder.textLine.text = lineSentenceSpannable
            } else {
                holder.textLine.visibility = View.GONE
            }
        }

        holder.textLine.fixTextSelection()
        holder.textValue.fixTextSelection()
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    fun setData(data: List<WebTrans>?) {
        CoroutineScope(Dispatchers.IO).launch {
            val newList = data?.toMutableList() ?: mutableListOf()
            withContext(Dispatchers.Main) {
                mItemList.clear()
                mItemList.addAll(newList)
                notifyDataSetChanged()
            }
        }
    }

    fun getItem(position: Int) = mItemList[position]


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNum: TextView = itemView.findViewById(R.id.tv_item_num)
        val textValue: TextView = itemView.findViewById(R.id.tv_item_value)
        val textLine: TextView = itemView.findViewById(R.id.tv_item_line)
    }

}

