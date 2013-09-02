package com.tavant.droid.security.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.tavant.droid.security.R;


/**
 * 
 * @author Tavant Technologies
 *  
 *  Class for playing siren.Can set repeat count
 */

public class ScreamPlayer implements OnCompletionListener {

	private Context ctx;
	private MediaPlayer mMediaplPlayer = null;
	private int mCount = 1;
	private int playCount = 0;
	private AudioManager mauAudioManager;
	private int presentstreamvolume=0; 
	public ScreamPlayer(Context mctx) {
		this.ctx = mctx;
		mauAudioManager=(AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		presentstreamvolume=mauAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);	
	}
	
	public void setRepeatCount(int count){
		this.mCount = count;
	}	
		
	public void startRinging() {
		playCount = 0;
		if (mMediaplPlayer != null) {
			try {
				if (mMediaplPlayer.isPlaying()){
					mMediaplPlayer.stop();
				}
				mMediaplPlayer.reset();
				mMediaplPlayer.release();
				mMediaplPlayer = null;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				mauAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
			    mauAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,presentstreamvolume , 0);	
			}
		}
		mauAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
	    mauAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mauAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0); 
		mMediaplPlayer = MediaPlayer.create(ctx, R.raw.siren);
		try {
			if(!mMediaplPlayer.isPlaying()){
				mMediaplPlayer.start();
			}
			mMediaplPlayer.setOnCompletionListener(this);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mauAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
		    mauAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,presentstreamvolume , 0);
			mMediaplPlayer = null;
			return;
		}
	}

	public void stopRinging() {
		if (mMediaplPlayer == null)
			return;
		try {
			if (mMediaplPlayer.isPlaying())
				mMediaplPlayer.stop();
			mMediaplPlayer.release();
			mMediaplPlayer = null;

		} catch (IllegalStateException e) {
			e.printStackTrace();
			mMediaplPlayer = null;
			mauAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
		    mauAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,presentstreamvolume , 0);
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		
		if (mp == null)
			return;
		try {
			playCount++;
			if(playCount < mCount){
				mp.start();
			}
			else
			{
				stopRinging();
				mauAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
			    mauAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,presentstreamvolume , 0); 
				
			}
		} catch (IllegalStateException e) {
			mMediaplPlayer = null;
			e.printStackTrace();
			mauAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
		    mauAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,presentstreamvolume , 0); 
		}
	}

}
