package com.tj.order.card

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.tj.order.card.adapter.MainAdapter
import com.tj.order.card.layoutmanager.TJCardLayoutManager
import com.tj.order.card.layoutmanager.listener.OnCardViewListener
import com.tj.order.card.layoutmanager.listener.OnManagerLayoutListener
import com.tj.order.card.layoutmanager.listener.OnTJItemTouchHelperCallback
import com.tj.order.card.layoutmanager.utils.TJCardFlingManager
import com.tj.order.card.layoutmanager.utils.TJItemTouchHelper
import kotlinx.android.synthetic.main.main_activity_layout.*

/**
 * 作者:TJbaobao
 * 时间:2018/12/18  10:25
 * 说明:
 * 使用：
 */
class MainActivity : AppCompatActivity() {

    companion object {
        var isRunActivity = false

        fun getScreenWidth(context:Context): Int {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            return dm.widthPixels

        }
    }

    private val list = mutableListOf<String>()
    private val adapter = MainAdapter(list)
    private lateinit var  cardFlingManager : TJCardFlingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)
        isRunActivity = true
        cardFlingManager = TJCardFlingManager(recyclerView,OnTJCardViewListener())
        cardFlingManager.setLayoutManargerListener(MyOnManagerLayoutListener())
        recyclerView.adapter = adapter

        for(i in 0 until 100){
            list.add("$i")
        }
    }

    private inner class OnTJCardViewListener : OnCardViewListener{
        override fun onNexCardBegin(holder: RecyclerView.ViewHolder, position: Int) {
        }

        override fun onLastCardBegin(holder: RecyclerView.ViewHolder, position: Int) {

        }

        override fun onNexCardEnd(holder: RecyclerView.ViewHolder, position: Int) {
            super.onNexCardEnd(holder, position)
        }

        override fun onLastCardEnd(holder: RecyclerView.ViewHolder, position: Int) {
            adapter.notifyItemChanged(position)
        }
    }

    private inner class MyOnManagerLayoutListener: OnManagerLayoutListener{
        override fun onChildDraw(childView: View, fraction: Float, index: Int) {

        }
    }
}