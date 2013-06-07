package com.tavant.womensecurity.parser;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.tavant.droid.womensecurity.data.BaseData;
import com.tavant.droid.womensecurity.data.UserData;

/**
 * 
 * @author tavant technologies
 * 
 */
public class UserDataParser extends BaseParser {

	/**
	 * sample xml  :  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ROOT>"+
	 *                 "<METHOD>userdata</METHOD>\n"+
	 *                 "<SS>TRUE</SS><MSG>updated message</MSG></ROOT>";
	 */
	
	
	
	private static final String TAG = UserDataParser.class.getSimpleName();
	private String mStartTag;
	private boolean isStart;
	UserData mData;
	
	
	public UserDataParser(InputStream inputStream) {
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
				mData = new UserData();
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
			
			if (mStartTag.equalsIgnoreCase(HttpResponseTags.TAG_SS.toString())) {
				mData.isSuccess = Boolean.parseBoolean(pullParser.getText());
			} else if (mStartTag.equalsIgnoreCase(HttpResponseTags.TAG_MSG.toString())) {
				mData.serverMessages = pullParser.getText();
			} else if (mStartTag.equalsIgnoreCase(HttpResponseTags.TAG_METHOD.toString())) {
				mData.methodName = pullParser.getText();
			}
		}
		
	}

	@Override
	public BaseData getData() {
		return mData;
	}

}
