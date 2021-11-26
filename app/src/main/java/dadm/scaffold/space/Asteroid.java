package dadm.scaffold.space;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.ScreenGameObject;
import dadm.scaffold.engine.Sprite;
import dadm.scaffold.engine.particles.ParticleSystem;
import dadm.scaffold.engine.particles.ScaleInitializer;
import dadm.scaffold.engine.particles.ScaleModifier;
import dadm.scaffold.sound.GameEvent;

public class Asteroid extends Sprite {
    public static final int EXPLOSION_PARTICLES = 15;
    private final GameController gameController;

    private double speed;
    private double speedX;
    private double speedY;
    private double rotationSpeed;
    private ParticleSystem mTrailParticleSystem; //necesito un sprite para el trail
    private ParticleSystem mExplisionParticleSystem;

    public Asteroid(GameController gameController, GameEngine gameEngine) {
        super(gameEngine, R.drawable.a10000);
        this.speed = 200d * pixelFactor / 1000d;
        this.gameController = gameController;
        mBodyType = BodyType.Circular;
        //necesito otro sprite para el trail
        mTrailParticleSystem = new ParticleSystem(gameEngine, 50, R.drawable.a10000, 600)
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
        // They initialize in a [-30, 30] degrees angle
        double angle = gameEngine.random.nextDouble() * Math.PI / 3d - Math.PI / 6d;
        speedX = speed * Math.sin(angle);
        speedY = speed * Math.cos(angle);
        // Asteroids initialize in the central 50% of the screen horizontally
        positionX = gameEngine.random.nextInt(gameEngine.width / 2) + gameEngine.width / 4;
        // They initialize outside of the screen vertically
        positionY = -height;
        rotationSpeed = angle * (180d / Math.PI) / 250d; // They rotate 4 times their ange in a second.
        rotation = gameEngine.random.nextInt(360);
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
        double prob = Math.random() * 101;

        if (prob >= 80) {
            gameController.spawnPowerUp(gameEngine, positionX, positionY);
        }

        gameEngine.removeGameObject(this);
        gameController.returnToPool(this);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        positionX += speedX * elapsedMillis;
        positionY += speedY * elapsedMillis;
        rotation += rotationSpeed * elapsedMillis;
        if (rotation > 360) {
            rotation = 0;
        } else if (rotation < 0) {
            rotation = 360;
        }
        // Check of the sprite goes out of the screen and return it to the pool if so
        if (positionY > gameEngine.height) {
            // Return to the pool
            gameEngine.onGameEvent(GameEvent.AsteroidMissed);
            gameEngine.removeGameObject(this);
            gameController.returnToPool(this);
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
}
