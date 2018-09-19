package ru.meatgames.tomb

class MainGameLoop : Runnable {

    @Volatile private var isRunning = true

    fun terminate() {
        isRunning = false
    }

    override fun run() {
        while (isRunning) {

            if (--GameController.mHero.init == 0) {
                if (--GameController.mHero.cregen == 0) {
                    GameController.mHero.cregen = GameController.mHero.regen
                    if (GameController.mHero.getStat(5) != GameController.mHero.getStat(6)) {
                        GameController.mHero.modifyStat(5, 1, 1)
                    }
                    if (GameController.mHero.isFullyHealed()) {
                        GameController.mHero.finishResting()
                    }
                }
                //Assets.game.updateHeroTurnCount(GameController.mHero.mIsResting)
                GameController.mIsPlayerTurn = true
                GameController.mIsPlayerMoved = false
                GameController.mAcceptPlayerInput = true
                while (GameController.mIsPlayerTurn) {
                    if (GameController.mHero.mIsResting) {
                        Thread.sleep(100)
                        if (GameController.mHero.mIsResting) {
                            GameController.skipTurn()
                        } else {
                            GameController.mHero.interruptResting()
                        }
                    }
                }
            }
            /*Assets.game.firstMob?.let {
                while (Assets.game.firstMob.turnCount <= Assets.game.turnCount) {
                    val temp = Assets.game.firstMob
                    Assets.game.firstMob = Assets.game.firstMob.next
                    if (Math.abs(temp.x - GameController.mHero.mx) < 5 && Math.abs(temp.y - GameController.mHero.my) < 5) {
                        Assets.game.mobTurn(temp)
                    }
                    Assets.game.addInQueue(temp)
                }
            }*/
            //Assets.game.turnCount++
        }
    }
}