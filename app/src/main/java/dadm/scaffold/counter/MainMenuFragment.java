package dadm.scaffold.counter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dadm.scaffold.BaseFragment;
import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;
import dadm.scaffold.sound.SoundManager;


public class MainMenuFragment extends BaseFragment implements View.OnClickListener {
    public MainMenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        return rootView;
    }

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
        TextView btnMusic = (TextView)
                getView().findViewById(R.id.btn_music);
        if (soundManager.getMusicStatus()) {
            btnMusic.setText(R.string.music_on);
        } else {
            btnMusic.setText(R.string.music_off);
        }
        TextView btnSounds = (TextView)
                getView().findViewById(R.id.btn_sound);
        if (soundManager.getSoundStatus()) {
            btnSounds.setText(R.string.sound_on);
        } else {
            btnSounds.setText(R.string.sound_off);
        }
    }
}
