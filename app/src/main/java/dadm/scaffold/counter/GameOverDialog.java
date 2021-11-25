package dadm.scaffold.counter;

import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;

public class GameOverDialog extends BaseCustomDialog implements View.OnClickListener {

    private GameOverDialogListener listener;


    public interface GameOverDialogListener {
        void exitGame();

        void startNewGame();
    }

    public GameOverDialog(ScaffoldActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_game_over);
        findViewById(R.id.exit_game).setOnClickListener(this);
        findViewById(R.id.btn_play_again).setOnClickListener(this);
    }

    public void setListener(GameOverDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exit_game) {
            super.dismiss();
            listener.exitGame();
        } else if (v.getId() == R.id.btn_play_again) {
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        listener.startNewGame();
    }

    public void setGameOverPoints(int points) {
        TextView scorePoints = (TextView) findViewById(R.id.final_score_value);
        String text = String.format(Locale.ENGLISH, "%05d", points);
        scorePoints.setText(text);
    }
}