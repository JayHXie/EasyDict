package com.jayhxie.easydict.ui.fragment.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch
import com.jayhxie.easydict.R
import com.jayhxie.easydict.adapter.popupmenu.PopupMenuAdapter
import com.jayhxie.easydict.adapter.popupmenu.PopupMenuItem
import com.jayhxie.easydict.databinding.FragmentSettingsMainBinding
import com.jayhxie.easydict.ui.activity.settings.SETTINGS_ABOUT
import com.jayhxie.easydict.ui.activity.settings.SETTINGS_DICT_CARD
import com.jayhxie.easydict.ui.activity.settings.SETTINGS_WELCOME_SCREEN
import com.jayhxie.easydict.ui.activity.settings.SettingsActivity
import com.jayhxie.easydict.ui.activity.settings.SettingsViewModel
import com.jayhxie.easydict.ui.base.BaseFragment
import com.jayhxie.easydict.ui.view.CustomPopWindow
import com.jayhxie.easydict.utils.common.ActivityUtils
import com.jayhxie.easydict.utils.common.FileUtils
import com.jayhxie.easydict.utils.common.ShareUtils
import com.jayhxie.easydict.utils.common.SizeUtils
import com.jayhxie.easydict.utils.common.ThemeUtils
import com.jayhxie.easydict.utils.constant.ShareConstant.DEF_VOICE
import com.jayhxie.easydict.utils.constant.ShareConstant.IS_USE_QUICK_SEARCH
import com.jayhxie.easydict.utils.constant.ShareConstant.IS_USE_SYSTEM_THEME
import com.jayhxie.easydict.utils.constant.ShareConstant.IS_USE_WEB_VIEW
import com.jayhxie.easydict.utils.constant.ShareConstant.MEI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsMainFragment : BaseFragment<FragmentSettingsMainBinding>(FragmentSettingsMainBinding::inflate) {
    // define variable
    private lateinit var settingsViewModel: SettingsViewModel
    private var changeSettingsTag = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            changeSettingsTag = true
            ShareUtils.putBoolean(context, IS_USE_QUICK_SEARCH, true)
        } else { showNotificationPermissionDialog() }
    }
    // define widget
    private lateinit var clAbout: ConstraintLayout
    private lateinit var clAppBrowser: ConstraintLayout
    private lateinit var clClearCache: ConstraintLayout
    private lateinit var clDarkMode: ConstraintLayout
    private lateinit var tvDarkModeDesc: TextView
    private lateinit var tvClearCacheDesc: TextView
    private lateinit var tvDefVoiceDesc: TextView
    private lateinit var tvThemeColorDesc: TextView
    private lateinit var clSystemThemeColor: ConstraintLayout
    private lateinit var clQuickSearch: ConstraintLayout
    private lateinit var clThemeColor: ConstraintLayout
    private lateinit var swAppBrowser: MaterialSwitch
    private lateinit var swQuickSearch: MaterialSwitch
    private lateinit var swSystemThemeColor: MaterialSwitch
    private lateinit var mtTitle: MaterialToolbar
    private lateinit var clClearHistory: ConstraintLayout
    private lateinit var clClearWordBook: ConstraintLayout
    private lateinit var clClearWordList: ConstraintLayout
    private lateinit var clDictCard: ConstraintLayout
    private lateinit var clWelcomeScreen: ConstraintLayout
    private lateinit var clDefVoice: ConstraintLayout

    override fun bindViews() {
        clAbout = binding.clAbout
        swQuickSearch = binding.swQuickSearch
        swSystemThemeColor = binding.swSystemThemeColor
        swAppBrowser = binding.swAppBrowser
        clAppBrowser = binding.clAppBrowser
        clClearCache = binding.clClearCache
        tvClearCacheDesc = binding.tvClearCacheDesc
        clDarkMode = binding.clDarkMode
        clQuickSearch = binding.clQuickSearch
        clSystemThemeColor = binding.clSystemThemeColor
        clThemeColor = binding.clThemeColor
        tvDarkModeDesc = binding.tvDarkModeDesc
        tvThemeColorDesc = binding.tvThemeColorDesc
        tvDefVoiceDesc = binding.tvDefVoiceDesc
        mtTitle = binding.mtTitle
        clClearHistory = binding.clClearHistory
        clClearWordBook = binding.clClearWordBook
        clClearWordList = binding.clClearWordList
        clDictCard = binding.clDictCard
        clWelcomeScreen = binding.clWelcomeScreen
        clDefVoice = binding.clDefVoice
    }

    override fun initViews() {
        settingsViewModel = (activity as SettingsActivity).getSettingsViewModel()
        swQuickSearch.isChecked = ShareUtils.getBoolean(mContext, IS_USE_QUICK_SEARCH, false)
        swAppBrowser.isChecked = ShareUtils.getBoolean(mContext, IS_USE_WEB_VIEW, true)
        swSystemThemeColor.isChecked = ShareUtils.getBoolean(mContext, IS_USE_SYSTEM_THEME, false)
        clSystemThemeColor.visibility = if (ThemeUtils.isDynamicColorAvailable()) View.VISIBLE else View.GONE
        clThemeColor.visibility = if (!swSystemThemeColor.isChecked || !ThemeUtils.isDynamicColorAvailable()) View.VISIBLE else View.GONE
        tvDarkModeDesc.text = settingsViewModel.darkModeMap[ThemeUtils.getDarkTheme(mContext)]
        tvThemeColorDesc.text = settingsViewModel.themeColorMap[ThemeUtils.getThemeColor(mContext)]
        tvDefVoiceDesc.text = settingsViewModel.defVoiceMap[ShareUtils.getString(mContext, DEF_VOICE, MEI)]

        settingsViewModel.selectPopupMenuList(settingsViewModel.darkModeList, settingsViewModel.darkModeMap[ThemeUtils.getDarkTheme(mContext)]!!)
        settingsViewModel.selectPopupMenuList(settingsViewModel.themeColorList, settingsViewModel.themeColorMap[ThemeUtils.getThemeColor(mContext)]!!)
        settingsViewModel.selectPopupMenuList(settingsViewModel.defVoiceList, settingsViewModel.defVoiceMap[ShareUtils.getString(mContext, DEF_VOICE, MEI)]!!)

        calculateClashSize()
    }

    override fun addListener() {
        mtTitle.setNavigationOnClickListener{
            mListener.onFragmentInteraction("onBackPressed")
        }
        clAbout.setOnClickListener {
            mListener.onFragmentInteraction("toDetailFragment", SETTINGS_ABOUT)
        }
        clDictCard.setOnClickListener {
            mListener.onFragmentInteraction("toDetailFragment", SETTINGS_DICT_CARD)
        }
        clWelcomeScreen.setOnClickListener {
            mListener.onFragmentInteraction("toDetailFragment", SETTINGS_WELCOME_SCREEN)
        }
        clClearCache.setOnClickListener {
            clearCache()
        }
        clClearHistory.setOnClickListener {
            mListener.onFragmentInteraction("clearHistory")
        }
        clClearWordBook.setOnClickListener {
            mListener.onFragmentInteraction("clearWordBook")
        }
        clClearWordList.setOnClickListener {
            mListener.onFragmentInteraction("clearWordList")
        }
        clDefVoice.setOnClickListener {
            showPopupMenu(clDefVoice, settingsViewModel.defVoiceList).also {
                it.second.setOnMenuItemClickListener(object : PopupMenuAdapter.OnMenuItemClickListener{
                    override fun onMenuItemClick(position: Int) {
                        ShareUtils.putString(mContext, DEF_VOICE, settingsViewModel.defVoiceMap.keys.toList()[position])
                        tvDefVoiceDesc.text = settingsViewModel.defVoiceMap[ShareUtils.getString(mContext, DEF_VOICE, MEI)]
                        it.first.dismiss()
                    }
                })
            }
        }
        clDarkMode.setOnClickListener {
            showPopupMenu(clDarkMode, settingsViewModel.darkModeList).also {
                it.second.setOnMenuItemClickListener(object : PopupMenuAdapter.OnMenuItemClickListener{
                    override fun onMenuItemClick(position: Int) {
                        ThemeUtils.setDarkTheme(mContext, settingsViewModel.darkModeMap.keys.toList()[position])
                        if(settingsViewModel.selectPopupMenuList(settingsViewModel.darkModeList, position)){
                            AppCompatDelegate.setDefaultNightMode(settingsViewModel.darkModeMap.keys.toList()[position])
                        }
                        tvDarkModeDesc.text = settingsViewModel.darkModeMap[ThemeUtils.getDarkTheme(mContext)]
                        it.first.dismiss()
                    }
                })
            }
        }
        clThemeColor.setOnClickListener {
            showPopupMenu(clThemeColor, settingsViewModel.themeColorList).also {
                it.second.setOnMenuItemClickListener(object : PopupMenuAdapter.OnMenuItemClickListener{
                    override fun onMenuItemClick(position: Int) {
                        ThemeUtils.setThemeColor(mContext, settingsViewModel.themeColorMap.keys.toList()[position])
                        if(settingsViewModel.selectPopupMenuList(settingsViewModel.themeColorList, position)){
                            ActivityUtils.restartAllActivities()
                        }
                        tvThemeColorDesc.text = settingsViewModel.themeColorMap[ThemeUtils.getThemeColor(mContext)]
                        it.first.dismiss()
                    }
                })
            }
        }
        clAppBrowser.setOnClickListener { swAppBrowser.isChecked = !swAppBrowser.isChecked }
        swAppBrowser.setOnCheckedChangeListener { _, isChecked -> ShareUtils.putBoolean(mContext, IS_USE_WEB_VIEW, isChecked) }
        clSystemThemeColor.setOnClickListener { swSystemThemeColor.isChecked = !swSystemThemeColor.isChecked }
        swSystemThemeColor.setOnCheckedChangeListener { _, isChecked ->
            ShareUtils.putBoolean(mContext, IS_USE_SYSTEM_THEME, isChecked)
            ActivityUtils.restartAllActivities()
        }
        clQuickSearch.setOnClickListener { swQuickSearch.isChecked = !swQuickSearch.isChecked }
        swQuickSearch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                if (!isNotificationsEnabled()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else { showNotificationPermissionDialog() }
                } else { ShareUtils.putBoolean(mContext, IS_USE_QUICK_SEARCH, true)
                    mListener.onFragmentInteraction("showQuickSearchNotification") }
            } else { ShareUtils.delShare(mContext, IS_USE_QUICK_SEARCH)
               mListener.onFragmentInteraction("cancelQuickSearchNotification") }
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

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            delay(400)
            withContext(Dispatchers.Main) {
                if ((changeSettingsTag || ShareUtils.getBoolean(mContext, IS_USE_QUICK_SEARCH, false))
                    && isNotificationsEnabled()) { swQuickSearch.isChecked = true
                    mListener.onFragmentInteraction("showQuickSearchNotification")
                } else {
                    swQuickSearch.isChecked = false
                    ShareUtils.delShare(mContext, IS_USE_QUICK_SEARCH)
                }
            }
        }
    }

    private fun showPopupMenu(view: View, popWindowList: List<PopupMenuItem>): Pair<CustomPopWindow, PopupMenuAdapter>{
        val parentView = requireActivity().findViewById<ViewGroup>(android.R.id.content)
        val contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_menu_recycleview, parentView, false)
        val popWindow = CustomPopWindow.PopupWindowBuilder(mContext)
            .setView(contentView)
            .create()
        val recycleView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = PopupMenuAdapter(popWindowList)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(mContext)
        adapter.selectItem(popWindowList.find { it.isMenuItemSelected }?.position ?: 0)
        adapter.setOnMenuItemClickListener(object : PopupMenuAdapter.OnMenuItemClickListener{
            override fun onMenuItemClick(position: Int) {
                popWindow.dismiss()
            }
        })
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        popWindow.popupWindow?.contentView?.measure(widthMeasureSpec, heightMeasureSpec)
        val xOff = view.right - (popWindow.popupWindow?.contentView?.measuredWidth ?: 0) - SizeUtils.dp2px(4f)
        val yOff = view.top - view.bottom
        popWindow.showAsDropDown(view, xOff, yOff)
        return Pair(popWindow, adapter)
    }

    private fun showNotificationPermissionDialog(){
        lifecycleScope.launch(Dispatchers.IO) {
            delay(400)
            withContext(Dispatchers.Main){
                swQuickSearch.isChecked = false
            }
        }
        val dialog = AlertDialog.Builder(mContext)
            .setTitle(mContext.getString(R.string.hint))
            .setMessage(getString(R.string.notify_permission_desc))
            .setPositiveButton(mContext.getString(R.string.confirm)){ _, _ ->
                changeSettingsTag = true
                openNotificationSettingsForApp(mContext)
            }
            .setNegativeButton(mContext.getString(R.string.cancel), null)
            .create()
        dialog.show()
    }
    // 打开通知设置页面
    private fun openNotificationSettingsForApp(context: Context) {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", context.packageName)
        intent.putExtra("app_uid", context.applicationInfo.uid)
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
        context.startActivity(intent)
    }
    //通知权限
    private fun isNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(mContext).areNotificationsEnabled()
    }


    private fun calculateClashSize(){
        lifecycleScope.launch {
            val clashSize = withContext(Dispatchers.IO) {
                async { FileUtils.getFileSizes(mContext.cacheDir) }.await()
            }
            withContext(Dispatchers.Main){
                tvClearCacheDesc.text = FileUtils.formatFileSize(clashSize)
            }
        }
    }
    private fun clearCache() {
        lifecycleScope.launch {
            val clearCacheOk = withContext(Dispatchers.IO) {
                async { FileUtils.clearFile(mContext.cacheDir) }.await()
            }
            if (clearCacheOk) {
                calculateClashSize()
                withContext(Dispatchers.Main) {
                    showShortToast(mContext.getString(R.string.clear_cache_success))
                }
            }
        }
    }
}