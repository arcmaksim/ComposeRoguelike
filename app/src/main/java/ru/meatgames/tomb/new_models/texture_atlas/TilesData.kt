package ru.meatgames.tomb.new_models.texture_atlas

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import ru.meatgames.tomb.new_models.tile.TileTextureJsonModel

@JsonObject
class TilesData {

    @JsonField(name = ["frames"]) var tiles: List<TileTextureJsonModel.TextureAtlasTile> = emptyList()

}