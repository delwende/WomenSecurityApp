
package com.tavant.droid.security.adapters;

import java.util.ArrayList;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tavant.droid.security.R;
import com.tavant.droid.security.database.ContentDescriptor;
import com.tavant.droid.security.database.ContentDescriptor.WSContact;
import com.tavant.droid.security.utils.FontLoader;
import com.tavant.droid.security.utils.Utils;
import com.tavant.droid.security.widget.CustomizableTextView;


public class ContactListAdapter extends 	android.support.v4.widget.CursorAdapter implements OnCheckedChangeListener,SectionIndexer{



	private ContentResolver mResolver = null; 
	private ContentValues values=null;

	private static final int TYPE_HEADER = 1;
	private static final int TYPE_NORMAL = 0;
	private static final int TYPE_COUNT = 2;
	private AlphabetIndexer indexer;
	private int[] usedSectionNumbers;
	private Map<Integer, Integer> sectionToOffset = null;
	private Map<Integer, Integer> sectionToPosition = null;
	private com.tavant.droid.security.adapters.ViewHolder adapter = null;
	private LayoutInflater mLayoutInflater;
	private int mNewPosition = -1;
	private TextView textView = null;
	private Cursor cur;
	private Uri muri;
	private ArrayList<String>miDs=null;
	private Typeface mTypeFace_bold = null;
	private Typeface mTypeFace_normal = null;



	public ContactListAdapter(Context context, Cursor c, AlphabetIndexer indexer,
			int[] usedSectionNumbers, Map<Integer, Integer> sectionToOffset,
			Map<Integer, Integer> sectionToPosition) {
		super(context, c,false);
		this.indexer = indexer;
		this.usedSectionNumbers = usedSectionNumbers;
		this.sectionToOffset = sectionToOffset;
		this.sectionToPosition = sectionToPosition;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResolver=context.getContentResolver();
		mTypeFace_bold=FontLoader.getMngr().getTfRobotBold();
		mTypeFace_normal	=FontLoader.getMngr().getTfRobotNormal();	
	}

	@Override
	public int getCount() {
		if (super.getCount() != 0) {
			return super.getCount() + usedSectionNumbers.length;
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (getItemViewType(position) == TYPE_NORMAL) {// we define this
			// function later
			return super.getItem(position- sectionToOffset.get(getSectionForPosition(position)) - 1);
		}
		return null;
	}


	@Override
	public int getPositionForSection(int section) {
		if (!sectionToOffset.containsKey(section)) {
			int i = 0;
			int maxLength = usedSectionNumbers.length;
			while (i < maxLength && section > usedSectionNumbers[i]) {
				i++;
			}
			if (i == maxLength)
				return getCount();
			return indexer.getPositionForSection(usedSectionNumbers[i])
					+ sectionToOffset.get(usedSectionNumbers[i]);
		}
		return indexer.getPositionForSection(section) + sectionToOffset.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		int i = 0;
		try{
			int maxLength = usedSectionNumbers.length;
			while (i < maxLength
					&& position >= sectionToPosition.get(usedSectionNumbers[i])) {
				i++;
			}
			return usedSectionNumbers[i - 1];
		}catch (Exception e) {
			return i;
		}
	}
	@Override
	public Object[] getSections() {
		return indexer.getSections();
	}
	@Override
	public int getItemViewType(int position) {
		try{
			if (position == getPositionForSection(getSectionForPosition(position))) {
				return TYPE_HEADER;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return TYPE_NORMAL;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int type = getItemViewType(position);
		if (type == TYPE_HEADER) {
			try{
				adapter = new com.tavant.droid.security.adapters.ViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.com_facebook_picker_list_section_header, null);
				textView = (TextView) convertView.findViewById(R.id.com_facebook_picker_list_section_header);
				textView.setTypeface(mTypeFace_bold);
				adapter.isTitle = true;
				convertView.setTag(adapter);
				textView.setText((String) getSections()[getSectionForPosition(position)]);
			}catch (Exception e) {}
			return convertView;
		}
		try{
			mNewPosition = position - sectionToOffset.get(getSectionForPosition(position))- 1;
		}catch (Exception e) {
			return convertView;
		}
		return super.getView(mNewPosition, convertView, parent);
	}

	// these two methods just disable the headers
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		if (getItemViewType(position) == TYPE_HEADER) {
			return false;
		}
		return true;
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View convertView = LayoutInflater.from(context).inflate(
				R.layout.list_row, null);
		adapter = getAdapter(convertView);
		convertView.setTag(adapter);
		return convertView;
	}

	private ViewHolder getAdapter(View convertView) {
		ViewHolder adapter = new ViewHolder();
		adapter.mName = (TextView) convertView.findViewById(R.id.fb_name);
		adapter.mAvathar = (ImageView) convertView.findViewById(R.id.com_facebook_image);
		adapter.mCheckBox=(CheckBox)convertView.findViewById(R.id.com_fb_checkbox);
		CustomizableTextView textview=new CustomizableTextView(mContext);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.RIGHT_OF, adapter.mAvathar.getId());
		lp.addRule(RelativeLayout.BELOW, adapter.mName.getId());
		//RelativeLayout layout=(RelativeLayout)mLayoutInflater.inflate(R.layout.list_row, null, false);
		lp.setMargins(getPixelValueFromDp(7) ,getPixelValueFromDp(10), 0, 0);
		((RelativeLayout)convertView).addView(textview,lp);
		adapter.mphone=textview;
		convertView.setTag(adapter);
		return adapter;
	}

	
	private int getPixelValueFromDp(int Dp){
		Resources r = mContext.getResources();
		int px = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_DIP,
		        Dp, 
		        r.getDisplayMetrics()
		);
		return px;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		String id;
		String uName;
		String imgurl;
		int selected=0;
		String phonenumber;

		adapter = (ViewHolder) view.getTag();
		if (adapter.isTitle) {
			view = null;
			adapter = null;
			view = mLayoutInflater.inflate(R.layout.list_row, null, false);
			adapter = getAdapter(view);
		}

		adapter.position = getCursor().getPosition();
		view.setTag(adapter);



		uName=cursor.getString(cursor.getColumnIndex(ContentDescriptor.WSContact.Cols.NAME));
		id=cursor.getString(cursor
				.getColumnIndex(ContentDescriptor.WSContact.Cols.CONTACTS_ID));
		selected=cursor.getInt(cursor
				.getColumnIndex(ContentDescriptor.WSContact.Cols.STATUS));
		imgurl=""+cursor.getString(cursor
				.getColumnIndex(ContentDescriptor.WSContact.Cols.IMGURL));
		phonenumber=""+cursor.getString(cursor
				.getColumnIndex(ContentDescriptor.WSContact.Cols.PHONE));
		Log.i("TAG","myphonenumber"+phonenumber);

		adapter.mName.setText(uName);
		adapter.mphone.setText(phonenumber);
		adapter.mName.setTypeface(mTypeFace_normal);
		adapter.mphone.setTypeface(mTypeFace_normal);
		adapter.mCheckBox.setOnCheckedChangeListener(null);
		adapter.mCheckBox.setChecked(selected==1 ? true:false );
		adapter.mCheckBox.setTag(id);
		adapter.mCheckBox.setOnCheckedChangeListener(this); 
		Utils.loadContactPhoto(context.getContentResolver(), Long.parseLong(id), adapter.mAvathar, null, 60, 60);
	}




	public void refresh(AlphabetIndexer indxr,int[] usdsecnum,Map<Integer, Integer> s2off,Map<Integer, Integer> sec2Pos) {
		try{
			indexer = indxr;
			usedSectionNumbers = usdsecnum;
			sectionToOffset = s2off;
			sectionToPosition = sec2Pos;
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		Log.i("TAG",""+"refreshing listview");
		String id=arg0.getTag().toString();
		values = new ContentValues();
		if (arg1) { 
			values.put(ContentDescriptor.WSContact.Cols.STATUS, 1);
			mResolver.update(ContentDescriptor.WSContact.CONTENT_URI, values,
					WSContact.Cols.CONTACTS_ID+ " = " + id + " ",
					null);
		} else {
			values.put(ContentDescriptor.WSContact.Cols.STATUS, 0);
			mResolver.update(ContentDescriptor.WSContact.CONTENT_URI, values,
					WSContact.Cols.CONTACTS_ID+ " = " + id + " ",
					null);
		}

	}

}
