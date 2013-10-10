//
//package com.tavant.droid.security.adapters;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.graphics.Typeface;
//import android.provider.ContactsContract.Contacts;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CursorAdapter;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.tavant.droid.security.R;
//import com.tavant.droid.security.utils.Utils;
//
//
//public class PhoneAdapter extends CursorAdapter {
//
//	/** Layout inflater object to inflate view from xml */
//	private LayoutInflater mLayoutInflater;
//
//	
//
//	private Typeface mTypeFace = null;
//
//	private ViewHolder adapter = null;
//
//	/**
//	 * constructor.
//	 * @param list user list.
//	 * @param ctx context of the parent.
//	 */
//	public PhoneAdapter(Context ctx,Cursor c) {
//		super(ctx, c, true);
//		mLayoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	}
//
//
//	/**
//	 * method will create the view adapter object and returned to caller.
//	 * @param convertView  parent view object.
//	 * @return viewHolder object.
//	 */
//	private ViewHolder getAdapter(View convertView) {
//		ViewHolder adapter = new ViewHolder();
//		adapter.mName = (TextView) convertView.findViewById(R.id.ph_contacts_userName);
//		adapter.mAvathar = (ImageView) convertView.findViewById(R.id.ph_contacts_avathar);
//		adapter.mCheckBox = (ImageView) convertView.findViewById(R.id.ph_contacts_avathar);
//		convertView.setTag(adapter);
//		return adapter;
//	}
//
//
//	@Override
//	public void bindView(View view, Context context, Cursor cursor) {
//		adapter = (ViewHolder) view.getTag();
//		adapter.position = getCursor().getPosition();
//		view.setTag(adapter);
//
//		String name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
//		long imgUrl = cursor.getLong(cursor.getColumnIndex(Contacts._ID));
//		
//		String _id = cursor.getString(cursor
//				.getColumnIndex(Contacts._ID));
//
//		if (mTypeFace != null)
//			adapter.mName.setTypeface(mTypeFace);
//
//		adapter.mName.setText(name);
//
//		adapter.isPhoneContact = BobsledConst.CONTACT_TYPE_CONTACT;
//		adapter.position = cursor.getPosition();
//		adapter.mAvathar.setTag(R.string.Position, adapter.position);
//		adapter.mAvathar.setTag(R.string.ISFAVORITE,new Short("0"));
//		adapter.mAvathar.setTag(R.string.ContactType,BobsledConst.CONTACT_TYPE_CONTACT);
//		adapter.mAvathar.setTag(R.string.ImageUrl,""+ imgUrl);
//		adapter.mAvathar.setTag(R.string.USERID, _id);
//
//		mQuickAction.setQuickActionForView(adapter.mAvathar);
//		Utils.loadContactPhoto(context.getContentResolver(), imgUrl, adapter.mAvathar, adapter.mProgress, 60, 60);
//	}
//
//	@Override
//	public View newView(Context context, Cursor cursor, ViewGroup parent) {
//		View view = mLayoutInflater.inflate(R.layout.childcontacts, parent, false);
//		adapter = getAdapter(view);
//		view.setTag(adapter);
//		return view;
//	}
//
//
//
//}
