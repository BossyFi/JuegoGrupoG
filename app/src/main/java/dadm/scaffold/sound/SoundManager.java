package dadm.scaffold.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import java.io.IOException;
import java.util.HashMap;

import dadm.scaffold.database.Preferences;

public final class SoundManager {
    public String SOUNDS_PREF_KEY = "Sound";
    public String MUSIC_PREF_KEY = "Music";
    private static final int MAX_STREAMS = 10;
    private static final float DEFAULT_MUSIC_VOLUME = 0.6f;
    private boolean soundEnabled;
    private boolean musicEnabled;
    private HashMap<GameEvent, Integer> soundsMap;

    private Context context;
    private SoundPool soundPool;
    private MediaPlayer bgPlayer;

    public SoundManager(Context context) {
        this.context = context;
        soundEnabled = Preferences.GetBooleanValue(this.context, SOUNDS_PREF_KEY);
        musicEnabled = Preferences.GetBooleanValue(this.context, MUSIC_PREF_KEY);
        loadIfNeeded();
    }

    private void loadEventSound(Context context, GameEvent event, String... filename) {
        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd("sfx/" + filename[0]);
            int soundId = soundPool.load(descriptor, 1);
            soundsMap.put(event, soundId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playSoundForGameEvent(GameEvent event) {
        if (!soundEnabled) return;

        Integer soundId = soundsMap.get(event);
        if (soundId != null) {
            // Left Volume, Right Volume, priority (0 == lowest), loop (0 == no) and rate (1.0 normal playback rate)
            soundPool.play(soundId, 0.5f, 0.5f, 0, 0, 1.0f);
        }
    }

    private void loadSounds() {
        createSoundPool();
        soundsMap = new HashMap<GameEvent, Integer>();
        loadEventSound(context, GameEvent.AsteroidHit, "Asteroid_explosion_1.wav");
        loadEventSound(context, GameEvent.SpaceshipHit, "Spaceship_explosion.wav");
        loadEventSound(context, GameEvent.LaserFired, "Laser_shoot.wav");
        loadEventSound(context, GameEvent.EnemyLaser, "enemyLaser.wav");
        loadEventSound(context, GameEvent.PowerUp, "powerUp2.wav");
    }

    private void loadMusic() {
        try {
            // Important to not reuse it. It can be on a strange state
            bgPlayer = new MediaPlayer();
            AssetFileDescriptor afd = context.getAssets().openFd("sfx/musica_menu.mp3");
            bgPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(), afd.getLength());
            bgPlayer.setLooping(true);
            bgPlayer.setVolume(DEFAULT_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME);
            bgPlayer.prepare();
            bgPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSoundPool() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        } else {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(MAX_STREAMS)
                    .build();
        }
    }

    private void unloadSounds() {
        soundPool.release();
        soundPool = null;
        soundsMap.clear();
    }

    private void unloadMusic() {
        bgPlayer.stop();
        bgPlayer.release();
    }

    public void pauseBgMusic() {
        if (musicEnabled) {
            bgPlayer.pause();
        }
    }

    public void resumeBgMusic() {
        if (musicEnabled) {
            bgPlayer.start();
        }
    }

    public boolean getSoundStatus() {
        return soundEnabled;
    }

    public boolean getMusicStatus() {
        return musicEnabled;
    }

    private void loadIfNeeded() {
        if (soundEnabled) {
            loadSounds();
        }
        if (musicEnabled) {
            loadMusic();
        }
    }

    public void toggleSoundStatus() {
        soundEnabled = !soundEnabled;
        if (soundEnabled) {
            loadSounds();
        } else {
            unloadSounds();
        }
        // Save it to preferences
        Preferences.SetBooleanValue(this.context, SOUNDS_PREF_KEY, soundEnabled);
    }

    public void toggleMusicStatus() {
        musicEnabled = !musicEnabled;
        if (musicEnabled) {
            loadMusic();
            resumeBgMusic();
        } else {
            unloadMusic();
        }
        // Save it to preferences
        Preferences.SetBooleanValue(this.context, MUSIC_PREF_KEY, musicEnabled);
    }
}
