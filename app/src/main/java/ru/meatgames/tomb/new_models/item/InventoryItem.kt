package ru.meatgames.tomb.new_models.item

import android.graphics.Rect
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonIgnore
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.Assets
import ru.meatgames.tomb.new_models.provider.GameDataProvider
import ru.meatgames.tomb.util.formatNumber

@JsonObject
abstract class InventoryItem {

	@JsonField(name = ["id"]) var id: Int = 0
	@JsonField(name = ["title"]) var title: String = ""
	@JsonField(name = ["ending"]) var ending: String = ""
	@JsonField(name = ["description"]) var description: String = ""
	@JsonField(name = ["statModifiers"]) var statModifiers: List<StatModifier> = emptyList()
	@JsonIgnore var image: Rect = Assets.getAssetRect(0)


	open fun getStatsDescription(): List<String> {
		return List(statModifiers.size) { index ->
			"${statModifiers[index].modifier.formatNumber()} ${GameDataProvider.stats.getStat(statModifiers[index].id).title}"
		}
	}

}