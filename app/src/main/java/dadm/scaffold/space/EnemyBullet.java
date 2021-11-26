package dadm.scaffold.space;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.sound.GameEvent;

public class EnemyBullet extends Sprite {

    private double speedFactor;
    private final GameController gameController;
    private SpaceShipEnemy parent;

    public EnemyBullet(GameController gameController, GameEngine gameEngine) {
        super(gameEngine, R.drawable.enemybullet_small);
        mBodyType = BodyType.Circular;
        speedFactor = gameEngine.pixelFactor * 200d / 1000d;
        this.gameController = gameController;
    }

    @Override
    public void startGame() {
    }

    @Override
    public void startGame(GameEngine gameEngine) {

    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        positionY += speedFactor * elapsedMillis;
        if (positionY < -height) {
            gameEngine.removeGameObject(this);
            // And return it to the pool
            parent.releaseBullet(this);
        }
    }


    public void init(SpaceShipEnemy parentPlayer, double initPositionX, double initPositionY) {
        positionX = initPositionX - width / 2;
        positionY = initPositionY - height / 2;
        parent = parentPlayer;
    }

//    private void removeObject(GameEngine gameEngine) {
//        gameEngine.removeGameObject(this);
//        // And return it to the pool
//        parent.releaseBullet(this);
//    }

    public void removeObject(GameEngine gameEngine) {
        // Return to the pool
        gameEngine.removeGameObject(this);
        parent.releaseBullet(this);
    }


    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {
//        if (otherObject instanceof Asteroid) {
//            // Remove both from the game (and return them to their pools)
//            removeObject(gameEngine);
//            Asteroid a = (Asteroid) otherObject;
//            a.removeObject(gameEngine);
//            a.explode(gameEngine);
//            gameEngine.onGameEvent(GameEvent.AsteroidHit);
//            // Add some score
//        }
    }
}
