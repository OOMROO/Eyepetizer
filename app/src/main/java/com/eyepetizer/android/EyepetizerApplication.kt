/*
 * Copyright (c) 2020. vipyinzhiwei <vipyinzhiwei@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eyepetizer.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.work.WorkManager
import com.eyepetizer.android.extension.preCreateSession
import com.eyepetizer.android.ui.SplashActivity
import com.eyepetizer.android.ui.common.ui.WebViewActivity
import com.eyepetizer.android.ui.common.view.NoStatusFooter
import com.eyepetizer.android.util.DialogAppraiseTipsWorker
import com.eyepetizer.android.util.GlobalUtil
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.umeng.commonsdk.UMConfigure
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * Eyepetizer自定义Application，在这里进行全局的初始化操作。
 *
 * @author vipyinzhiwei
 * @since  2020/4/28
 */
class EyepetizerApplication : Application() {

    /**
     * des : init 关键字 初始化块 （initializer block）  顺序执行
     * 主构造的参数可以在 init 中使用
     *
    */
    init {
        SmartRefreshLayout.setDefaultRefreshInitializer { context, layout ->
            layout.setEnableLoadMore(true)
            layout.setEnableLoadMoreWhenContentNotFull(true)
        }

        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setEnableHeaderTranslationContent(true)
            MaterialHeader(context).setColorSchemeResources(R.color.blue, R.color.blue, R.color.blue)
        }

        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setEnableFooterFollowWhenNoMoreData(true)
            layout.setEnableFooterTranslationContent(true)
            layout.setFooterHeight(153f)
            layout.setFooterTriggerRate(0.6f)
            NoStatusFooter.REFRESH_FOOTER_NOTHING = GlobalUtil.getString(R.string.footer_not_more)
            NoStatusFooter(context).apply {
                setAccentColorId(R.color.colorTextPrimary)
                setTextTitleSize(16f)
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        IjkPlayerManager.setLogLevel(if (BuildConfig.DEBUG) IjkMediaPlayer.IJK_LOG_WARN else IjkMediaPlayer.IJK_LOG_SILENT)
        WebViewActivity.DEFAULT_URL.preCreateSession()
        if (!SplashActivity.isFirstEntryApp && DialogAppraiseTipsWorker.isNeedShowDialog) {
            WorkManager.getInstance(this).enqueue(DialogAppraiseTipsWorker.showDialogWorkRequest)
        }
    }

    /**
     * des : companion 关键字 伴生对象 替代JAVA 中的 static 修饰符
     * 伴生对象是实际对象的单例实例，
     * 允许类名访问伴生对象的内容（如果伴生对象存在一个特定的类中）
     * 伴生对象中的变量 相当于 常量
     * Lateinit 关键字  变量不需要初始化，在使用变量时不需要加上？或!! 操作符，在第一次使用时保证变量赋值，
     * 不然会出现空指针异常。
     *
    */
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}