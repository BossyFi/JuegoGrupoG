package dadm.scaffold.counter;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dadm.scaffold.BaseFragment;
import dadm.scaffold.R;
import dadm.scaffold.engine.FramesPerSecondCounter;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.GameView;
import dadm.scaffold.engine.ScoreGameObject;
import dadm.scaffold.sound.GameEvent;
import dadm.scaffold.space.LivesCounter;
import dadm.scaffold.space.ParallaxBackground;
import dadm.scaffold.input.JoystickInputController;
import dadm.scaffold.space.GameController;


public class GameFragment extends BaseFragment implements View.OnClickListener, PauseDialog.PauseDialogListener, GameOverDialog.GameOverDialogListener, InputManager.InputDeviceListener {
    private GameEngine theGameEngine;

    private GameFragment gameFragment = this;
    private ScoreGameObject scoreGameObject;

    public GameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_pause).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pause) {
            pauseGameAndShowPauseDialog();
            getScaffoldActivity().getSoundManager().playSoundForGameEvent(GameEvent.PausedGame);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (theGameEngine.isRunning()) {
            pauseGameAndShowPauseDialog();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        theGameEngine.stopGame();
    }

    @Override
    public boolean onBackPressed() {
        if (theGameEngine.isRunning() && !theGameEngine.isPaused()) {
            pauseGameAndShowPauseDialog();
            return true;
        }
        return super.onBackPressed();
    }

    private void pauseGameAndShowPauseDialog() {
        if (theGameEngine.isPaused()) {
            return;
        }
        theGameEngine.pauseGame();
        PauseDialog dialog = new PauseDialog(getScaffoldActivity());
        dialog.setListener(this);
        showDialog(dialog);

    }

    private void playOrPause() {
        ImageView button = (ImageView) getView().findViewById(R.id.btn_pause);
        if (theGameEngine.isPaused()) {
            theGameEngine.resumeGame();
//            button.setText(R.string.pause);
        } else {
            theGameEngine.pauseGame();
//            button.setText(R.string.resume);
        }
    }

    @Override
    public void exitGame() {
        theGameEngine.stopGame();
        getScaffoldActivity().getSoundManager().playSoundForGameEvent(GameEvent.GoBackMenu);
        getScaffoldActivity().navigateBack();
    }

    @Override
    public void resumeGame() {
        theGameEngine.resumeGame();
        getScaffoldActivity().getSoundManager().playSoundForGameEvent(GameEvent.ResumeGame);
    }

    @Override
    public void startNewGame() {
        // Exit the current game
        theGameEngine.stopGame();
        // Start a new one
        prepareAndStartGame();
    }

    private void prepareAndStartGame() {
        GameView gameView = (GameView)
                getView().findViewById(R.id.gameView);
        theGameEngine = new GameEngine(getActivity(), gameView);
        theGameEngine.setSoundManager(getScaffoldActivity().getSoundManager());
        theGameEngine.setTheInputController(new JoystickInputController(getView()));
        scoreGameObject = new ScoreGameObject(getView(), R.id.score_value);
        theGameEngine.addGameObject(scoreGameObject);
        theGameEngine.addGameObject(new ParallaxBackground(theGameEngine, 100, R.drawable.seamless_space_0));

        theGameEngine.addGameObject(new ParallaxBackground(theGameEngine, 120, R.drawable.p_cuatro_resized));

        //theGameEngine.addGameObject(new SpaceShipPlayer(theGameEngine));
        theGameEngine.addGameObject(new LivesCounter(getView(), R.id.lives_value, getScaffoldActivity().getApplicationContext()));
        theGameEngine.addGameObject(new FramesPerSecondCounter(theGameEngine));
        theGameEngine.addGameObject(new GameController(theGameEngine, gameFragment, getScaffoldActivity().getApplicationContext()));
        theGameEngine.startGame();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            InputManager inputManager = (InputManager)
                    getActivity().getSystemService(Context.INPUT_SERVICE);
            inputManager.registerInputDeviceListener(GameFragment.this,
                    null);
        }
    }

    public int getFinalScore() {
        return scoreGameObject.GetFinalPoints();
    }

    @Override
    public void onInputDeviceAdded(int i) {
    }

    @Override
    public void onInputDeviceRemoved(int i) {

    }

    @Override
    public void onInputDeviceChanged(int i) {

    }

    @Override
    public void onLayoutCompleted() {
        prepareAndStartGame();
    }
}
