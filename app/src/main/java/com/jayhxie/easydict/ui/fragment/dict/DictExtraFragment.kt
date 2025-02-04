package com.jayhxie.easydict.ui.fragment.dict

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.jayhxie.easydict.R
import com.jayhxie.easydict.adapter.dict.DictAuthSentsAdapter
import com.jayhxie.easydict.adapter.dict.DictBlngClassificationAdapter
import com.jayhxie.easydict.adapter.dict.DictBlngSentsAdapter
import com.jayhxie.easydict.adapter.dict.DictEtymAdapter
import com.jayhxie.easydict.adapter.dict.DictExternalDictTransAdapter
import com.jayhxie.easydict.adapter.dict.DictPhraseAdapter
import com.jayhxie.easydict.adapter.dict.DictRelWordAdapter
import com.jayhxie.easydict.adapter.dict.DictSpecialOuterAdapter
import com.jayhxie.easydict.adapter.dict.DictSynoAntoAdapter
import com.jayhxie.easydict.adapter.dict.DictThesurusOuterAdapter
import com.jayhxie.easydict.adapter.dict.DictWebTransOuterAdapter
import com.jayhxie.easydict.databinding.FragmentDictExtraBinding
import com.jayhxie.easydict.ui.activity.dict.ANTONYM_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.AUTH_SENTS_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.Antonym
import com.jayhxie.easydict.ui.activity.dict.BLNG_SENTS_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.DictActivity
import com.jayhxie.easydict.ui.activity.dict.DictViewModel
import com.jayhxie.easydict.ui.activity.dict.ETYM_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.EXTERNAL_DICT_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.EXTERNAL_TRANS_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.Etym
import com.jayhxie.easydict.ui.activity.dict.PHRASE_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.REL_WORD_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.RelInfo
import com.jayhxie.easydict.ui.activity.dict.SEARCH_LE_EN
import com.jayhxie.easydict.ui.activity.dict.SPECIAL_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.SYNONYM_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.Synonym
import com.jayhxie.easydict.ui.activity.dict.THESAURUS_PART_MORE
import com.jayhxie.easydict.ui.activity.dict.VOICE_AUTH_PART
import com.jayhxie.easydict.ui.activity.dict.VOICE_BLNG_PART
import com.jayhxie.easydict.ui.activity.dict.WEB_TRANS_PART_MORE
import com.jayhxie.easydict.ui.activity.web.WebActivity
import com.jayhxie.easydict.ui.base.BaseFragment
import com.jayhxie.easydict.ui.view.LoadFailedView
import com.jayhxie.easydict.ui.view.LoadingView
import com.jayhxie.easydict.utils.common.ClipboardUtils


class DictExtraFragment : BaseFragment<FragmentDictExtraBinding>(FragmentDictExtraBinding::inflate) {
    // define variable
    private lateinit var dictViewModel: DictViewModel
    private lateinit var rvBlngSentsPartClassificationAdapter: DictBlngClassificationAdapter
    private lateinit var rvDictBlngSentsAdapter: DictBlngSentsAdapter
    private lateinit var rvDictAuthSentsAdapter: DictAuthSentsAdapter
    private lateinit var rvDictThesurusOuterAdapter: DictThesurusOuterAdapter
    private lateinit var rvDictAntoAdapter: DictSynoAntoAdapter<Antonym>
    private lateinit var rvDictSynoAdapter: DictSynoAntoAdapter<Synonym>
    private lateinit var rvPhraseAdapter: DictPhraseAdapter
    private lateinit var rvRelWordAdapter: DictRelWordAdapter
    private lateinit var rvEtymAdapter: DictEtymAdapter
    private lateinit var rvDictSpecialOuterAdapter: DictSpecialOuterAdapter
    private lateinit var rvDictWebTransOuterAdapter: DictWebTransOuterAdapter
    private lateinit var rvExternalDictAdapter: DictExternalDictTransAdapter
    private lateinit var rvExternalTransAdapter: DictExternalDictTransAdapter
    // define widget
    private lateinit var mtTitle : MaterialToolbar
    private lateinit var rvRecycleView : RecyclerView
    private lateinit var rvBlngSentsPartClassification : RecyclerView
    private lateinit var llRelWordPartWord : LinearLayout
    private lateinit var tvRelWordPartInf : TextView
    private lateinit var lvLoading: LoadingView
    private lateinit var lvLoadFailed: LoadFailedView
    private lateinit var nsvContent: NestedScrollView

    override fun bindViews() {
        mtTitle = binding.mtTitle
        rvRecycleView = binding.rvRecycleView
        rvBlngSentsPartClassification = binding.rvBlngSentsPartClassification
        llRelWordPartWord = binding.llRelWordPartWord
        tvRelWordPartInf = binding.tvRelWordPartInf
        lvLoading = binding.lvLoading
        lvLoadFailed = binding.lvLoadFailed
        nsvContent = binding.nsvContent
    }

    override fun initViews() {
        dictViewModel = (activity as DictActivity).getDictViewModel()
        dictViewModel.extraFragmentStyle.observe(this){ initData(it) }
    }

    override fun addListener() {
        lvLoadFailed.setOnClickListener {
            initData(dictViewModel.extraFragmentStyle.value!!)
        }
        mtTitle.setNavigationOnClickListener{
            mListener.onFragmentInteraction("onBackPressed")
        }
    }

    private fun initData(extraStyle: String) {
        rvBlngSentsPartClassification.visibility = View.GONE
        llRelWordPartWord.visibility = View.GONE
        when (extraStyle) {
            AUTH_SENTS_PART_MORE -> {
                mtTitle.title = getString(R.string.authoritative_example)
                rvDictAuthSentsAdapter = DictAuthSentsAdapter(ArrayList())
                rvRecycleView.adapter = rvDictAuthSentsAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                rvDictAuthSentsAdapter.setOnSourceUrlClickListener(object : DictAuthSentsAdapter.OnSourceUrlClickListener{
                    override fun onSourceUrlClick(position: Int) {
                        WebActivity.start(
                            requireActivity(),
                            rvDictAuthSentsAdapter.getItem(position).url!!
                        )
                    }
                })
                dictViewModel.onlineSearchSent()
                dictViewModel.authSentenceResponse.observe(this){
                    rvDictAuthSentsAdapter.setData(it?.sent)
                }
                dictViewModel.dataMoreAuthLoadInfo.observe(this){
                    when(it) {
                        0 -> {
                            lvLoading.visibility = View.VISIBLE
                            lvLoadFailed.visibility = View.GONE
                        }
                        1 -> {
                            lvLoading.visibility = View.GONE
                            lvLoadFailed.visibility = View.GONE
                        }
                        2 -> {
                            lvLoading.visibility = View.GONE
                            lvLoadFailed.visibility = View.VISIBLE
                        }
                    }
                }
                dictViewModel.playPosition.observe(this) {
                    it[VOICE_AUTH_PART]?.let { it1 ->
                        if (it1 != -1) rvDictAuthSentsAdapter.setPositionPlayingSound(it1)
                        else rvDictAuthSentsAdapter.resetPlaySound()
                    }
                }
                rvDictAuthSentsAdapter.setOnPlayButtonClickListener(object : DictAuthSentsAdapter.OnPlayButtonClickListener{
                    override fun onPlayButtonClick(position: Int) {
                        val item = rvDictAuthSentsAdapter.getItem(position)
                        dictViewModel.startPlaySound(VOICE_AUTH_PART, position, item.speech!!, SEARCH_LE_EN)
                    }
                })
                lvLoadFailed.setOnClickListener { dictViewModel.onlineSearchSent() }
            }
            ANTONYM_PART_MORE -> {
                mtTitle.title = getString(R.string.antonym)
                rvDictAntoAdapter =  DictSynoAntoAdapter(ArrayList())
                rvRecycleView.adapter = rvDictAntoAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                rvDictAntoAdapter.setData(dictViewModel.antoResponse.value?.antos)
            }
            PHRASE_PART_MORE -> {
                mtTitle.title = getString(R.string.phrase)
                rvPhraseAdapter =  DictPhraseAdapter(ArrayList())
                rvRecycleView.adapter = rvPhraseAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                rvPhraseAdapter.setData(dictViewModel.phrsResponse.value?.phrs)
            }
            SYNONYM_PART_MORE -> {
                mtTitle.title = getString(R.string.synonym)
                rvDictSynoAdapter =  DictSynoAntoAdapter(ArrayList())
                rvRecycleView.adapter = rvDictSynoAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                rvDictSynoAdapter.setData(dictViewModel.synoResponse.value?.synos)

            }
            THESAURUS_PART_MORE -> {
                mtTitle.title = getString(R.string.thesaurus)
                rvDictThesurusOuterAdapter = DictThesurusOuterAdapter(ArrayList())
                rvRecycleView.adapter = rvDictThesurusOuterAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                rvDictThesurusOuterAdapter.setData(dictViewModel.thesaurusResponse.value?.thesauruses?.filter { it1 ->
                    (it1.thesaurus?.size ?: 0) > 0 })
            }
            BLNG_SENTS_PART_MORE -> {
                mtTitle.title = getString(R.string.bilingual_example)
                rvBlngSentsPartClassification.visibility = View.VISIBLE
                rvBlngSentsPartClassificationAdapter = DictBlngClassificationAdapter(ArrayList())
                rvBlngSentsPartClassification.adapter = rvBlngSentsPartClassificationAdapter
                rvBlngSentsPartClassification.layoutManager =
                LinearLayoutManager(requireActivity()).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
                rvDictBlngSentsAdapter = DictBlngSentsAdapter(ArrayList())
                rvRecycleView.adapter = rvDictBlngSentsAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                dictViewModel.onlineSearchBlng()
                dictViewModel.dataMoreBlngLoadInfo.observe(this){
                    when(it) {
                        0 -> {
                            lvLoading.visibility = View.VISIBLE
                            lvLoadFailed.visibility = View.GONE
                        }
                        1 -> {
                            lvLoading.visibility = View.GONE
                            lvLoadFailed.visibility = View.GONE
                        }
                        2 -> {
                            lvLoading.visibility = View.GONE
                            lvLoadFailed.visibility = View.VISIBLE
                        }
                    }
                }
                dictViewModel.playPosition.observe(this) {
                    it[VOICE_BLNG_PART]?.let { it1 ->
                        if (it1 != -1) rvDictBlngSentsAdapter.setPositionPlayingSound(it1)
                        else rvDictBlngSentsAdapter.resetPlaySound()
                    }
                }
                lvLoadFailed.setOnClickListener { dictViewModel.onlineSearchBlng() }
                dictViewModel.blngClassification.observe(this) {
                    if ((it?.size ?: 0) > 1) {
                        rvBlngSentsPartClassificationAdapter.setData(it)
                        rvBlngSentsPartClassification.visibility = View.VISIBLE
                    } else rvBlngSentsPartClassification.visibility = View.GONE
                }
                rvDictBlngSentsAdapter.setOnPlayButtonClickListener(object : DictBlngSentsAdapter.OnPlayButtonClickListener{
                    override fun onPlayButtonClick(position: Int) {
                        val item = rvDictBlngSentsAdapter.getItem(position)
                        dictViewModel.startPlaySound(VOICE_BLNG_PART, position, item.speech!!, SEARCH_LE_EN)
                    }
                })
                dictViewModel.blngSents.observe(this) {
                    rvDictBlngSentsAdapter.setData(it?.get(rvBlngSentsPartClassificationAdapter.getSelectedItemText()))
                }
                dictViewModel.blngSelectedItem.observe(this){
                    rvDictBlngSentsAdapter.setData(dictViewModel.blngSents.value?.get(it))
                }
                rvBlngSentsPartClassificationAdapter.setOnItemClickListener(object : DictBlngClassificationAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int) {
                        val item = rvBlngSentsPartClassificationAdapter.getItem(position).text
                        dictViewModel.setBlngClassification(item)
                        rvBlngSentsPartClassification.scrollToPosition(position)
                    }
                })
            }
            REL_WORD_PART_MORE -> {
                mtTitle.title = getString(R.string.rel_word)
                llRelWordPartWord.visibility = View.VISIBLE
                rvRelWordAdapter =  DictRelWordAdapter(ArrayList())
                rvRecycleView.adapter = rvRelWordAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                tvRelWordPartInf.text = dictViewModel.relWordResponse.value?.stem
                val rel = mutableListOf<RelInfo>()
                dictViewModel.relWordResponse.value?.rels?.forEach { it1 -> it1.rel?.let{ it2 -> rel.add(it2)} }
                rvRelWordAdapter.setData(rel)
            }
            ETYM_PART_MORE -> {
                mtTitle.title = getString(R.string.etym)
                rvEtymAdapter =  DictEtymAdapter(ArrayList())
                rvRecycleView.adapter = rvEtymAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                val etyms = mutableListOf<Etym>()
                dictViewModel.etymResponse.value?.etyms?.zh?.forEach { it1 -> etyms.add(it1) }
                dictViewModel.etymResponse.value?.etyms?.en?.forEach { it1 -> etyms.add(it1) }
                rvEtymAdapter.setData(etyms)
            }
            WEB_TRANS_PART_MORE -> {
                mtTitle.title = getString(R.string.web_trans)
                rvDictWebTransOuterAdapter =  DictWebTransOuterAdapter(ArrayList())
                rvRecycleView.adapter = rvDictWebTransOuterAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                rvDictWebTransOuterAdapter.setData(dictViewModel.webTransResponse.value?.`web-translation`)
            }
            SPECIAL_PART_MORE -> {
                mtTitle.title = getString(R.string.special_trans)
                rvDictSpecialOuterAdapter =  DictSpecialOuterAdapter(ArrayList())
                rvRecycleView.adapter = rvDictSpecialOuterAdapter
                rvRecycleView.layoutManager = LinearLayoutManager(requireActivity())
                rvDictSpecialOuterAdapter.setData(dictViewModel.specialResponse.value?.entries)
            }
            EXTERNAL_DICT_PART_MORE -> {
                mtTitle.title = getString(R.string.dict)
                rvExternalDictAdapter = DictExternalDictTransAdapter(ArrayList())
                rvRecycleView.adapter = rvExternalDictAdapter
                rvRecycleView.layoutManager = FlexboxLayoutManager(requireActivity())
                rvExternalDictAdapter.setData(dictViewModel.dictExternalDict.value)
            }
            EXTERNAL_TRANS_PART_MORE -> {
                mtTitle.title = getString(R.string.translation)
                rvExternalTransAdapter = DictExternalDictTransAdapter(ArrayList())
                rvRecycleView.adapter = rvExternalTransAdapter
                rvRecycleView.layoutManager = FlexboxLayoutManager(requireActivity())
                rvExternalTransAdapter.setData(dictViewModel.dictExternalTrans.value)
                rvExternalTransAdapter.setOnItemClickListener(object : DictExternalDictTransAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        ClipboardUtils.copyTextToClipboard(mContext, dictViewModel.searchText.value?.searchText, getString(R.string.copied_to_trans))
                    }
                })
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) { //表示是一个进入动作，比如add.show等
            return if (enter) { //普通的进入的动作
                AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_in_right)
            } else { //比如一个已经Fragment被另一个replace，是一个进入动作，被replace的那个就是false
                AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_left)
            }
        } else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) { //表示一个退出动作，比如出栈，hide，detach等
            return if (enter) { //之前被replace的重新进入到界面或者Fragment回到栈顶
                AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_in_left)
            } else { //Fragment退出，出栈
                AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_right)
            }
        }
        return null
    }

}