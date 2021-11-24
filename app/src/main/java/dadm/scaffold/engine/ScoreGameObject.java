package dadm.scaffold.engine;

import android.graphics.Canvas;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import dadm.scaffold.sound.GameEvent;

public class ScoreGameObject extends GameObject {
    private final TextView mText;
    private int mPoints;
    private boolean mPointsHaveChanged;
    private static final int POINTS_LOSS_PER_ASTEROID_MISSED = 1;
    private static final int POINTS_GAINED_PER_ASTEROID_HIT = 50;

    public ScoreGameObject(View view, int viewResId) {
        mText = (TextView) view.findViewById(viewResId);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
    }


    @Override
    public void startGame() {
        mPoints = 0;
        mText.post(mUpdateTextRunnable);
    }

    @Override
    public void startGame(GameEngine gameEngine) {

    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        if (gameEvent == GameEvent.AsteroidHit) {
            mPoints += POINTS_GAINED_PER_ASTEROID_HIT;
            mPointsHaveChanged = true;
        } else if (gameEvent == GameEvent.AsteroidMissed) {
            if (mPoints > 0) {
                mPoints -= POINTS_LOSS_PER_ASTEROID_MISSED;
            }
            mPointsHaveChanged = true;
        }
    }

    private Runnable mUpdateTextRunnable = new Runnable() {
        @Override
        public void run() {
            String text = String.format(Locale.ENGLISH, "%05d", mPoints);
            mText.setText(text);
        }
    };

    @Override
    public void onDraw(Canvas canvas) {
        if (mPointsHaveChanged) {
            mText.post(mUpdateTextRunnable);
            mPointsHaveChanged = false;
        }
    }

    public int GetFinalPoints() {
        return mPoints;
    }
}