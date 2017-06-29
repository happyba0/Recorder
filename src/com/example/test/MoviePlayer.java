package com.example.test;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.SurfaceView;

public class MoviePlayer {
	public MediaPlayer mPlayer0;
	boolean isPlaying;
	String filePath;


	public MoviePlayer() {
		super();
	}

	public void play(String fileName, SurfaceView view) {
		mPlayer0 = new MediaPlayer();
		mPlayer0.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer0.setDisplay(view.getHolder());
		mPlayer0.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				stop();
			}
		});

		try {
			filePath = fileName;
			mPlayer0.setDataSource(fileName);
			mPlayer0.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isPlaying = true;
		mPlayer0.start();
	}
	
	public MediaPlayer getMediaPlayer(){
		return mPlayer0;
	}
	
	public void stop() {
		if (mPlayer0 != null) {
			mPlayer0.release();
			mPlayer0 = null;
		}
		isPlaying = false;
	}
	
	public void release()
	{
		if (mPlayer0 != null) {
			mPlayer0.release();
			mPlayer0 = null;
		}
		isPlaying = false;
	}
}
