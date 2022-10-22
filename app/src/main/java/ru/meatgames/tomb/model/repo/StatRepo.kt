package ru.meatgames.tomb.model.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.model.stat.Stat

@JsonObject
class StatRepo {

	@JsonField(name = ["stats"]) var stats: List<Stat> = emptyList()


	fun getStat(statId: String): Stat = stats.first { it.id == statId }

}
