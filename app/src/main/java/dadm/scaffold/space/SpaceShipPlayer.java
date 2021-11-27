package dadm.scaffold.space;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import dadm.scaffold.R;
import dadm.scaffold.database.Preferences;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.input.InputController;
import dadm.scaffold.sound.GameEvent;

public class SpaceShipPlayer extends Sprite {

    private static final int INITIAL_BULLET_POOL_AMOUNT = 20;
    private static final long TIME_BETWEEN_BULLETS = 250;
    private static final long TIME_SPECIAL_ATTACK = 100;
    private static final long MAX_BULLETS_FIRED = 1;
    private long timeSinceLastSpecialAttack = 100;
    private static final int CD_RELOAD = 2000;
    private long timeSinceLastReload = 0;

    private static final long POWER_UP_DURATION = 5000;
    private long currentPowerUpTime = 0;
    private boolean hasPowerUp = false;

    List<Bullet> bullets = new ArrayList<Bullet>();


    private static final int INITIAL_SPECIAL_BULLET_POOL_AMOUNT = 20;
    List<BulletLeft> bulletsLeft = new ArrayList<BulletLeft>();
    List<BulletRight> bulletsRight = new ArrayList<BulletRight>();
//    List<Bullet> bullets = new ArrayList<Bullet>();

    private long timeSinceLastFire;

    private long lastFrameChangeTime = 0;
    private final int frameLengthInMillisecond = 500;
    private int nextResourceIntegerId = 0;
    private int bulletsFired = 0;

    private int maxX;
    private int maxY;
    private double speedFactor;

    private Context preferenceContext;

    public SpaceShipPlayer(GameEngine gameEngine, Context context) {
        super(gameEngine, Preferences.GetShipValue(context, "PickedShip"), context);
        if (Preferences.GetShipValue(context, "PickedShip") == R.drawable.ship_a) {
            nextResourceIntegerId = R.drawable.ship_b;
        } else {
            nextResourceIntegerId = R.drawable.player2_b;
        }

        speedFactor = pixelFactor * 100d / 1000d; // We want to move at 100px per second on a 400px tall screen
        maxX = gameEngine.width - width;
        maxY = gameEngine.height - height;
        mBodyType = BodyType.Circular;
        positionX = maxX / 2;
        positionY = maxY / 2;
        initBulletPool(gameEngine);
    }

    private void initBulletPool(GameEngine gameEngine) {
        for (int i = 0; i < INITIAL_BULLET_POOL_AMOUNT; i++) {
            bullets.add(new Bullet(gameEngine));
        }
        for (int i = 0; i < INITIAL_SPECIAL_BULLET_POOL_AMOUNT; i++) {
            bulletsLeft.add(new BulletLeft(gameEngine));
            bulletsRight.add(new BulletRight(gameEngine));
        }
    }

    private Bullet getBullet() {
        if (bullets.isEmpty()) {
            return null;
        }
        return bullets.remove(0);
    }

    private BulletLeft getBulletLeft() {
        if (bulletsLeft.isEmpty()) {
            return null;
        }
        return bulletsLeft.remove(0);
    }

    private BulletRight getBulletRight() {
        if (bulletsRight.isEmpty()) {
            return null;
        }
        return bulletsRight.remove(0);
    }


    void releaseBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    void releaseBullet(BulletRight bullet) {
        bulletsRight.add(bullet);
    }

    void releaseBullet(BulletLeft bullet) {
        bulletsLeft.add(bullet);
    }


    @Override
    public void startGame() {
        positionX = maxX / 2;
        positionY = maxY / 2;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        positionX = maxX / 2;
        positionY = maxY / 2;
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        // Get the info from the inputController
        updatePosition(elapsedMillis, gameEngine.theInputController);
        checkFiring(elapsedMillis, gameEngine);
    }

    private void updatePosition(long elapsedMillis, InputController inputController) {
        positionX += speedFactor * inputController.horizontalFactor * elapsedMillis;
        if (positionX < 0) {
            positionX = 0;
        }
        if (positionX > maxX) {
            positionX = maxX;
        }
        positionY += speedFactor * inputController.verticalFactor * elapsedMillis;
        if (positionY < 0) {
            positionY = 0;
        }
        if (positionY > maxY) {
            positionY = maxY;
        }
    }

    private void checkFiring(long elapsedMillis, GameEngine gameEngine) {


        if (gameEngine.theInputController.isFiring &&
                timeSinceLastSpecialAttack > TIME_SPECIAL_ATTACK &&
                bulletsFired < MAX_BULLETS_FIRED) {

            BulletLeft bulletLeft = getBulletLeft();
            if (bulletLeft == null) {
                return;
            }
            bulletLeft.init(this, positionX + width / 2, positionY);
            gameEngine.addGameObject(bulletLeft);


            BulletRight bulletRight = getBulletRight();
            if (bulletRight == null) {
                return;
            }
            bulletRight.init(this, positionX + width / 2, positionY);
            gameEngine.addGameObject(bulletRight);


            bulletsFired++;
            timeSinceLastSpecialAttack = 0;
            gameEngine.onGameEvent(GameEvent.LaserFired);

        } else {

            timeSinceLastSpecialAttack += elapsedMillis;

            if (timeSinceLastReload > CD_RELOAD && bulletsFired >= MAX_BULLETS_FIRED) {
                timeSinceLastReload = 0;
                bulletsFired = 0;
            } else {
                timeSinceLastReload += elapsedMillis;
            }


        }


        if (timeSinceLastFire > TIME_BETWEEN_BULLETS) {
            if (hasPowerUp && currentPowerUpTime < POWER_UP_DURATION) {

                Bullet bullet = getBullet();
                Bullet bullet2 = getBullet();
                if (bullet == null) {
                    return;
                }
                if (bullet2 == null) {
                    return;
                }
                bullet.init(this, (positionX + width / 2) + 25, positionY);
                bullet2.init(this, (positionX + width / 2) - 25, positionY);
                gameEngine.addGameObject(bullet);
                gameEngine.addGameObject(bullet2);
                timeSinceLastFire = 0;
                gameEngine.onGameEvent(GameEvent.LaserFired);

            } else {
                Bullet bullet = getBullet();
                if (bullet == null) {
                    return;
                }
                bullet.init(this, positionX + width / 2, positionY);
                gameEngine.addGameObject(bullet);
                timeSinceLastFire = 0;
                gameEngine.onGameEvent(GameEvent.LaserFired);
                hasPowerUp = false;
            }


        } else {
            timeSinceLastFire += elapsedMillis;
            currentPowerUpTime += elapsedMillis;
        }
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {
        if (otherObject instanceof Asteroid) {
            gameEngine.removeGameObject(this);
            //gameEngine.stopGame();
            Asteroid a = (Asteroid) otherObject;
            a.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.SpaceshipHit);
        }

        if (otherObject instanceof EnemyBullet) {
            gameEngine.removeGameObject(this);
            //gameEngine.stopGame();
            EnemyBullet a = (EnemyBullet) otherObject;
            a.removeObject(gameEngine);
            //gameEngine.removeGameObject(a);
            // And return it to the pool
//            parent.releaseBullet(this);
//            a.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.SpaceshipHit);
        }

        if (otherObject instanceof SpaceShipEnemy) {
            gameEngine.removeGameObject(this);
            //gameEngine.stopGame();
            SpaceShipEnemy a = (SpaceShipEnemy) otherObject;
            a.removeObject(gameEngine);
            gameEngine.onGameEvent(GameEvent.SpaceshipHit);
        }

        if (otherObject instanceof PowerUp) {

            PowerUp a = (PowerUp) otherObject;
            a.removeObject(gameEngine);
            currentPowerUpTime = 0;
            hasPowerUp = true;
            gameEngine.onGameEvent(GameEvent.PowerUp);
        }


    }

    @Override
    public void onDraw(Canvas canvas) {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMillisecond) {
            lastFrameChangeTime = time;
            super.setBitmap(nextResourceIntegerId);
            if (nextResourceIntegerId == R.drawable.ship_a) {
                nextResourceIntegerId = R.drawable.ship_b;
            } else if (nextResourceIntegerId == R.drawable.ship_b) {
                nextResourceIntegerId = R.drawable.ship_a;
            } else if (nextResourceIntegerId == R.drawable.player2_a) {
                nextResourceIntegerId = R.drawable.player2_b;
            } else if (nextResourceIntegerId == R.drawable.player2_b) {
                nextResourceIntegerId = R.drawable.player2_a;
            }
        }
        super.onDraw(canvas);
    }
}
