package com.tavant.droid.security.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.tavant.droid.security.HomeActivity;
import com.tavant.droid.security.R;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.utils.Utils;

public class SplashScreenActivity extends Activity implements View.OnClickListener {
	
	/**
	 * class for showing the 
	 */
	private CommonPreferences pref=null;
	private Handler handler=null;
	private ImageView close_image=null;
	private ViewFlipper viewFlipper;
    private float lastX;
    private FrameLayout help_view=null;
    private ProgressDialog dialog=null;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref=CommonPreferences.getInstance();
		pref.load(this);
		if(pref.isFirstTime()){
			setContentView(R.layout.splashscreen);
			inithelpView();
			handleHelpView();
		}else{
			startLoginScreen();
		}	
	}
	
	private void inithelpView(){
		ImageView  image1=(ImageView) findViewById(R.id.image_view1);
		ImageView image2=(ImageView) findViewById(R.id.image_view2);
		close_image=(ImageView)findViewById(R.id.close_btn);
		close_image.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});
		close_image.setOnClickListener(this);
		viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		Drawable h1 = getResources().getDrawable(R.drawable.help_1);
        Drawable h2 = getResources().getDrawable(R.drawable.help_2);
        h1.setAlpha(250);
        h2.setAlpha(250);
      // setting the images on the ImageViews
       image1.setBackgroundDrawable(h2);
       image2.setBackgroundDrawable(h1);
       help_view=(FrameLayout)findViewById(R.id.help_view);
       
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		/*
		if(help_view.isClickable())
			viewFlipper.showNext();
			*/
	}
	
	private void handleSplash(){
		handler=new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				pref.setFirstTime(false);
				dialog.dismiss();
				startLoginScreen();
			}
		}, 2000L);
	}
	
	private void handleHelpView(){
		handler=new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				help_view.setVisibility(View.VISIBLE);
			}
		}, 3000L);
	}
	
	private void startLoginScreen() {
		Intent intent=new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	// Method to handle touch event like left to right swap and right to left swap
    public boolean onTouchEvent(MotionEvent touchevent) 
    {
                 switch (touchevent.getAction())
                 {
                        // when user first touches the screen to swap
                         case MotionEvent.ACTION_DOWN: 
                         {
                             lastX = touchevent.getX();
                             break;
                        }
                         case MotionEvent.ACTION_UP: 
                         {
                             float currentX = touchevent.getX();
                             
                             // if left to right swipe on screen
                             if (lastX < currentX) 
                             {
                                  // If no more View/Child to flip
                                 if (viewFlipper.getDisplayedChild() == 0)
                                     break;
                                 
                                 // set the required Animation type to ViewFlipper
                                 // The Next screen will come in form Left and current Screen will go OUT from Right 
                                 viewFlipper.setInAnimation(this, R.anim.in_from_left);
                                 viewFlipper.setOutAnimation(this, R.anim.out_to_right);
                                 // Show the next Screen
                                 viewFlipper.showNext();
                             }
                             
                             // if right to left swipe on screen
                             if (lastX > currentX)
                             {
                                 if (viewFlipper.getDisplayedChild() == 1)
                                     break;
                                 // set the required Animation type to ViewFlipper
                                 // The Next screen will come in form Right and current Screen will go OUT from Left 
                                 viewFlipper.setInAnimation(this, R.anim.in_from_right);
                                 viewFlipper.setOutAnimation(this, R.anim.out_to_left);
                                 // Show The Previous Screen
                                 viewFlipper.showPrevious();
                             }
                             break;
                         }
                 }
                 return false;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.close_btn){
			dialog=new ProgressDialog(SplashScreenActivity.this);
			//dialog=ProgressDialog.show(SplashScreenActivity.this, getString(R.string.app_name), getString(R.string.loading));
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setMessage(getString(R.string.loading));
			dialog.show();
			help_view.setVisibility(View.GONE);
			handleSplash();
		}
	}

}
