package dadm.scaffold.space;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.engine.particles.ParticleSystem;
import dadm.scaffold.engine.particles.ScaleInitializer;
import dadm.scaffold.engine.particles.ScaleModifier;
import dadm.scaffold.sound.GameEvent;

public class SpaceShipEnemy extends Sprite {
    public static final int EXPLOSION_PARTICLES = 15;
    private final GameController gameController;

    private long lastFrameChangeTime = 0;
    private final int frameLengthInMillisecond = 500;
    private int nextResourceIntegerId = 0;

    private double speed;
    private double speedX;
    private double speedY;
    private double rotationSpeed;
    private ParticleSystem mTrailParticleSystem; //necesito un sprite para el trail
    private ParticleSystem mExplisionParticleSystem;


    //Shoot thing
    private long timeSinceLastFire;
    private static final long TIME_BETWEEN_BULLETS = 600;
    private static final int INITIAL_BULLET_POOL_AMOUNT = 2;
    List<EnemyBullet> bullets = new ArrayList<EnemyBullet>();


    public SpaceShipEnemy(GameController gameController, GameEngine gameEngine) {
        super(gameEngine, R.drawable.enemyship_a);
        nextResourceIntegerId = R.drawable.enemyship_b;
        this.speed = 80d * pixelFactor / 1000d;
        this.gameController = gameController;
        mBodyType = BodyType.Circular;
        //necesito otro sprite para el trail
        mTrailParticleSystem = new ParticleSystem(gameEngine, 50, R.drawable.ship_a, 600)
                .addModifier(new ScaleModifier(1, 2, 200, 600))
                .setFadeOut(200);
        mExplisionParticleSystem = new ParticleSystem(gameEngine, EXPLOSION_PARTICLES, R.drawable.a10000, 700)
                .setSpeedRange(15, 40)
                .setFadeOut(300)
                .setInitialRotationRange(0, 360)
                .setRotationSpeedRange(-180, 180);
        mExplisionParticleSystem.addInitializer(new ScaleInitializer(0.5));
    }

    public void init(GameEngine gameEngine) {

        //Balillas
        initBulletPool(gameController, gameEngine);

        // They initialize in a [-30, 30] degrees angle
        double angle = gameEngine.random.nextDouble() * Math.PI / 3d - Math.PI / 6d;
        speedX = speed * Math.sin(angle);
        speedY = speed * Math.cos(angle);
        // Asteroids initialize in the central 50% of the screen horizontally
        positionX = gameEngine.random.nextInt(gameEngine.width / 2) + gameEngine.width / 4;
        // They initialize outside of the screen vertically
        positionY = -height;
        rotationSpeed = angle * (180d / Math.PI) / 250d; // They rotate 4 times their ange in a second.
        rotation = 180;//gameEngine.random.nextInt(360);
        mTrailParticleSystem.clearInitializers()
                .setInitialRotationRange(0, 360)
                .setRotationSpeedRange(rotationSpeed * 800, rotationSpeed * 1000)
                .setSpeedByComponentsRange(-speedY * 100, speedY * 100, speedX * 100, speedX * 100);
    }

    @Override
    public void startGame() {
    }

    @Override
    public void startGame(GameEngine gameEngine) {

    }

    public void removeObject(GameEngine gameEngine) {
        // Return to the pool
        gameEngine.removeGameObject(this);
        gameController.returnToPoolSpaceShip(this);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        positionX += speedX * elapsedMillis;
        positionY += speedY * elapsedMillis;
//        rotation += rotationSpeed * elapsedMillis;
//        if (rotation > 360) {
//            rotation = 0;
//        } else if (rotation < 0) {
//            rotation = 360;
//        }
        // Check of the sprite goes out of the screen and return it to the pool if so
        checkFiring(elapsedMillis, gameEngine);

        if (positionY > gameEngine.height) {
            // Return to the pool
            gameEngine.onGameEvent(GameEvent.AsteroidMissed);
            gameEngine.removeGameObject(this);
            gameController.returnToPoolSpaceShip(this);
        }
    }

    @Override
    public void onCollision(GameEngine gameEngine, ScreenGameObject otherObject) {

    }

    @Override
    public void addToGameEngine(GameEngine gameEngine) {
        super.addToGameEngine(gameEngine);
        mTrailParticleSystem.addToGameEngine(gameEngine);
        mTrailParticleSystem.emit(15);
    }

    @Override
    public void removeFromGameEngine(GameEngine gameEngine) {
        super.removeFromGameEngine(gameEngine);
        mTrailParticleSystem.stopEmiting();
        mTrailParticleSystem.removeFromGameEngine(gameEngine);
    }

    public void explode(GameEngine gameEngine) {
        mExplisionParticleSystem.oneShot(gameEngine, positionX + width / 2.0, positionY + height / 2.0, EXPLOSION_PARTICLES);
    }


    private void initBulletPool(GameController gameController, GameEngine gameEngine) {
        for (int i = 0; i < INITIAL_BULLET_POOL_AMOUNT; i++) {
            bullets.add(new EnemyBullet(gameController, gameEngine));
        }
    }


    private EnemyBullet getBullet() {
        if (bullets.isEmpty()) {
            return null;
        }
        return bullets.remove(0);
    }

    void releaseBullet(EnemyBullet bullet) {
        bullets.add(bullet);
    }


    private void checkFiring(long elapsedMillis, GameEngine gameEngine) {
        //gameEngine.theInputController.isFiring
        if (timeSinceLastFire > TIME_BETWEEN_BULLETS) {
            EnemyBullet bullet = getBullet();
            if (bullet == null) {
                return;
            }
            bullet.init(this, positionX + width / 2, positionY);
            gameEngine.addGameObject(bullet);
            timeSinceLastFire = 0;
            gameEngine.onGameEvent(GameEvent.EnemyLaser);
        } else {
            timeSinceLastFire += elapsedMillis;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMillisecond) {
            lastFrameChangeTime = time;
            super.setBitmap(nextResourceIntegerId);
            if (nextResourceIntegerId == R.drawable.enemyship_a) {
                nextResourceIntegerId = R.drawable.enemyship_b;
            } else {
                nextResourceIntegerId = R.drawable.enemyship_a;
            }
        }
        super.onDraw(canvas);
    }


}
