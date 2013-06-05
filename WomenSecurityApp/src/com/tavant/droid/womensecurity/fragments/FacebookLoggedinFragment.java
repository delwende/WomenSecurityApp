package com.tavant.droid.womensecurity.fragments;

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
	    return view;
	}	
	

}
