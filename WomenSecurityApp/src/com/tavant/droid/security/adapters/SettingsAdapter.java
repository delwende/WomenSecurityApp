package com.tavant.droid.security.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaRouter.VolumeCallback;
import android.support.v7.appcompat.R.bool;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tavant.droid.security.R;
import com.tavant.droid.security.activities.FacebookFriendPicker;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.utils.VolunteerStatus;

public class SettingsAdapter extends BaseAdapter implements  OnCheckedChangeListener{


	private  int mdesc[] =null;
	private  int mtitle[]=null;
	private int mdesclong[]=null;
	private Context mctx=null;
	private LayoutInflater inflater=null;
	private Resources res=null;
	private CommonPreferences prefs=null;
	private boolean isvolunteer=false;
	private boolean informfriends=false;
	private boolean buzzer=false;

	public SettingsAdapter(Context ctx,Object title,Object desc,Object desc_long) {
		mctx=ctx;
		mdesc= (int[]) desc;
		mtitle=(int[]) title;
		mdesclong=(int[])desc_long;
		res=mctx.getResources();
		prefs=CommonPreferences.getInstance();
		isvolunteer=prefs.isIsvolunteer();
		informfriends=prefs.isInformFriends();
		buzzer=prefs.isNeedbuzzer();
	}

	@Override
	public int getCount() {
		return mtitle.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if(rowView==null){
			ViewHolder holder=new ViewHolder();
			inflater=LayoutInflater.from(mctx);
			rowView=inflater.inflate(R.layout.settings_list_item, null);
			holder.title=(TextView) rowView.findViewById(R.id.title);
			holder.desc=(TextView) rowView.findViewById(R.id.desc);	
			holder.check=(CheckBox)rowView.findViewById(R.id.check);
			holder.desc_long=(TextView)rowView.findViewById(R.id.desc_long);
			rowView.setTag(holder);
		}
		rowView.setOnClickListener(parentListner);
		ViewHolder holder=(ViewHolder) rowView.getTag();
		holder.title.setText(res.getString(mtitle[position]));
		holder.desc.setText(res.getString(mdesc[position]));
		holder.desc_long.setText(res.getString(mdesclong[position]));
		if(position==2||position==3||position==4){
			holder.check.setVisibility(View.VISIBLE);
			holder.check.setId(position);
			holder.check.setOnCheckedChangeListener(this);
			if(position==2)
				holder.check.setChecked(buzzer);
			else if(position==3)
				holder.check.setChecked(informfriends);
			else if(position==4)
				holder.check.setChecked(isvolunteer);
		}
		else
			holder.check.setVisibility(View.GONE);
		rowView.setId(position);
		return rowView;
	}

	static class ViewHolder {
		TextView title;
		TextView desc;
		TextView desc_long;
		CheckBox check;
	}

	

	View.OnClickListener parentListner=new  View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ViewHolder holder=(ViewHolder) v.getTag();
			
			switch (v.getId()) {
			case 0:
				Intent intent = new Intent();
				intent.setClass(mctx, FacebookFriendPicker.class);
				mctx.startActivity(intent);	
			  break;
			case 1:
				break;
			case 2 :
				CheckBox v1=holder.check;
				v1.setChecked(!v1.isChecked());
				break;
			case 3 :
				CheckBox v2=holder.check;
				v2.setChecked(!v2.isChecked());
				break;
			case 4 :
				CheckBox v3=holder.check;
				v3.setChecked(!v3.isChecked());
				break;
			case 5:
				((VolunteerStatus)mctx).startpatternActivity();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case 2 :
			prefs.setNeedbuzzer(isChecked);  
			break;
		case 3 :
			prefs.setInformFriends(isChecked); 
			break;
		case 4 :
			((VolunteerStatus)mctx).changetoVolunteer(isChecked); 
			break;
		default:
			break;
		}
	}


}


