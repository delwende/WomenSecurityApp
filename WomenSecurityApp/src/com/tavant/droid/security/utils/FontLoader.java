
package com.tavant.droid.security.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

public class FontLoader {

	private static FontLoader mDataMngr = null;
	private Typeface tfRobotNormal = null;
	private Typeface tfRobotBold = null;

	public static FontLoader getMngr() {
		if (mDataMngr == null) {
			mDataMngr = new FontLoader();
		}
		return mDataMngr;
	}

	public void setTypeFont(AssetManager mngr) {
		tfRobotNormal = Typeface.createFromAsset(mngr,"fonts/Roboto-Regular.ttf");
		tfRobotBold = Typeface.createFromAsset(mngr,"fonts/Roboto-Bold.ttf");
	}

	public Typeface getTfRobotNormal() {
		return tfRobotNormal;
	}

	public Typeface getTfRobotBold() {
		return tfRobotBold;
	}
    
	
	

}
