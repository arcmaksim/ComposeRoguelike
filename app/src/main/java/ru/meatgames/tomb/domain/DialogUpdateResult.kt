package ru.meatgames.tomb.domain

sealed class DialogUpdateResult {
    
    data class NewInteraction(
        val dialogState: DialogState,
    ) : DialogUpdateResult()
    
    object NoChange : DialogUpdateResult()
    
    object DisruptInteraction : DialogUpdateResult()
    
}
