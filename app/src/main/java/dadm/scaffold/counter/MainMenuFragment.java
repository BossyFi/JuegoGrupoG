package dadm.scaffold.counter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import dadm.scaffold.BaseFragment;
import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;
import dadm.scaffold.database.Preferences;
import dadm.scaffold.sound.GameEvent;
import dadm.scaffold.sound.SoundManager;


public class MainMenuFragment extends BaseFragment implements View.OnClickListener, QuitDialog.QuitDialogListener, ChooseShipDialog.ChooseShipDialogListener {
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
        view.findViewById(R.id.btn_choose_ship).setOnClickListener(this);
        Animation pulseAnimation = AnimationUtils.
                loadAnimation(getActivity(), R.anim.button_pulse);
        view.findViewById(R.id.btn_start).startAnimation(
                pulseAnimation);
        Animation titleAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.title_enter);
        view.findViewById(R.id.GameTitle).startAnimation(titleAnimation);
        Animation subtitleAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.subtitle_enter);
        view.findViewById(R.id.GameSubtitle).startAnimation(subtitleAnimation);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            ((ScaffoldActivity) getActivity()).startGame();
            getScaffoldActivity().getSoundManager().playSoundForGameEvent(GameEvent.StartGame);
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
        } else if (v.getId() == R.id.btn_choose_ship) {
            showShipMenu();
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

    @Override
    public boolean onBackPressed() {
        boolean consumed = super.onBackPressed();
        if (!consumed) {
            QuitDialog quitDialog = new QuitDialog(getScaffoldActivity());
            quitDialog.setListener(this);
            showDialog(quitDialog);
        }
        return true;
    }

    private void showShipMenu() {
        ChooseShipDialog shipDialog = new ChooseShipDialog(getScaffoldActivity());
        shipDialog.setListener(this);
        showDialog(shipDialog);
    }

    @Override
    public void exit() {
        getScaffoldActivity().finish();
    }

    @Override
    public void chooseFirstShip() {
        Preferences.SetShipValue(getScaffoldActivity().getApplicationContext(), "PickedShip", R.drawable.ship_a);
        getScaffoldActivity().getSoundManager().playSoundForGameEvent(GameEvent.PickedShip);
    }

    @Override
    public void chooseSecondShip() {
        Preferences.SetShipValue(getScaffoldActivity().getApplicationContext(), "PickedShip", R.drawable.player2_a);
        getScaffoldActivity().getSoundManager().playSoundForGameEvent(GameEvent.PickedShip);
    }
}
