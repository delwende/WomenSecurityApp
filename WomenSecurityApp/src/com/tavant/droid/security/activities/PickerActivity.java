package com.tavant.droid.security.activities;
///**
// * Copyright 2010-present Facebook.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.tavant.droid.womensecurity.activities;
//
//import java.util.List;
//
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.Context;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//
//import com.facebook.FacebookException;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.FriendPickerFragment;
//import com.facebook.widget.PickerFragment;
//import com.facebook.widget.PlacePickerFragment;
//import com.tavant.droid.womensecurity.R;
//import com.tavant.droid.womensecurity.database.ContentDescriptor;
//import com.tavant.droid.womensecurity.database.ContentDescriptor.WSFacebook;
//
///**
// * The PickerActivity enhances the Friend or Place Picker by adding a title
// * and a Done button. The selection results are saved in the ScrumptiousApplication
// * instance.
// */
//public class PickerActivity extends FragmentActivity {
//    public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
//   
//    private FriendPickerFragment friendPickerFragment;
//    private PlacePickerFragment placePickerFragment;
//    private LocationListener locationListener;
//    private ContentResolver resolver;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pickers);
//        resolver=getContentResolver();
//
//        Bundle args = getIntent().getExtras();
//        FragmentManager manager = getFragmentManager();
//        Fragment fragmentToShow = null;
//        Uri intentUri = getIntent().getData();
//
//        if (FRIEND_PICKER.equals(intentUri)) {
//            if (savedInstanceState == null) {
//                friendPickerFragment = new FriendPickerFragment(args);
//            } else {
//                friendPickerFragment = (FriendPickerFragment) manager.findFragmentById(R.id.picker_fragment);;
//            }
//
//            friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
//                @Override
//                public void onError(PickerFragment<?> fragment, FacebookException error) {
//                    PickerActivity.this.onError(error);
//                }
//            });
//            friendPickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
//                @Override
//                public void onDoneButtonClicked(PickerFragment<?> fragment) {
//                    finishActivity();
//                }
//            });
//            fragmentToShow = friendPickerFragment;
//
//        }  
//
//        manager.beginTransaction().replace(R.id.picker_fragment, fragmentToShow).commit();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (FRIEND_PICKER.equals(getIntent().getData())) {
//            try {
//                friendPickerFragment.loadData(false);
//            } catch (Exception ex) {
//                onError(ex);
//            }
//        } 
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (locationListener != null) {
//            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            locationManager.removeUpdates(locationListener);
//            locationListener = null;
//        }
//    }
//
//    private void onError(Exception error) {
////        String text = getString(R.string.exception, error.getMessage());
////        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
////        toast.show();
//    }
//
//    private void onError(String error, final boolean finishActivity) {
////        AlertDialog.Builder builder = new AlertDialog.Builder(this);
////        builder.setTitle(R.string.error_dialog_title).
////                setMessage(error).
////                setPositiveButton(R.string.error_dialog_button_text, new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                        if (finishActivity) {
////                            finishActivity();
////                        }
////                    }
////                });
////        builder.show();
//    }
//
//    private void finishActivity() {
//        if (FRIEND_PICKER.equals(getIntent().getData())) {
//            if (friendPickerFragment != null) {
//              List<GraphUser>Users= (List<GraphUser>) friendPickerFragment.getSelection();
//              for ( GraphUser user: Users ){
//            	  ContentValues values=new ContentValues();
//            	  values.put(WSFacebook.Cols.FBID, user.getId());
//            	  values.put(WSFacebook.Cols.FBNAME, user.getFirstName());
//            	  resolver
//					.insert(ContentDescriptor.WSFacebook.CONTENT_URI, values);
//              }
//            }
//        } 
//        setResult(RESULT_OK, null);
//        finish();
//    }
//}
