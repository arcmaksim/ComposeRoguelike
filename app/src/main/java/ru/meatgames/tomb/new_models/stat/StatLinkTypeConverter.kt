package ru.meatgames.tomb.new_models.stat

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter

class StatLinkTypeConverter : StringBasedTypeConverter<StatLinkType>() {

    override fun convertToString(`object`: StatLinkType?): String = `object`?.type ?: ""

    override fun getFromString(string: String): StatLinkType = StatLinkType.getValue(string)

}