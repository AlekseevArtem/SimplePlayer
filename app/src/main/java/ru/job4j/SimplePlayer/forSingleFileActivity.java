package ru.job4j.SimplePlayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class forSingleFileActivity extends AppCompatActivity {
    private Button mPlayOrPause;
    private Button mStop;
    private MediaPlayer mMedia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_single_file);
        mMedia = MediaPlayer.create(this, getIntent().getData());
        mPlayOrPause = findViewById(R.id.play_pause_single);
        mPlayOrPause.setOnClickListener(this::playOrPause);
        mStop = findViewById(R.id.stop_single);
        mStop.setOnClickListener(v -> finish());
        playOrPause(getCurrentFocus());
    }

    private void playOrPause(View view) {
        if(mMedia.isPlaying()) {
            mPlayOrPause.setText(R.string.play);
            try {
                mMedia.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mPlayOrPause.setText(R.string.pause);
            try {
                mMedia.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMedia.stop();
        mMedia.release();
    }
}
