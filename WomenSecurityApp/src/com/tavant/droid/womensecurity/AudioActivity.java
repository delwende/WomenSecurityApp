package com.tavant.droid.womensecurity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.tavant.droid.sound.PlayAudio;

public class AudioActivity extends Activity {

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_audio);
	    }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }
	    
	    public void playAudio(View view) {
		    Intent objIntent = new Intent(this, PlayAudio.class);
		    startService(objIntent);
	    }
	    
	    public void stopAudio(View view) {
	    	Intent objIntent = new Intent(this, PlayAudio.class);
		    stopService(objIntent);    
	    }
	}
