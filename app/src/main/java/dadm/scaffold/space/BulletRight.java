package dadm.scaffold.space;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.sound.GameEvent;

public class BulletRight extends Sprite {

    private double speedFactor;

    private SpaceShipPlayer parent;

    public BulletRight(GameEngine gameEngine) {
        super(gameEngine, R.drawable.side_bullets);
        mBodyType = BodyType.Circular;
        speedFactor = gameEngine.pixelFactor * -300d / 1000d;
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
        positionX -= speedFactor * elapsedMillis;
        if (positionY < -height) {
            gameEngine.removeGameObject(this);
            // And return it to the pool
            parent.releaseBullet(this);
        }
    }


    public void init(SpaceShipPlayer parentPlayer, double initPositionX, double initPositionY) {
        positionX = initPositionX - width / 2;
        positionY = initPositionY - height / 2;
        parent = parentPlayer;

    }

    private void removeObject(GameEngine gameEngine) {
        gameEngine.removeGameObject(this);
        // And return it to the pool
        parent.releaseBullet(this);
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {
        if (otherObject instanceof Asteroid) {
            // Remove both from the game (and return them to their pools)
            removeObject(gameEngine);
            Asteroid a = (Asteroid) otherObject;
            a.removeObject(gameEngine);
            a.explode(gameEngine);
            gameEngine.onGameEvent(GameEvent.AsteroidHit);
            // Add some score
        }
        if (otherObject instanceof SpaceShipEnemy) {
            // Remove both from the game (and return them to their pools)
            removeObject(gameEngine);
            SpaceShipEnemy a = (SpaceShipEnemy) otherObject;
            a.removeObject(gameEngine);
            a.explode(gameEngine);
            gameEngine.onGameEvent(GameEvent.AsteroidHit);
            // Add some score
        }
    }
}
