package dadm.scaffold.space;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import dadm.scaffold.ScaffoldActivity;
import dadm.scaffold.counter.GameFragment;
import dadm.scaffold.counter.GameOverDialog;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.GameObject;
import dadm.scaffold.sound.GameEvent;

public class GameController extends GameObject {

    private static final int TIME_BETWEEN_ENEMIES = 1000;
    private long currentMillis;
    private List<Asteroid> asteroidPool = new ArrayList<Asteroid>();
    private List<PowerUp> powerUpPool = new ArrayList<PowerUp>();
    private List<SpaceShipEnemy> spaceShipEnemyPool = new ArrayList<SpaceShipEnemy>();
    private int enemiesSpawned;
    private GameControllerState state;
    private GameFragment parent;
    private long waitingTime;
    private int INITIAL_LIFES = 4;
    private int numLives = 0;
    private long STOPPING_WAVE_WAITING_TIME = 2000;
    private long WAITING_TIME = 500;
    private Context context;

    public enum GameControllerState {
        StoppingWave,
        SpawningEnemies,
        PlacingSpaceship,
        Waiting,
        GameOver;
    }

    public GameController(GameEngine gameEngine) {
        // We initialize the pool of items now
        for (int i = 0; i < 20; i++) {
            asteroidPool.add(new Asteroid(this, gameEngine));
            spaceShipEnemyPool.add(new SpaceShipEnemy(this, gameEngine));
        }
    }

    public GameController(GameEngine gameEngine, GameFragment parent, Context context) {
        this.parent = parent;
        this.context = context;
        // We initialize the pool of items now
        for (int i = 0; i < 20; i++) {
            asteroidPool.add(new Asteroid(this, gameEngine));
            spaceShipEnemyPool.add(new SpaceShipEnemy(this, gameEngine));
            powerUpPool.add(new PowerUp(this, gameEngine));
        }
    }

    @Override
    public void startGame() {
        currentMillis = 0;
        enemiesSpawned = 0;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        currentMillis = 0;
        enemiesSpawned = 0;
        waitingTime = 0;
        for (int i = 0; i < INITIAL_LIFES; i++) {
            gameEngine.onGameEvent(GameEvent.LifeAdded);
        }
        state = GameControllerState.PlacingSpaceship;
    }


    public void spawnPowerUp(GameEngine gameEngine, double x, double y) {
        PowerUp a = powerUpPool.remove(0);
        a.init(gameEngine, x, y);
        gameEngine.addGameObject(a);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        if (state == GameControllerState.SpawningEnemies) {
            currentMillis += elapsedMillis;
            long waveTimestamp = enemiesSpawned * TIME_BETWEEN_ENEMIES;
            if (currentMillis > waveTimestamp) {
                // Spawn a new enemy
                double prob = Math.random() * 101;
                if (prob >= 40) {
                    SpaceShipEnemy a = spaceShipEnemyPool.remove(0);
                    a.init(gameEngine);
                    gameEngine.addGameObject(a);
                    enemiesSpawned++;
                    return;
                } else {
                    Asteroid a = asteroidPool.remove(0);
                    a.init(gameEngine);
                    gameEngine.addGameObject(a);
                    enemiesSpawned++;
                    return;
                }
            }


        } else if (state == GameControllerState.StoppingWave) {
            waitingTime += elapsedMillis;
            if (waitingTime > STOPPING_WAVE_WAITING_TIME) {
                state = GameControllerState.PlacingSpaceship;
            }
        } else if (state == GameControllerState.PlacingSpaceship) {
            if (numLives == 0) {
                gameEngine.onGameEvent(GameEvent.GameOver);
            } else {
                gameEngine.onGameEvent(GameEvent.LifeLost);
                SpaceShipPlayer newLife = new SpaceShipPlayer(gameEngine, context);
                newLife.addToGameEngine(gameEngine);
                newLife.startGame(gameEngine);
                // We wait to start spawning more enemies
                state = GameControllerState.Waiting;
                waitingTime = 0;
            }
        } else if (state == GameControllerState.Waiting) {
            waitingTime += elapsedMillis;
            if (waitingTime > WAITING_TIME) {
                state = GameControllerState.SpawningEnemies;
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        // This game object does not draw anything
    }

    public void returnToPool(Asteroid asteroid) {
        asteroidPool.add(asteroid);
    }

    public void returnToPoolPowerUp(PowerUp powerUp) {
        powerUpPool.add(powerUp);
    }

    public void returnToPoolSpaceShip(SpaceShipEnemy spaceShipEnemy) {
        spaceShipEnemyPool.add(spaceShipEnemy);
    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        if (gameEvent == GameEvent.SpaceshipHit) {
            state = GameControllerState.StoppingWave;
            waitingTime = 0;
        } else if (gameEvent == GameEvent.GameOver) {
            state = GameControllerState.GameOver;
            showGameOverDialog();
        } else if (gameEvent == GameEvent.LifeAdded) {
            numLives++;
        } else if (gameEvent == GameEvent.LifeLost) {
            numLives--;
        }
    }

    private void showGameOverDialog() {
        parent.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameOverDialog quitDialog = new GameOverDialog((ScaffoldActivity) parent.getActivity());
                quitDialog.setListener(parent);
                quitDialog.setGameOverPoints(parent.getFinalScore());
                parent.showDialog(quitDialog);
            }
        });
    }
}
