package ru.meatgames.tomb.screen.game.animation

const val CHARACTER_IDLE_ANIMATION_FRAMES = 2
const val CHARACTER_IDLE_ANIMATION_DURATION_MILLIS = 600

const val ANIMATION_DURATION_MILLIS = 300

//region Attack
/**
 * Distance modifier for attack animation - mutliplied by tile size
 */
const val ATTACK_DISTANCE_MODIFIER = .2f
const val DEFAULT_ATTACK_DURATION_MILLIS = ANIMATION_DURATION_MILLIS
const val DEFAULT_ATTACK_DELAY_MILLIS = DEFAULT_ATTACK_DURATION_MILLIS - (DEFAULT_ATTACK_DURATION_MILLIS / 4)
const val ENEMIES_DEFAULT_ATTACK_EXAGGERATION = 1f
//endregion
