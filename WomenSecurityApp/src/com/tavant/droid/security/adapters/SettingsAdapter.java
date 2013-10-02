package com.tavant.droid.security.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.media.MediaRouter.VolumeCallback;
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
	 private Context mctx=null;
	 private LayoutInflater inflater=null;
	 private Resources res=null;
	 private CommonPreferences prefs=null;
	
	public SettingsAdapter(Context ctx,Object title,Object desc) {
		mctx=ctx;
		mdesc= (int[]) desc;
		mtitle=(int[]) title;
		res=mctx.getResources();
		prefs=CommonPreferences.getInstance();
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
			rowView.setTag(holder);
		}
		ViewHolder holder=(ViewHolder) rowView.getTag();
		holder.title.setText(res.getString(mtitle[position]));
		holder.desc.setText(res.getString(mdesc[position]));
		if(position==2||position==3||position==4){
			holder.check.setVisibility(View.VISIBLE);
			holder.check.setId(position);
			holder.check.setOnClickListener(this);
		}
		else
			holder.check.setVisibility(View.GONE);
		rowView.setId(position);
		return rowView;
	}
	
	static class ViewHolder {
		  TextView title;
		  TextView desc;
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


