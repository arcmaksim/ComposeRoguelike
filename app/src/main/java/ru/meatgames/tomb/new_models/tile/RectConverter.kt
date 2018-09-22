package ru.meatgames.tomb.new_models.tile

import android.graphics.Rect
import com.bluelinelabs.logansquare.typeconverters.TypeConverter
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser

class RectConverter : TypeConverter<Rect> {

	override fun parse(jsonParser: JsonParser): Rect {
		val values = arrayListOf<Int>()
		for (i in 0 until 4) {
			jsonParser.nextValue()
			values.add(jsonParser.intValue)
		}
		return Rect(values[0], values[1], values[2], values[3])
	}

	override fun serialize(
			`object`: Rect?,
			fieldName: String?,
			writeFieldNameForObject: Boolean,
			jsonGenerator: JsonGenerator?) = Unit

}