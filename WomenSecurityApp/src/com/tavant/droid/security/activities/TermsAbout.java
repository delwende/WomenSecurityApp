
package com.tavant.droid.security.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.tavant.droid.security.R;
import com.tavant.droid.security.utils.WSConstants;

public class TermsAbout extends ActionBarActivity{





	private WebView mTermsWeb = null;
	private TextView mTermsTitle = null;
	private static String mVersion = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		initTerms();
		mTermsWeb.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
			@Override
			public void onPageStarted(WebView view, String url,Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});
		mTermsWeb.loadUrl("file:///android_asset/gladio_terms&conditions.html");
		//mTermsWeb.loadUrl("http://code.vivox.com/bobsled/policy/Vivox_T-Mobile_EULA.html");
	}

	private void initTerms(){
		mTermsTitle =(TextView)findViewById(R.id.TermsTitle);
		mTermsWeb =(WebView)findViewById(R.id.termsWebview);
	}

}
