
package com.tavant.droid.security.adapters;

import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tavant.droid.security.R;
import com.tavant.droid.security.database.ContentDescriptor;







public class FbFriendsAdapter extends 	android.support.v4.widget.CursorAdapter implements OnCheckedChangeListener,SectionIndexer{
	
	
	
	private ContentResolver mResolver = null; 
	private String imgurl;
	private String uName=null;
	private int selected=0;
	private int id=-1;
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
    
   
    
	public FbFriendsAdapter(Context context, Cursor c, AlphabetIndexer indexer,
			int[] usedSectionNumbers, Map<Integer, Integer> sectionToOffset,
			Map<Integer, Integer> sectionToPosition) {
		super(context, c,false);
		this.indexer = indexer;
		this.usedSectionNumbers = usedSectionNumbers;
		this.sectionToOffset = sectionToOffset;
		this.sectionToPosition = sectionToPosition;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResolver=context.getContentResolver();
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
		convertView.setTag(adapter);
		return adapter;
	}
	
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		adapter = (ViewHolder) view.getTag();
		if (adapter.isTitle) {
			view = null;
			adapter = null;
			view = mLayoutInflater.inflate(R.layout.list_row, null, false);
			adapter = getAdapter(view);
		}

		adapter.position = getCursor().getPosition();
		view.setTag(adapter);
		
		
		
		String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
		String uName = cursor.getString(cursor.getColumnIndex(ContentDescriptor.WSFacebook.Cols.FBNAME));
		String imgurl = cursor.getString(cursor.getColumnIndex(ContentDescriptor.WSFacebook.Cols.IMGURL));
		int selected=cursor.getInt(cursor
				.getColumnIndex(ContentDescriptor.WSFacebook.Cols.FBSTATUS));
		
		
		adapter.mName.setText(uName);
		adapter.mCheckBox.setChecked(selected==1 ? true:false );
		adapter.mCheckBox.setTag(id);
		adapter.mCheckBox.setOnCheckedChangeListener(this); 
		adapter.mAvathar.setTag(R.string.ImageUrl, imgurl);
		ImageView img=(ImageView)view.findViewById(R.id.com_facebook_image);
		img.setTag(imgurl);
		ImageLoader.getInstance().DisplayImage(imgurl,
				adapter.mAvathar.getContext(), adapter.mAvathar,null);
				
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
		Integer _id = new Integer(arg0.getTag().toString());
			values = new ContentValues();
			if (arg1) { 
					values.put(ContentDescriptor.WSFacebook.Cols.FBSTATUS, 1);
					mResolver.update(ContentDescriptor.WSFacebook.CONTENT_URI, values,
							BaseColumns._ID+ " = " + _id + " ",
							null);
				
			} else {
				values.put(ContentDescriptor.WSFacebook.Cols.FBSTATUS, 0);
				mResolver.update(ContentDescriptor.WSFacebook.CONTENT_URI, values,
						BaseColumns._ID+ " = " + _id + " ",
						null);
			}
	 }


	
}
