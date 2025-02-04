package com.jayhxie.easydict.ui.fragment.dict

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jayhxie.easydict.adapter.dict.DictCollinsOuterAdapter
import com.jayhxie.easydict.databinding.FragmentDictDetailCoBinding
import com.jayhxie.easydict.ui.activity.dict.DictActivity
import com.jayhxie.easydict.ui.activity.dict.DictViewModel
import com.jayhxie.easydict.ui.base.BaseFragment


class DictDetailCOFragment : BaseFragment<FragmentDictDetailCoBinding>(FragmentDictDetailCoBinding::inflate) {
    // define variable
    private lateinit var dictViewModel: DictViewModel
    private lateinit var rvDictCollinsOuterAdapter: DictCollinsOuterAdapter

    // define widget
    private lateinit var rvCollins: RecyclerView

    override fun bindViews() {
        rvCollins = binding.rvCollins
    }

    override fun initViews() {
        dictViewModel = (activity as DictActivity).getDictViewModel()

        rvDictCollinsOuterAdapter = DictCollinsOuterAdapter(ArrayList())
        rvCollins.adapter = rvDictCollinsOuterAdapter
        rvCollins.layoutManager = LinearLayoutManager(requireActivity())
        dictViewModel.collinsResponse.observe(this){
            if ((it?.collins_entries?.size ?: 0) > 0){
                rvDictCollinsOuterAdapter.setData(it?.collins_entries)
            }
        }
    }

}