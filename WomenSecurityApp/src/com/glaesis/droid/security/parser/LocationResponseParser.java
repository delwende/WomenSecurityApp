package com.glaesis.droid.security.parser;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.glaesis.droid.security.data.BaseData;
import com.glaesis.droid.security.data.CopsData;
import com.glaesis.droid.security.data.UserData;

public class LocationResponseParser extends BaseParser {

	/*
	 * sample xml
	 * 
	 * <?xml version="1.0"
	 * encoding="UTF-8"?><ROOT><METHOD>updatelocation</METHOD>
	 * 
	 * <SS>TRUE</SS> <MSG><PHONENO>9663960311</PHONENO></MSG></ROOT>
	 */
	private static final String TAG = LocationResponseParser.class
			.getSimpleName();
	private String mStartTag;
	private boolean isStart;
	CopsData mData;

	public LocationResponseParser(InputStream inputStream) {
		super(inputStream);
	}

	@Override
	public void doProcessTagByTag(int eventType) throws Exception {
		switch (eventType) {
		case XmlPullParser.START_DOCUMENT:

			break;
		case XmlPullParser.END_DOCUMENT:

			break;
		case XmlPullParser.START_TAG:
			String localName = pullParser.getName();
			if (localName.equalsIgnoreCase(HttpResponseTags.TAG_RESULT)) {
				mData = new CopsData();
			}
			mStartTag = localName;
			isStart = true;
			break;
		case XmlPullParser.END_TAG:
			isStart = false;
			break;
		case XmlPullParser.TEXT:
			if (isStart) {
				parseText();
			}
			break;
		}

	}

	@Override
	public void parseText() {
		if (isStart) {

			if (mStartTag.equalsIgnoreCase((String) HttpResponseTags.TAG_SS)) {
				mData.isSuccess = Boolean.parseBoolean(pullParser.getText());
			} else if (mStartTag.equalsIgnoreCase((String)HttpResponseTags.TAG_MSG)) {
				mData.serverMessages = pullParser.getText();
			} else if (mStartTag.equalsIgnoreCase((String)HttpResponseTags.TAG_METHOD)) {
				mData.methodName = pullParser.getText();
			}else if (mStartTag.equalsIgnoreCase((String)HttpResponseTags.TAG_PHONE_NO)) {
				mData.phoneNumber = pullParser.getText();
			}
		}
	}

	@Override
	public BaseData getData() {
		return mData;
	}

}
