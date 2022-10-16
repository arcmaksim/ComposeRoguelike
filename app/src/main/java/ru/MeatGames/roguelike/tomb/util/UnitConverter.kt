package ru.MeatGames.roguelike.tomb.util

import android.content.Context
import android.util.TypedValue

object UnitConverter {

    @JvmStatic fun convertSpToPixels(sp: Float, context: Context): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)

    @JvmStatic fun convertDpToPixels(dp: Float, context: Context): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

}