package com.calcwidget.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.widget.RemoteViews

class CalculatorWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_BTN = "com.calcwidget.app.BUTTON_CLICK"
        const val EXTRA_BTN  = "button_value"

        fun refresh(ctx: Context) {
            val mgr  = AppWidgetManager.getInstance(ctx)
            val ids  = mgr.getAppWidgetIds(ComponentName(ctx, CalculatorWidgetProvider::class.java))
            val intent = Intent(ctx, CalculatorWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            ctx.sendBroadcast(intent)
        }
    }

    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        ids.forEach { updateWidget(ctx, mgr, it) }
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        super.onReceive(ctx, intent)
        if (intent.action == ACTION_BTN) {
            CalculatorEngine.handleButton(ctx, intent.getStringExtra(EXTRA_BTN) ?: return)
            refresh(ctx)
        }
    }

    private fun updateWidget(ctx: Context, mgr: AppWidgetManager, id: Int) {
        val theme   = ThemeManager.current(ctx)
        val views   = RemoteViews(ctx.packageName, R.layout.widget_calculator)

        views.setTextViewText(R.id.tv_display, CalculatorEngine.getDisplay(ctx))
        views.setTextColor(R.id.tv_display, theme.displayText)
        views.setTextViewText(R.id.tv_operator, CalculatorEngine.getOperator(ctx))
        views.setTextColor(R.id.tv_operator, theme.displayText)
        views.setImageViewBitmap(R.id.img_widget_bg,  roundRect(theme.widgetBg,  theme.cornerDp.dp(ctx)))
        views.setImageViewBitmap(R.id.img_display_bg, roundRect(theme.displayBg, (theme.cornerDp * 0.8f).toInt().dp(ctx)))

        val ids = listOf(R.id.btn_c, R.id.btn_sign, R.id.btn_pct, R.id.btn_div,
            R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_mul,
            R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_sub,
            R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_add,
            R.id.btn_zero, R.id.btn_dot, R.id.btn_eq)
        val vals = listOf("C","+/-","%","÷","7","8","9","×","4","5","6","-","1","2","3","+","0",".","=")
        val actionSet = setOf("÷","×","-","+","=")

        ids.zip(vals).forEach { (viewId, value) ->
            val isAction = value in actionSet
            views.setImageViewBitmap(viewId, btnBmp(if (isAction) theme.actionBg else theme.btnBg, theme.cornerDp.dp(ctx).toFloat()))
            views.setTextColor(viewId, if (isAction) theme.actionText else theme.btnText)
            views.setOnClickPendingIntent(viewId, btnPending(ctx, value))
        }
        mgr.updateAppWidget(id, views)
    }

    private fun roundRect(color: Int, cornerPx: Int): Bitmap {
        val bmp = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
        Canvas(bmp).drawRoundRect(0f, 0f, 4f, 4f, cornerPx.toFloat(), cornerPx.toFloat(),
            Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color })
        return bmp
    }

    private fun btnBmp(color: Int, cornerPx: Float): Bitmap {
        val bmp = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888)
        Canvas(bmp).drawRoundRect(0f, 0f, 48f, 48f, cornerPx, cornerPx,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color })
        return bmp
    }

    private fun btnPending(ctx: Context, value: String): PendingIntent {
        val intent = Intent(ctx, CalculatorWidgetProvider::class.java).apply {
            action = ACTION_BTN; putExtra(EXTRA_BTN, value)
        }
        val flags = if (Build.VERSION.SDK_INT >= 23)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getBroadcast(ctx, value.hashCode() and 0xFFFF, intent, flags)
    }

    private fun Int.dp(ctx: Context) = (this * ctx.resources.displayMetrics.density + 0.5f).toInt()
}
