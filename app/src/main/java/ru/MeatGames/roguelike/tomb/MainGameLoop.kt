package ru.MeatGames.roguelike.tomb

class MainGameLoop : Runnable {

    @Volatile private var isRunning = true

    fun terminate() {
        isRunning = false
    }

    override fun run() {
        while (isRunning) {
            if (--Assets.hero!!.init == 0) {
                if (--Assets.hero!!.cregen == 0) {
                    Assets.hero!!.cregen = Assets.hero!!.regen
                    if (Assets.hero!!.getStat(5) != Assets.hero!!.getStat(6)) {
                        Assets.hero!!.modifyStat(5, 1, 1)
                    }
                    if (Assets.hero!!.isFullyHealed()) {
                        Assets.hero!!.finishResting()
                    }
                }
                //Assets.game.updateHeroTurnCount(Assets.hero!!.mIsResting)
                GameController.mIsPlayerTurn = true
                GameController.mIsPlayerMoved = false
                GameController.mAcceptPlayerInput = true
                while (GameController.mIsPlayerTurn) {
                    if (Assets.hero!!.mIsResting) {
                        Thread.sleep(100)
                        if (Assets.hero!!.mIsResting) {
                            GameController.skipTurn()
                        } else {
                            Assets.hero!!.interruptResting()
                        }
                    }
                }
            }
            /*Assets.game.firstMob?.let {
                while (Assets.game.firstMob.turnCount <= Assets.game.turnCount) {
                    val temp = Assets.game.firstMob
                    Assets.game.firstMob = Assets.game.firstMob.next
                    if (Math.abs(temp.x - Assets.hero!!.mx) < 5 && Math.abs(temp.y - Assets.hero!!.my) < 5) {
                        Assets.game.mobTurn(temp)
                    }
                    Assets.game.addInQueue(temp)
                }
            }*/
            //Assets.game.turnCount++
        }
    }
}