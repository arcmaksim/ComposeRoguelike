package ru.meatgames.tomb.domain.component

data class HealthComponent(
    val currentHealth: Int,
    val maxHealth: Int,
) {
    
    val isDepleted: Boolean
        get() = currentHealth > 0
    
    val isAtMaxHealth: Boolean
        get() = currentHealth == maxHealth
    
    constructor(
        maxHealth: Int,
    ) : this(
        currentHealth = maxHealth,
        maxHealth = maxHealth,
    )
    
    fun updateHealth(
        modifier: Int,
    ): HealthComponent = copy(
        currentHealth = (currentHealth + modifier).coerceAtMost(maxHealth),
    )
    
}