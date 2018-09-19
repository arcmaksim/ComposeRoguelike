package ru.MeatGames.roguelike.tomb.new_models.stat

enum class StatLinkType(val type: String) {
	CHILD("child"),
	PARENT("parent"),
	OWNER("owner")
}