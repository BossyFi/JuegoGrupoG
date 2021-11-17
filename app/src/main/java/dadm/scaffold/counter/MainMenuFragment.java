package dadm.scaffold.counter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dadm.scaffold.BaseFragment;
import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;
import dadm.scaffold.sound.SoundManager;


public class MainMenuFragment extends BaseFragment implements View.OnClickListener {
    private boolean musicPressed;
    private boolean soundPressed;

    public MainMenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        return rootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_start).setOnClickListener(this);
        view.findViewById(R.id.btn_sound).setOnClickListener(this);
        view.findViewById(R.id.btn_music).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            ((ScaffoldActivity) getActivity()).startGame();
        } else if (v.getId() == R.id.btn_music) {
            SoundManager soundManager =
                    getScaffoldActivity().getSoundManager();
            soundManager.toggleMusicStatus();
            updateSoundAndMusicButtons();
        } else if (v.getId() == R.id.btn_sound) {
            SoundManager soundManager =
                    getScaffoldActivity().getSoundManager();
            soundManager.toggleSoundStatus();
            updateSoundAndMusicButtons();
        }
    }

    private void updateSoundAndMusicButtons() {
        SoundManager soundManager = getScaffoldActivity().getSoundManager();

        ImageView btnMusic = (ImageView) getView().findViewById(R.id.btn_music);
        if (soundManager.getMusicStatus()) {
            btnMusic.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_pressed));
            btnMusic.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.volume_on_nobg));
        } else {
            btnMusic.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_normal));
            btnMusic.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.volume_off_nobg));
        }
        ImageView btnSounds = (ImageView)
                getView().findViewById(R.id.btn_sound);
        if (soundManager.getSoundStatus()) {
            btnSounds.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_pressed));
            btnSounds.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.sound_on_nobg));
        } else {
            btnSounds.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_normal));
            btnSounds.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.sound_off_nobg));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSoundAndMusicButtons();
    }

}
