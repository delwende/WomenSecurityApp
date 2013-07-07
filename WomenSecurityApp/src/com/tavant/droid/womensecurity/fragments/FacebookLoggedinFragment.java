package com.tavant.droid.womensecurity.fragments;

import java.util.Arrays;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tavant.droid.womensecurity.R;



public class FacebookLoggedinFragment extends Fragment {
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.facebook_loggedout, 
	            container, false);
	    com.facebook.widget.LoginButton authButton = ( com.facebook.widget.LoginButton) view.findViewById(R.id.login_button);
	  //  authButton.setFragment(this);
	    //authButton.setBackgroundColor(Color.RED);
	    authButton.setReadPermissions(Arrays.asList("xmpp_login", "user_online_presence","friends_online_presence")); 
	    return view;
	}	
	

}
