package com.tavant.droid.security.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.tavant.droid.security.R;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.service.WSContactUpdateManger;

public class SplashScreenActivity extends Activity implements View.OnClickListener, AnimationListener {

	/**
	 * class for showing the 
	 */
	private CommonPreferences pref=null;
	private Handler handler=null;
	private ImageView close_image=null;
	private ImageView arr_img_right=null;
	private ImageView arr_img_left=null;
	private ViewFlipper viewFlipper;
	private float lastX;
	private FrameLayout help_view=null;
	private ProgressDialog dialog=null;
	private Animation infromright=null;
	private Animation outtoleft=null;
	private Animation infromleft=null;
	private Animation outtoright=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, WSContactUpdateManger.class));
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
		infromright=AnimationUtils.loadAnimation(this, R.anim.in_from_right);
		outtoleft=AnimationUtils.loadAnimation(this, R.anim.out_to_left);
		outtoleft.setAnimationListener(this);
		infromleft=AnimationUtils.loadAnimation(this, R.anim.in_from_left);
		outtoright=AnimationUtils.loadAnimation(this, R.anim.out_to_right);
		outtoright.setAnimationListener(this);
		ImageView  image1=(ImageView) findViewById(R.id.image_view1);
		ImageView image2=(ImageView) findViewById(R.id.image_view2);
		close_image=(ImageView)findViewById(R.id.close_btn);
		arr_img_right=(ImageView)findViewById(R.id.arrow_btn_right);
		arr_img_left=(ImageView)findViewById(R.id.arrow_btn_left);
		close_image.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});
		close_image.setOnClickListener(this);
		arr_img_right.setOnClickListener(this);
		arr_img_left.setOnClickListener(this);
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
				// viewFlipper.setInAnimation(this, R.anim.in_from_left);
				//viewFlipper.setOutAnimation(this, R.anim.out_to_right);
				viewFlipper.setInAnimation(infromleft);
				viewFlipper.setOutAnimation(outtoright);
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
				//  viewFlipper.setInAnimation(this, R.anim.in_from_right);
				// viewFlipper.setOutAnimation(this, R.anim.out_to_left);
				viewFlipper.setInAnimation(infromright);
				viewFlipper.setOutAnimation(outtoleft);
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
		}else if(v.getId()==R.id.arrow_btn_right){    // if right to left swipe on screen
			viewFlipper.setInAnimation(infromright);
			viewFlipper.setOutAnimation(outtoleft);
			// Show The Previous Screen
			viewFlipper.showPrevious();
		}else if(v.getId()==R.id.arrow_btn_left){
			viewFlipper.setInAnimation(infromleft);
			viewFlipper.setOutAnimation(outtoright);
			// Show the next Screen
			viewFlipper.showNext();

		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(animation.equals(outtoright))
		{
			arr_img_right.setVisibility(View.VISIBLE);
			arr_img_left.setVisibility(View.INVISIBLE);
		}else if(animation.equals(outtoleft)){
			arr_img_right.setVisibility(View.INVISIBLE);
			arr_img_left.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {


	}

	@Override
	public void onAnimationStart(Animation animation) {


	}

}
