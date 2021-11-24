package dadm.scaffold.counter;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import dadm.scaffold.BaseFragment;
import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;
import dadm.scaffold.engine.FramesPerSecondCounter;
import dadm.scaffold.engine.GameEngine;
import dadm.scaffold.engine.GameView;
import dadm.scaffold.engine.ScoreGameObject;
import dadm.scaffold.space.LivesCounter;
import dadm.scaffold.space.ParallaxBackground;
import dadm.scaffold.input.JoystickInputController;
import dadm.scaffold.space.GameController;
import dadm.scaffold.space.SpaceShipPlayer;


public class GameFragment extends BaseFragment implements View.OnClickListener, PauseDialog.PauseDialogListener {
    private GameEngine theGameEngine;

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
        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Para evitar que sea llamado múltiples veces,
                //se elimina el listener en cuanto es llamado
                observer.removeOnGlobalLayoutListener(this);
                GameView gameView = (GameView) getView().findViewById(R.id.gameView);
                theGameEngine = new GameEngine(getActivity(), gameView);
                theGameEngine.setSoundManager(getScaffoldActivity().getSoundManager());
                theGameEngine.setTheInputController(new JoystickInputController(getView()));
                theGameEngine.addGameObject(new ScoreGameObject(getView(), R.id.score_value));
                theGameEngine.addGameObject(new ParallaxBackground(theGameEngine, 20, R.drawable.seamless_space_0));
                //theGameEngine.addGameObject(new SpaceShipPlayer(theGameEngine));
                theGameEngine.addGameObject(new LivesCounter(getView(), R.id.lives_value));
                theGameEngine.addGameObject(new FramesPerSecondCounter(theGameEngine));
                theGameEngine.addGameObject(new GameController(theGameEngine));
                theGameEngine.startGame();
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pause) {
            pauseGameAndShowPauseDialog();
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
        getScaffoldActivity().navigateBack();
    }

    @Override
    public void resumeGame() {
        theGameEngine.resumeGame();
    }
}
