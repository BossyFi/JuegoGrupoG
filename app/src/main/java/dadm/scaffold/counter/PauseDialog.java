package dadm.scaffold.counter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;
import dadm.scaffold.sound.SoundManager;

public class PauseDialog extends BaseCustomDialog
        implements View.OnClickListener {
    private PauseDialogListener mListener;

    public PauseDialog(ScaffoldActivity activity) {
        super(activity);
        setContentView(R.layout.pause_layout);
        findViewById(R.id.btn_music).setOnClickListener(this);
        findViewById(R.id.btn_sound).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        updateSoundAndMusicButtons();
    }

    public void setListener(PauseDialogListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_sound) {
            parent.getSoundManager().toggleSoundStatus();
            updateSoundAndMusicButtons();
        } else if (v.getId() == R.id.btn_music) {
            parent.getSoundManager().toggleMusicStatus();
            updateSoundAndMusicButtons();
        } else if (v.getId() == R.id.btn_exit) {
            super.dismiss();
            mListener.exitGame();
        } else if (v.getId() == R.id.btn_resume) {
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mListener.resumeGame();
    }

    public void updateSoundAndMusicButtons() {
        SoundManager soundManager = parent.getSoundManager();

        ImageView btnMusic = (ImageView) findViewById(R.id.btn_music);
        if (soundManager.getMusicStatus()) {
            btnMusic.setBackground(ContextCompat.getDrawable(parent, R.drawable.button_pressed));
            btnMusic.setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.volume_on_nobg));
        } else {
            btnMusic.setBackground(ContextCompat.getDrawable(parent, R.drawable.button_normal));
            btnMusic.setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.volume_off_nobg));
        }
        ImageView btnSounds = (ImageView) findViewById(R.id.btn_sound);
        if (soundManager.getSoundStatus()) {
            btnSounds.setBackground(ContextCompat.getDrawable(parent, R.drawable.button_pressed));
            btnSounds.setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.sound_on_nobg));
        } else {
            btnSounds.setBackground(ContextCompat.getDrawable(parent, R.drawable.button_normal));
            btnSounds.setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.sound_off_nobg));
        }
    }

    public interface PauseDialogListener {
        void exitGame();

        void resumeGame();
    }
}