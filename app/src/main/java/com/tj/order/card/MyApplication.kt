package com.tj.order.card

import android.app.Application
import com.tj.order.card.layoutmanager.utils.LogUtil
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * 作者:TJbaobao
 * 时间:2019/1/2  14:15
 * 说明:
 * 使用：
 */
class MyApplication : Application() {

    private var defHandler: Thread.UncaughtExceptionHandler? = null

    override fun onCreate() {
        super.onCreate()
        defHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler())
    }

    private inner class MyUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
        /**
         * 处理未捕获异常
         */
        override fun uncaughtException(thread: Thread, ex: Throwable) {
            try {
                //获取并记录异常日志
                val baos = ByteArrayOutputStream()
                val ps = PrintStream(baos)
                ex.printStackTrace(ps)
                val data: ByteArray = baos.toByteArray()
                val sLog = "程序出错：" + String(data)
                LogUtil.e(sLog)
                ps.close()
                baos.close()
            } catch (ignored: Exception) {

            }

            //让默认未捕获异常处理器来处理未捕获异常
            defHandler?.uncaughtException(thread, ex)
        }
    }
}