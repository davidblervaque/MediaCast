package mediacast.com.mediacast;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

import mediacast.com.mediacast.Models.Music;

/**
 * Created by David on 29/02/2016.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {


    private MediaPlayer mPlayer;
    private ArrayList<Music> mMusics;
    private int mSongPosition;
    private final IBinder mBinder = new MusicBinder();
    private String musicTitle = "";
    private static final int NOTIFY_ID = 1;
    private boolean mShuffle = false;
    private Random mRandom;

    public void onCreate() {
        //creation of the service
        super.onCreate();
        mSongPosition = 0;
        mPlayer = new MediaPlayer();
        mRandom = new Random();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        //initialize the player with properties
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    public void setShuffle() {
        if (mShuffle) {
            mShuffle = false;
        }
        else {
            mShuffle = true;
        }
    }

    public void setList(ArrayList<Music> musics) {
        mMusics = musics;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        mPlayer.reset();
        Music playableMusic = mMusics.get(mSongPosition);
        musicTitle = playableMusic.getTitle();
        long musicId = playableMusic.getId();
        Uri musicUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicId);
        try {
            mPlayer.setDataSource(getApplicationContext(), musicUri);
        }
        catch (Exception ex) {
            Log.e("MUSIC SERVICE", "Error while setting data source", ex);
        }
        mPlayer.prepareAsync();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.stop();
        mPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mPlayer.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_play_button)
                .setTicker(musicTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(musicTitle);
        Notification notification = builder.build();
        startForeground(NOTIFY_ID, notification);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void setMusic(int musicPos) {
        mSongPosition = musicPos;
    }

    public int getMusicPosition() {
        return mPlayer.getCurrentPosition();
    }

    public int getMusicDuration() {
        return mPlayer.getDuration();
    }

    public boolean isMusicPlaying() {
        return mPlayer.isPlaying();
    }

    public void pausePlayer() {
        mPlayer.pause();
    }

    public void seek(int position) {
        mPlayer.seekTo(position);
    }

    public void go() {
        mPlayer.start();
    }

    public void playPrev() {
        mSongPosition--;
        if (mSongPosition < 0) {
            mSongPosition = mMusics.size() - 1;
        }
        playSong();
    }

    public void playNext() {
        if (mShuffle) {
            int newMusic = mSongPosition;
            while (newMusic == mSongPosition) {
                newMusic = mRandom.nextInt(mMusics.size());
            }
            mSongPosition = newMusic;
        }
        else {
            mSongPosition++;
            if (mSongPosition > mMusics.size()) {
                mSongPosition = 0;
            }
        }
        playSong();
    }
}
