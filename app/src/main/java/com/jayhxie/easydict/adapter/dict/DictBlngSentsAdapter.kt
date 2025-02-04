package com.jayhxie.easydict.adapter.dict

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jayhxie.easydict.R
import com.jayhxie.easydict.ui.activity.dict.SentencePair
import com.jayhxie.easydict.utils.common.ColorUtils
import com.jayhxie.easydict.utils.factory.fixTextSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("NotifyDataSetChanged")
class DictBlngSentsAdapter(private val mItemSentenceList: ArrayList<SentencePair>) :
    RecyclerView.Adapter<DictBlngSentsAdapter.ViewHolder>() {
    private var playButtonClickListener: OnPlayButtonClickListener? = null
    private var playPosition: Int = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dict_blng_sents, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemSentence: SentencePair = mItemSentenceList[position]
        val num = position + 1
        holder.textNum.text = num.toString()

        val tagedSentence = itemSentence.`taged-sentence`
        val tagedSentenceText = Html.fromHtml(tagedSentence, FROM_HTML_MODE_COMPACT)
        val tagedSentenceSpannable = SpannableString(tagedSentenceText)
        for (span in tagedSentenceText.getSpans(0, tagedSentenceText.length, Any::class.java)){
            if (span is StyleSpan && span.style == Typeface.BOLD){
                tagedSentenceSpannable.removeSpan(span)
                tagedSentenceSpannable.setSpan(ForegroundColorSpan(ColorUtils.colorPrimary(holder.itemView.context)),
                    tagedSentenceText.getSpanStart(span), tagedSentenceText.getSpanEnd(span), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        holder.textEn.text = tagedSentenceSpannable

        val tagedTranslation = itemSentence.`taged-translation`
        val tagedTranslationText = Html.fromHtml(tagedTranslation, FROM_HTML_MODE_COMPACT)
        val tagedTranslationSpannable = SpannableString(tagedTranslationText)
        for (span in tagedTranslationText.getSpans(0, tagedTranslationText.length, Any::class.java)){
            if (span is StyleSpan && span.style == Typeface.BOLD){
                tagedTranslationSpannable.removeSpan(span)
                tagedTranslationSpannable.setSpan(ForegroundColorSpan(ColorUtils.colorPrimary(holder.itemView.context)),
                    tagedTranslationText.getSpanStart(span), tagedTranslationText.getSpanEnd(span), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        holder.textCn.text = tagedTranslationSpannable

        val imageResId = if (position == playPosition) {
            R.drawable.ic_voice_on
        } else R.drawable.ic_voice
        holder.imgVoice.setImageResource(imageResId)
        holder.imgVoice.setOnClickListener {
            playPosition = holder.absoluteAdapterPosition
            holder.imgVoice.setImageResource(imageResId)
            playButtonClickListener?.onPlayButtonClick(position)
        }

        holder.textEn.fixTextSelection()
        holder.textCn.fixTextSelection()
    }

    override fun getItemCount(): Int {
        return mItemSentenceList.size
    }

    fun getItem(position: Int) = mItemSentenceList[position]


    fun setData(data: List<SentencePair>?) {
        CoroutineScope(Dispatchers.IO).launch {
            val newList = data?.toMutableList() ?: mutableListOf()
            withContext(Dispatchers.Main) {
                mItemSentenceList.clear()
                mItemSentenceList.addAll(newList)
                notifyDataSetChanged()
            }
        }
    }

    fun resetPlaySound() {
        playPosition = -1
        notifyDataSetChanged()
    }

    fun setPositionPlayingSound(position: Int) {
        playPosition = position
        notifyDataSetChanged()
    }

    interface OnPlayButtonClickListener {
        fun onPlayButtonClick(position: Int)
    }
    fun setOnPlayButtonClickListener(listener: OnPlayButtonClickListener) {
        playButtonClickListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textEn: TextView = itemView.findViewById(R.id.tv_item_sen_en)
        var textNum: TextView = itemView.findViewById(R.id.tv_item_num)
        var textCn: TextView = itemView.findViewById(R.id.tv_item_sen_cn)
        var imgVoice : ImageView = itemView.findViewById(R.id.iv_item_sen_voice)
    }

}

