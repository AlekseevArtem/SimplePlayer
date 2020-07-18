package ru.job4j.SimplePlayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private MediaPlayer mMedia = null;
    private RecyclerView mMediaRecyclerView;
    private Button mPlayOrPause;
    private int mCurrentSongIndex = 0;
    private List<MediaFile> mMusicFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMediaRecyclerView = findViewById(R.id.list_of_media);
        mMediaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateUI();
        mMedia = MediaPlayer.create(MainActivity.this, mMusicFiles.get(mCurrentSongIndex).getId());
        mMedia.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMedia.setOnCompletionListener(mp -> nextTrack());
        mPlayOrPause = findViewById(R.id.play_pause);
        mPlayOrPause.setOnClickListener(v -> playOrPause());
        Button previous = findViewById(R.id.previous);
        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> nextTrack());
        previous.setOnClickListener(v -> previousTrack());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSongIndex = savedInstanceState.getInt("song index");
        mMedia = MediaPlayer.create(MainActivity.this, mMusicFiles.get(mCurrentSongIndex).getId());
        mMedia.seekTo(savedInstanceState.getInt("song position"));
        if(savedInstanceState.getBoolean("plays")) {
            mMedia.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Intent svc = new Intent(this, BackgroundSoundService.class);
//        startService(svc);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMedia.stop();
        mMedia.release();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("song index", mCurrentSongIndex);
        outState.putInt("song position", mMedia.getCurrentPosition());
        outState.putBoolean("plays", mMedia.isPlaying());
    }

    private void updateUI() {
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++){
            Log.i("Raw Asset: ", fields[count].getName());
            int resourceID = 0;
            try {
                resourceID = fields[count].getInt(fields[count]);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String resourceName = fields[count].getName();
            MediaFile file = new MediaFile(resourceID,resourceName);
            mMusicFiles.add(file);
        }
        mMediaRecyclerView.setAdapter(new MediaFilesAdapter(mMusicFiles));
    }

    private void playOrPause() {
        if(mMedia.isPlaying()) {
            mPlayOrPause.setText("Play");
            try {
                mMedia.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mPlayOrPause.setText("Pause");
            try {
                mMedia.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void nextTrack() {
        mMedia.stop();
        mMedia.release();
        if(mCurrentSongIndex < (mMusicFiles.size() - 1)) {
            mMedia = MediaPlayer.create(MainActivity.this, mMusicFiles.get(++mCurrentSongIndex).getId());
        } else {
            mCurrentSongIndex = 0;
            mMedia = MediaPlayer.create(MainActivity.this, mMusicFiles.get(mCurrentSongIndex).getId());
        }
        playOrPause();
    }

    private void previousTrack() {
        mMedia.stop();
        mMedia.release();
        if(mCurrentSongIndex > 0) {
            mMedia = MediaPlayer.create(MainActivity.this, mMusicFiles.get(--mCurrentSongIndex).getId());
        } else {
            mCurrentSongIndex = mMusicFiles.size() - 1;
            mMedia = MediaPlayer.create(MainActivity.this, mMusicFiles.get(mCurrentSongIndex).getId());
        }
        playOrPause();
    }

    private class MediaFilesHolder extends RecyclerView.ViewHolder {
        private Button mNameButton;
        private MediaFile mMediaFile;

        public MediaFilesHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_media, parent,false));
            mNameButton = itemView.findViewById(R.id.media_name);
        }

        public void bind(MediaFile file) {
            mMediaFile = file;
            mNameButton.setText(mMediaFile.getName());
            mNameButton.setOnClickListener(this::clickOnNameButton);
        }

        private void clickOnNameButton(View view){
            if(mMedia.isPlaying()) {
                mMedia.stop();
            }
            mMedia = MediaPlayer.create(MainActivity.this, mMediaFile.getId());
            playOrPause();
        }
    }

    private class MediaFilesAdapter extends RecyclerView.Adapter<MediaFilesHolder> {
        private List<MediaFile> mMediaFiles;

        public MediaFilesAdapter(List<MediaFile> crimes) {
            mMediaFiles = crimes;
        }

        @NonNull
        @Override
        public MediaFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MediaFilesHolder(LayoutInflater.from(getApplicationContext()), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull MediaFilesHolder holder, int position) {
            holder.bind(mMediaFiles.get(position));
        }

        @Override
        public int getItemCount() {
            return mMediaFiles.size();
        }
    }
}