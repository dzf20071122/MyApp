package com.research;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool.OnLoadCompleteListener;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class CallSound {
	private static MediaPlayer mMediaPlaer;
	private static int soundId;
	private static int soundName;

	public static void startCallSound(Context context){

		mMediaPlaer = MediaPlayer.create(context, R.raw.outgoing);
		mMediaPlaer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mMediaPlaer.start();
			}
		});
		mMediaPlaer.start();
	}

	public static void stopCallSound(){

		if (mMediaPlaer == null)
			return;
		try {
			mMediaPlaer.pause();
			mMediaPlaer.stop();
			mMediaPlaer.release();
			mMediaPlaer = null;
		} catch (Exception e) {
			Log.i("media-stop", "er");
		}
	}
}
