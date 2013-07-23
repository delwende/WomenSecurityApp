
package com.glaesis.droid.security.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.glaesis.droid.security.R;
import com.glaesis.droid.security.database.ContentDescriptor;







public class FbFriendsAdapter extends CursorAdapter implements OnCheckedChangeListener{
	
	
	private LayoutInflater mLayoutInflater;
	private ContentResolver mResolver = null; 
	private String imgurl;
	private String uName=null;
	private int selected=0;
	private int id=-1;
    private ContentValues values=null;
	public FbFriendsAdapter(Context context, Cursor c) {
		super(context, c, true);
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResolver=context.getContentResolver();
	}



	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	
	
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View convertView;
		convertView = LayoutInflater.from(context).inflate(
					R.layout.list_row, null);
		return convertView;
	}
	
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		id=cursor.getInt(cursor
				.getColumnIndex(BaseColumns._ID));
		
		imgurl= cursor.getString(cursor
				.getColumnIndex(ContentDescriptor.WSFacebook.Cols.IMGURL));
		
		uName= cursor.getString(cursor
				.getColumnIndex(ContentDescriptor.WSFacebook.Cols.FBNAME));
		selected=cursor.getInt(cursor
				.getColumnIndex(ContentDescriptor.WSFacebook.Cols.FBSTATUS));
		
		
		TextView name = (TextView) view
				.findViewById(R.id.fb_name);
		name.setText(uName);
		ImageView img=(ImageView)view.findViewById(R.id.com_facebook_image);
		img.setTag(imgurl);
		CheckBox mcheckbox = (CheckBox) view
				.findViewById(R.id.com_fb_checkbox);

		mcheckbox.setChecked(selected==1 ? true:false );
		mcheckbox.setTag(id);
		mcheckbox.setOnCheckedChangeListener(this);
	}
	
	
	
	
	public void refresh(){
		try{
			getCursor().requery();
			notifyDataSetChanged();
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
					//getCursor().requery();
					//notifyDataSetChanged();
				
			} else {
				values.put(ContentDescriptor.WSFacebook.Cols.FBSTATUS, 0);
				mResolver.update(ContentDescriptor.WSFacebook.CONTENT_URI, values,
						BaseColumns._ID+ " = " + _id + " ",
						null);
				//getCursor().requery();
				//notifyDataSetChanged();
			}
	}

}
