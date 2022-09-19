package ru.meatgames.tomb.new_models.rule

data class RuleRoom(
    val size: RoomSize = RoomSize.M,
    val role: RoomRole = RoomRole.REGULAR,
    val form: RoomForm = RoomForm.STRICT,
) {

    val width = when (size) {
        else -> 5
    }
    val height = when (size) {
        else -> 5
    }

}
