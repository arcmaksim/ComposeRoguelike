package ru.MeatGames.roguelike.tomb.new_models.repo

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.MeatGames.roguelike.tomb.new_models.stat.Stat

@JsonObject
class StatRepo {

	@JsonField(name = ["stats"]) var stats: List<Stat> = emptyList()


	fun getStat(statId: String): Stat = stats.first { it.id == statId }

}