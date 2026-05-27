package com.calcwidget.app

import android.content.Context
import android.graphics.Color

data class CalcTheme(
    val widgetBg: Int, val displayBg: Int, val displayText: Int,
    val btnBg: Int, val btnText: Int, val actionBg: Int, val actionText: Int,
    val cornerDp: Int = 24
)

object ThemeManager {

    private const val PREFS = "calc_theme"
    private const val KEY   = "theme_name"

    val themes: Map<String, CalcTheme> = linkedMapOf(
        "Eco Default"    to CalcTheme(p("#0B1105ED"), Color.TRANSPARENT, Color.WHITE,       p("#2AFFFFFF"), Color.WHITE,       p("#9EAE6F"), p("#0B1105")),
        "Material Light" to CalcTheme(p("#F3F4F6"),   Color.WHITE,       p("#111827"),      p("#E5E7EB"),   p("#1F2937"),      p("#8B5CF6"), Color.WHITE),
        "AMOLED Neon"    to CalcTheme(Color.BLACK,    Color.BLACK,       p("#10B981"),      p("#111111"),   Color.WHITE,       p("#10B981"), Color.BLACK,  12),
        "Pastel Dream"   to CalcTheme(p("#FDF4FF"),   p("#FCE7F3"),      p("#831843"),      p("#FBCFE8"),   p("#9D174D"),      p("#EC4899"), Color.WHITE,  32),
        "Ocean Blue"     to CalcTheme(p("#0F172A"),   p("#1E293B"),      p("#E0F2FE"),      p("#0F172A"),   p("#BAE6FD"),      p("#0EA5E9"), Color.WHITE,  36),
    )

    fun current(ctx: Context): CalcTheme {
        val name = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "Eco Default") ?: "Eco Default"
        return themes[name] ?: themes.values.first()
    }

    fun currentName(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "Eco Default") ?: "Eco Default"

    fun save(ctx: Context, name: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, name).apply()
    }

    private fun p(hex: String): Int = if (hex.length == 9) {
        val (r, g, b, a) = listOf(hex.substring(1,3), hex.substring(3,5), hex.substring(5,7), hex.substring(7,9))
        Color.parseColor("#$a$r$g$b")
    } else Color.parseColor(hex)
}
