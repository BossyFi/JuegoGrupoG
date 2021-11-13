package dadm.scaffold.engine;

import android.graphics.Canvas;

public abstract class GameObject {

    /**
     * Inicializa los objetos antes de que comience la partida
     */
    public abstract void startGame();

    /**
     * Es llamado por el GameEngine lo más rápido posible
     *
     * @param elapsedMillis los milisegundos desde la última llamada
     * @param gameEngine    el GameEngine
     */
    public abstract void onUpdate(long elapsedMillis, GameEngine gameEngine);

    /**
     * Hace que el propio objeto se renderice
     *
     * @param canvas el canvas sobre el que se dibuja.
     */
    public abstract void onDraw(Canvas canvas);

    /**
     * Runnable que ejecuta el método onAddedToGameUIThread
     */
    public final Runnable onAddedRunnable = new Runnable() {
        @Override
        public void run() {
            onAddedToGameUiThread();
        }
    };

    /**
     * Contiene código que debe ejecutarse en el UIThread cuando un objeto es añadido a la escena
     */
    public void onAddedToGameUiThread() {
    }

    /**
     * Runnable que ejecuta el método onRemovedFromGameUIThread
     */
    public final Runnable onRemovedRunnable = new Runnable() {
        @Override
        public void run() {
            onRemovedFromGameUiThread();
        }
    };

    /**
     * Contiene código que debe ejecutarse en el UIThread cuando un objeto es eliminado de la escena
     */

    public void onRemovedFromGameUiThread() {
    }

}
