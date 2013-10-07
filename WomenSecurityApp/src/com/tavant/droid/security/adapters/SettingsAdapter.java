package com.tavant.droid.security.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.media.MediaRouter.VolumeCallback;
import android.support.v7.appcompat.R.bool;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tavant.droid.security.R;
import com.tavant.droid.security.prefs.CommonPreferences;
import com.tavant.droid.security.utils.VolunteerStatus;

public class SettingsAdapter extends BaseAdapter implements android.view.View.OnClickListener{

	
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
		ViewHolder holder=(ViewHolder) rowView.getTag();
		holder.title.setText(res.getString(mtitle[position]));
		holder.desc.setText(res.getString(mdesc[position]));
		holder.desc_long.setText(res.getString(mdesclong[position]));
		if(position==2||position==3||position==4){
			holder.check.setVisibility(View.VISIBLE);
			holder.check.setId(position);
			holder.check.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		
		CheckBox v1=(CheckBox)v;
		switch (v1.getId()) {
		  case 2 :
			  prefs.setNeedbuzzer(v1.isChecked());  
		  break;
		  case 3 :
			  prefs.setInformFriends(v1.isChecked());  
		  break;
		  case 4 :
			((VolunteerStatus)mctx).changetoVolunteer(v1.isChecked()); 
		  break;
		default:
			break;
		}
	}

	
}


