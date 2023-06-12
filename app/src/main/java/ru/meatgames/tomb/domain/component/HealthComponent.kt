package ru.meatgames.tomb.domain.component

import ru.meatgames.tomb.logMessage

data class HealthComponent(
    val currentHealth: Int,
    val maxHealth: Int,
) {
    
    val isDepleted: Boolean
        get() = currentHealth <= 0
    
    val isAtMaxHealth: Boolean
        get() = currentHealth == maxHealth
    
    val ratio: Float
        get() = currentHealth.toFloat() / maxHealth
    
    constructor(
        maxHealth: Int,
    ) : this(
        currentHealth = maxHealth,
        maxHealth = maxHealth,
    )
    
    fun updateHealth(
        modifier: Int,
    ): HealthComponent {
        val value = (currentHealth + modifier).coerceAtMost(maxHealth)
        logMessage("Attack", "was $currentHealth, now $value")
        return copy(
            currentHealth = value,
        )
    }
    
}