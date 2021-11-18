package dadm.scaffold.space;

import android.graphics.Canvas;
import android.view.View;
import android.widget.LinearLayout;

import dadm.scaffold.R;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.GameObject;
import dadm.scaffold.sound.GameEvent;

public class LivesCounter extends GameObject {

    private final LinearLayout layout;

    public LivesCounter(View view, int viewResId) {
        layout = (LinearLayout) view.findViewById(viewResId);
    }

    @Override
    public void startGame() {

    }

    @Override
    public void startGame(GameEngine gameEngine) {

    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        if (gameEvent == GameEvent.LifeLost) {
            layout.post(mRemoveLifeRunnable);
        } else if (gameEvent == GameEvent.LifeAdded) {
            layout.post(mAddLifeRunnable);
        }
    }

    private Runnable mRemoveLifeRunnable = new Runnable() {
        @Override
        public void run() {
            // Removemos vida
            layout.removeViewAt(layout.getChildCount() - 1);
        }
    };
    private Runnable mAddLifeRunnable = new Runnable() {
        @Override
        public void run() {
            // Añadimos vida
            View spaceship = View.inflate(layout.getContext(),
                    R.layout.view_spaceship, layout);
        }
    };
}