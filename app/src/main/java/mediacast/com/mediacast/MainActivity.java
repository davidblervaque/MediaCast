package mediacast.com.mediacast;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController.MediaPlayerControl;

import com.activeandroid.ActiveAndroid;
import mediacast.com.mediacast.MusicService.MusicBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mediacast.com.mediacast.Models.Music;

//TODO : L'application est à faire

public class MainActivity extends Activity implements MediaPlayerControl {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    private ArrayList<Music> mMusics; //TODO : Build the music list
    private RecyclerView mMusicView;

    private MusicService mMusicService;
    private Intent mIntent;
    private boolean mMusicBound = false;

    private MusicController mMusicController;

    private String mMusicTitle;
    private static final int NOTIFY_ID = 1;

    private boolean paused = false;
    private boolean playbackPaused = false;

    // Connection to the service MusicService
    private ServiceConnection mMusicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            mMusicService = binder.getService();
            mMusicService.setList(mMusics);
            mMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //nvDrawer = (NavigationView) findViewById(R.id.nvView);
        //setupDrawerContent(nvDrawer);

        mMusicView = (RecyclerView)findViewById(R.id.music_list);
        mMusicView.setHasFixedSize(true);
        mMusics = new ArrayList<Music>();
        getSongList();

        Collections.sort(mMusics, new Comparator<Music>() {
            @Override
            public int compare(Music lhs, Music rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });

        MusicAdapter musicAdapter = new MusicAdapter(this, mMusics);
        mMusicView.setAdapter(musicAdapter);
        mMusicView.setLayoutManager(new LinearLayoutManager(this));

        setmMusicController();

        if (mIntent == null) {
            mIntent = new Intent(this, MusicService.class);
            bindService(mIntent, mMusicConnection, Context.BIND_AUTO_CREATE);
            startService(mIntent);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.home_fragment:
                Log.d("CLICK", "Home clicked");
                //fragmentClass = FirstFragment.class;
                break;
            case R.id.artists:
                Log.d("CLICK", "artists clicked");
                //fragmentClass = SecondFragment.class;
                break;
            case R.id.albums:
                Log.d("CLICK", "albums clicked");
                //fragmentClass = ThridFragment.class;
                break;
            case R.id.videos:
                Log.d("CLICK", "videos clicked");
                //fragmentClass = FourthFragment.class;
                break;
            case R.id.all_music:
                Log.d("CLICK", "all music clicked");
                //fragmentClass = FifthFragment.class;
                break;
            default:
                Log.d("CLICK", "default clicked");
                //fragmentClass = FirstFragment.class;
        }

        /*try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        //menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            setmMusicController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        mMusicController.hide();
        super.onStop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {
                long Id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                mMusics.add(new Music(Id, title, artist));
            } while (musicCursor.moveToNext());
        }
    }

    public void musicPicked(View view) {
        System.out.println();
        mMusicService.setMusic(Integer.parseInt(view.getTag().toString()));
        mMusicService.playSong();
        if (playbackPaused) {
            setmMusicController();
            playbackPaused = false;
        }
        mMusicController.show(0);
    }

    private void playNext() {
        mMusicService.playNext();
        if (playbackPaused) {
            setmMusicController();
            playbackPaused = false;
        }
        mMusicController.show(0);
    }

    private void playPrev() {
        mMusicService.playPrev();
        if (playbackPaused) {
            setmMusicController();
            playbackPaused = false;
        }
        mMusicController.show(0);
    }

    private void setmMusicController() {
        mMusicController = new MusicController(this);
        mMusicController.setPrevNextListeners(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        mMusicController.setMediaPlayer(this);
        mMusicController.setAnchorView(findViewById(R.id.music_list));
        mMusicController.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        stopService(mIntent);
        mMusicService = null;
        super.onDestroy();
    }

    @Override
    public void start() {
        mMusicService.go();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        mMusicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (mMusicService != null && mMusicBound && mMusicService.isMusicPlaying()) {
            return mMusicService.getMusicDuration();
        }
        else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (mMusicService != null && mMusicBound && mMusicService.isMusicPlaying()){
            return mMusicService.getMusicPosition();
        }
        else {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        mMusicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (mMusicService != null && mMusicBound) {
            return mMusicService.isMusicPlaying();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
