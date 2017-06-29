package com.example.test;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceView;

public class MovieRecorder {
	private MediaRecorder mediarecorder;
	boolean isRecording;
	String path;
	Timer timer;
	int timeSize = 0;
	private String lastFileName;
	
	public MovieRecorder (){
		creatFile();
	}

	public void startRecording(SurfaceView surfaceView) {
		creatFile();
		mediarecorder = new MediaRecorder();
		mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mediarecorder.setVideoSize(176, 144);
		mediarecorder.setVideoFrameRate(20);
		mediarecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
		lastFileName = newFileName();
		
		mediarecorder.setOutputFile(lastFileName);
		try {
			mediarecorder.prepare();
			mediarecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isRecording = true;
		timeSize = 0;
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				timeSize++;
			}
		}, 0,1000);
	}

	

	public void stopRecording() {
		if (mediarecorder != null) {
			mediarecorder.stop();
			mediarecorder.release();
			mediarecorder = null;

			timer.cancel();
			if (null != lastFileName && !"".equals(lastFileName)) {
				File f = new File(lastFileName);
				String name = f.getName().substring(0,
						f.getName().lastIndexOf(".mp4"));
				name += "_" + timeSize + "s.mp4";
				String newPath = Environment.getExternalStorageDirectory() + "/mRecorder/video/"
						+ name;
				if (f.renameTo(new File(newPath))) {
					int i = 0;
					i++;
				}
				path = lastFileName;
			}
		}
	}
	
	public static void creatFile(){
		String mkdir = Environment.getExternalStorageDirectory() + "/mRecorder/video";
		File file = new File(mkdir);
		if(!file.exists()){
		file.mkdirs();
		}
	}

	public String newFileName() {
		try {
			String path = Environment.getExternalStorageDirectory() + "/mRecorder/video";
			File path1 = new File(path);
			return File.createTempFile("/mov_", ".mp4",path1).getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getLastFileName(){
		return lastFileName;
	}

	public void release() {
		if (mediarecorder != null) {
			mediarecorder.stop();
			mediarecorder.release();
			mediarecorder = null;
		}
	}
}
