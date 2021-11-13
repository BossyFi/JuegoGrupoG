package dadm.scaffold.engine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dadm.scaffold.input.InputController;

public class GameEngine {


    private List<GameObject> gameObjects = new ArrayList<GameObject>();
    /**
     * Lista auxiliar para llevar la cuenta de los objetos que se añadirán. Se usa si el juego está
     * en marcha y más tarde se utiliza.
     */
    private List<GameObject> objectsToAdd = new ArrayList<GameObject>();
    /**
     * Lista auxiliar para llevar la cuenta de los objetos que se eliminarán. Se usa si el juego está
     * en marcha y más tarde se utiliza.
     */
    private List<GameObject> objectsToRemove = new ArrayList<GameObject>();
    private List<Collision> detectedCollisions = new ArrayList<Collision>();
    private QuadTree quadTree = new QuadTree();

    private UpdateThread theUpdateThread;
    private DrawThread theDrawThread;
    public InputController theInputController;
    private final GameView theGameView;

    public Random random = new Random();

    public int width;
    public int height;
    public double pixelFactor;

    private Activity mainActivity;

    public GameEngine(Activity activity, GameView gameView) {
        mainActivity = activity;

        theGameView = gameView;
        theGameView.setGameObjects(this.gameObjects);

        QuadTree.init();

        this.width = theGameView.getWidth()
                - theGameView.getPaddingRight() - theGameView.getPaddingLeft();
        this.height = theGameView.getHeight()
                - theGameView.getPaddingTop() - theGameView.getPaddingTop();

        quadTree.setArea(new Rect(0, 0, width, height));

        this.pixelFactor = this.height / 400d;
    }

    public void setTheInputController(InputController inputController) {
        theInputController = inputController;
    }

    public void startGame() {
        // Paramos el juego si está corriendo
        stopGame();

        // Configuramos los objetos en la posición inicial llamando al startGame de cada uno
        int nugameObjects = gameObjects.size();
        for (int i = 0; i < nugameObjects; i++) {
            gameObjects.get(i).startGame();
        }

        // Comenzamos el Update Thread
        theUpdateThread = new UpdateThread(this);
        theUpdateThread.start();

        // Comenzamos el Draw Thread
        theDrawThread = new DrawThread(this);
        theDrawThread.start();
    }

    public void stopGame() {
        if (theUpdateThread != null) {
            theUpdateThread.stopGame();
        }
        if (theDrawThread != null) {
            theDrawThread.stopGame();
        }
    }

    public void pauseGame() {
        if (theUpdateThread != null) {
            theUpdateThread.pauseGame();
        }
        if (theDrawThread != null) {
            theDrawThread.pauseGame();
        }
    }

    public void resumeGame() {
        if (theUpdateThread != null) {
            theUpdateThread.resumeGame();
        }
        if (theDrawThread != null) {
            theDrawThread.resumeGame();
        }
    }

    /**
     * El motor tiene que manejar la adición o eliminación de gameObjects. No podemos
     * pasarle la lista de objetos directamente debido a que se usará de forma intensiva durante
     * onUpdate y onDraw, por eso tenemos las listas auxiliares en caso de que el juego esté ya en
     * marcha.
     *
     * @param gameObject El gameObject a añadir
     */
    public void addGameObject(GameObject gameObject) {
        if (isRunning()) {
            objectsToAdd.add(gameObject);
        } else {
            addGameObjectNow(gameObject);
        }
        mainActivity.runOnUiThread(gameObject.onAddedRunnable);
    }

    public void removeGameObject(GameObject gameObject) {
        objectsToRemove.add(gameObject);
        mainActivity.runOnUiThread(gameObject.onRemovedRunnable);
    }

    /**
     * Lo primero que se hace es ejecutar el método update sobre cada uno de los
     * gameObjects que están ya en la escena. Después de eso, se usa una sección donde se
     * sincronizan los gameObjects para que no se vuelva a hacer un update sobre los objetos
     * de la escena hasta que se eliminen de la escena los objetos que teníamos en la lista
     * auxiliar o se añaden a la escena los objetos que teníamos en la otra lista auxiliar.
     * En resumen, no volver a actualizar los gameObjects hasta que se vacíen las listas.
     * @param elapsedMillis el tiempo en milisegundos desde la última llamada al método.
     */
    public void onUpdate(long elapsedMillis) {
        int numGameObjects = gameObjects.size();
        for (int i = 0; i < numGameObjects; i++) {
            GameObject go = gameObjects.get(i);
            go.onUpdate(elapsedMillis, this);
            if (go instanceof ScreenGameObject) {
                ((ScreenGameObject) go).onPostUpdate(this);
            }
        }
        checkCollisions();
        synchronized (gameObjects) {
            while (!objectsToRemove.isEmpty()) {
                GameObject objectToRemove = objectsToRemove.remove(0);
                gameObjects.remove(objectToRemove);
                if (objectToRemove instanceof ScreenGameObject) {
                    quadTree.removeGameObject((ScreenGameObject) objectToRemove);
                }
            }
            while (!objectsToAdd.isEmpty()) {
                GameObject gameObject = objectsToAdd.remove(0);
                addGameObjectNow(gameObject);
            }
        }
    }

    public void onDraw() {
        theGameView.draw();
    }

    public boolean isRunning() {
        return theUpdateThread != null && theUpdateThread.isGameRunning();
    }

    public boolean isPaused() {
        return theUpdateThread != null && theUpdateThread.isGamePaused();
    }

    public Context getContext() {
        return theGameView.getContext();
    }

    private void checkCollisions() {
        // Release the collisions from the previous step
        while (!detectedCollisions.isEmpty()) {
            Collision.release(detectedCollisions.remove(0));
        }
        quadTree.checkCollisions(this, detectedCollisions);
    }

    private void addGameObjectNow(GameObject object) {
        gameObjects.add(object);
        if (object instanceof ScreenGameObject) {
            ScreenGameObject sgo = (ScreenGameObject) object;

            quadTree.addGameObject(sgo);
        }
    }
}
