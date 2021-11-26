package dadm.scaffold.space;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.LinearLayout;

import dadm.scaffold.R;
import dadm.scaffold.database.Preferences;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.GameObject;
import dadm.scaffold.sound.GameEvent;

public class LivesCounter extends GameObject {

    private final LinearLayout layout;
    private Context context;

    public LivesCounter(View view, int viewResId, Context context) {
        layout = (LinearLayout) view.findViewById(viewResId);
        this.context = context;
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
            if (Preferences.GetShipValue(context, "PickedShip") == R.drawable.ship_a) {
                View spaceship = View.inflate(layout.getContext(),
                        R.layout.view_spaceship, layout);
            } else {
                View spaceship = View.inflate(layout.getContext(),
                        R.layout.view_spaceship2, layout);
            }

        }
    };
}