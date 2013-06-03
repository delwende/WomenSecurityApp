package com.tavant.droid.womensecurity.activities;

import java.util.HashMap;
import java.util.Map;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tavant.droid.womensecurity.BuildConfig;
import com.tavant.droid.womensecurity.R;
import com.tavant.droid.womensecurity.database.ContentDescriptor;
import com.tavant.droid.womensecurity.fragments.ContactsListFragment;
import com.tavant.droid.womensecurity.utils.PhoneUtils;
import com.tavant.droid.womensecurity.utils.Utils;

public class FetchContactsActivity extends FragmentActivity implements
		ContactsListFragment.OnContactsInteractionListener, OnClickListener {

	// Defines a tag for identifying log entries
	private static final String TAG = "ContactsListActivity";

	// If true, this is a larger screen device which fits two panes
	// private boolean isTwoPaneLayout;

	// True if this activity instance is a search result view (used on pre-HC
	// devices that load
	// search results in a separate instance of the activity rather than loading
	// results in-line
	// as the query is typed.
	private boolean isSearchResultView = false;
	private Map<String, String> contactsMap = new HashMap<String, String>();

	ContentResolver contentResolver;
	Cursor cur;
	Button cancelBtn, saveBtn;
	LinearLayout buttonLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Utils.enableStrictMode();
		}
		super.onCreate(savedInstanceState);

		// Set main content view. On smaller screen devices this is a single
		// pane view with one
		// fragment. One larger screen devices this is a two pane view with two
		// fragments.
		setContentView(R.layout.contacts_listview);

		cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		saveBtn = (Button) findViewById(R.id.save_button);
		saveBtn.setOnClickListener(this);
		buttonLayout = (LinearLayout) findViewById(R.id.button_layout);

		// get content resolver used to manage registered content providers
		contentResolver = this.getContentResolver();

		// Check if this activity instance has been triggered as a result of a
		// search query. This
		// will only happen on pre-HC OS versions as from HC onward search is
		// carried out using
		// an ActionBar SearchView which carries out the search in-line without
		// loading a new
		// Activity.
		if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

			// Fetch query from intent and notify the fragment that it should
			// display search
			// results instead of all contacts.
			String searchQuery = getIntent()
					.getStringExtra(SearchManager.QUERY);
			ContactsListFragment mContactsListFragment = (ContactsListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.contact_list);

			// This flag notes that the Activity is doing a search, and so the
			// result will be
			// search results rather than all contacts. This prevents the
			// Activity and Fragment
			// from trying to a search on search results.
			isSearchResultView = true;
			mContactsListFragment.setSearchQuery(searchQuery);

			// Set special title for search results
			String title = getString(
					R.string.contacts_list_search_results_title, searchQuery);
			setTitle(title);
		}
	}

	/**
	 * This interface callback lets the main contacts list fragment notify this
	 * activity that a contact has been selected.
	 * 
	 * @param contactUri
	 *            The contact Uri to the selected contact.
	 */
	@Override
	public void onContactSelected(Uri contactUri) {

		//System.out.println(TAG+ "onContactSelected >>>  " + contactUri);
		//System.out.println("PHONE NUMBER: "	+ PhoneUtils.getContactPhoneNumber(this,contactUri.getLastPathSegment()));
		//System.out.println("DISPLAY NAME: "	+ PhoneUtils.getDisplayName(this,contactUri.getLastPathSegment()));
		String phoneNumber = PhoneUtils.getContactPhoneNumber(this,
				contactUri.getLastPathSegment());
		String displayName = PhoneUtils.getDisplayName(this,
				phoneNumber);
		contactsMap.put(phoneNumber, displayName);
		if (buttonLayout.getVisibility() == View.INVISIBLE) {
			buttonLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * This interface callback lets the main contacts list fragment notify this
	 * activity that a contact is no longer selected.
	 */
	@Override
	public void onSelectionCleared() {
		// should clear the unselected one. for now just clearing the complete
		// contacts map
		contactsMap.clear();
	}

	@Override
	public boolean onSearchRequested() {
		// Don't allow another search if this activity instance is already
		// showing
		// search results. Only used pre-HC.
		return !isSearchResultView && super.onSearchRequested();
	}

	private void saveContent() {

		for (Map.Entry<String, String> entry : contactsMap.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : "
					+ entry.getValue());
			ContentValues val = new ContentValues();
			val.put(ContentDescriptor.WSContact.Cols.NAME, (entry.getValue()));
			val.put(ContentDescriptor.WSContact.Cols.PHONE, (entry.getKey()));
			contentResolver
					.insert(ContentDescriptor.WSContact.CONTENT_URI, val);
			System.out.println("Content saved.." + entry.getValue() + "and"
					+ entry.getKey());
		}

		Toast.makeText(FetchContactsActivity.this, "Contacts saved !",
				Toast.LENGTH_SHORT).show();
		contactsMap.clear();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.cancel_button:
			FetchContactsActivity.this.finish();
			break;

		case R.id.save_button:
			saveContent();
			FetchContactsActivity.this.finish();
			break;

		}

	}
}
