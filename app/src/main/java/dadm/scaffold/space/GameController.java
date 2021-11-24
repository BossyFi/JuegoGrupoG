package dadm.scaffold.space;

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

    private static final int TIME_BETWEEN_ENEMIES = 500;
    private long currentMillis;
    private List<Asteroid> asteroidPool = new ArrayList<Asteroid>();
    private int enemiesSpawned;
    private GameControllerState state;
    private GameFragment parent;
    private long waitingTime;
    private int INITIAL_LIFES = 4;
    private int numLives = 0;
    private long STOPPING_WAVE_WAITING_TIME = 2000;
    private long WAITING_TIME = 500;

    public enum GameControllerState {
        StoppingWave,
        SpawningEnemies,
        PlacingSpaceship,
        Waiting,
        GameOver;
    }

    public GameController(GameEngine gameEngine) {
        // We initialize the pool of items now
        for (int i = 0; i < 10; i++) {
            asteroidPool.add(new Asteroid(this, gameEngine));
        }
    }

    public GameController(GameEngine gameEngine, GameFragment parent) {
        this.parent = parent;
        // We initialize the pool of items now
        for (int i = 0; i < 10; i++) {
            asteroidPool.add(new Asteroid(this, gameEngine));
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

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        if (state == GameControllerState.SpawningEnemies) {
            currentMillis += elapsedMillis;
            long waveTimestamp = enemiesSpawned * TIME_BETWEEN_ENEMIES;
            if (currentMillis > waveTimestamp) {
                // Spawn a new enemy
                Asteroid a = asteroidPool.remove(0);
                a.init(gameEngine);
                gameEngine.addGameObject(a);
                enemiesSpawned++;
                return;
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
                SpaceShipPlayer newLife = new SpaceShipPlayer(gameEngine);
                gameEngine.addGameObject(new SpaceShipPlayer(gameEngine));
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
