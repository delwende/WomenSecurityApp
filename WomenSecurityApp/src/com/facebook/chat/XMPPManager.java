/*******************************************************************************
 * Copyright (c) 2011 by Vivox Inc.
 *  Permission to use, copy, modify or distribute this software in binary or source form
 *  for any purpose is allowed only under explicit prior consent in writing from Vivox Inc.
 * THE SOFTWARE IS PROVIDED "AS IS" AND VIVOX DISCLAIMS
 *  ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL VIVOX
 *  BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *  DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *  PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 *  ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS
 *  SOFTWARE.
 ******************************************************************************/

package com.facebook.chat;

import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class XMPPManager implements ConnectionListener, ChatManagerListener,
		MessageListener {

	// public static final String FACEBOOK_APP_KEY = "136375599784483";

	private static final String TAG = "FacebookLogin";

	// private static final String SECRET = "c4b361ecd33cd400b180b26d8629ba79";

	private String url = "https://graph.facebook.com/oauth/access_token?display=touch&scope=publish_stream,read_stream,offline_access,xmpp_login&client_id={0}&type=user_agent&redirect_uri=fbconnect://success&client_secret={1}&code={2}";

	private static XMPPManager mInstance;

	private XMPPConnection mXmppConnection;

	HashMap<String, Chat> chatList = new HashMap<String, Chat>();

	public XMPPChatListener mChatListener;

	private XMPPChatUpdateListener mChatUpdateListener;

	private String currentChatId;
	long perTime = 0;

	private ContentResolver mContentReolver;

	public static XMPPManager getInstance() {
		if (mInstance == null)
			mInstance = new XMPPManager();
		return mInstance;
	}

	public XMPPManager() {
	}

	private void CallXMPPLogin(String tocken, String scre) throws XMPPException {
		ConnectionConfiguration config = new ConnectionConfiguration(
				"chat.facebook.com", 5222);
		config.setSASLAuthenticationEnabled(true);
		mXmppConnection = new XMPPConnection(config);

		XMPPConnection.DEBUG_ENABLED = true;
		SASLAuthentication.registerSASLMechanism("X-FACEBOOK-PLATFORM",
				SASLXFacebookPlatformMechanism.class);
		SASLAuthentication.supportSASLMechanism("X-FACEBOOK-PLATFORM", 0);
		mXmppConnection.connect();
		// String connecitonId = mXmppConnection.getConnectionID();
		// if (connecitonId == null) {

		mXmppConnection.login("" + "|" + "", scre, "Application", tocken);
		// Log.d("FacebookLogin", "connecitonId : "+connecitonId);
		// mFacebookWorker.setXMPPConnectionId(connecitonId);
		// }
		mXmppConnection.addConnectionListener(this);
		mXmppConnection.getChatManager().addChatListener(this);

		mXmppConnection.getRoster().addRosterListener(mRosterListener);
		Log.d(TAG, "XMPP Login Success");

	}

	public void setXMPPChatListener(Context context, XMPPChatListener listener) {
		this.mChatListener = listener;
	}

	public void init(final String tocken, ContentResolver resolver) {
		if (isConnected()) {
			return;
		}
		mContentReolver = resolver;
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					CallXMPPLogin(tocken, "appsecret here");
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Log.d(TAG, "XMPP Login Fail " + e.toString());
						if (mXmppConnection != null) {
							mXmppConnection.disconnect();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					return null;
				}
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
			}
		};
		task.execute();
	}

	public boolean isConnected() {

		if (mXmppConnection != null && mXmppConnection.isConnected())
			return true;

		return false;
	}

	public void sendMess(String id, String message)
			throws XMPPConnetionException {

		if (mXmppConnection == null) {
			throw new XMPPConnetionException();
		}

		if (!mXmppConnection.isConnected()) {
			throw new XMPPConnetionException();
		}
		Chat sendChat = null;
		if (chatList.containsKey(id)) {
			sendChat = chatList.get(id);
		} else {
			ChatManager chatManager = mXmppConnection.getChatManager();
			sendChat = chatManager.createChat(id, this);
		}

		if (sendChat != null) {
			try {
				sendChat.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void sendChatMessage(String id, String message)
			throws XMPPConnetionException {
		String participanId = parserParticipantId(id);
		if (mChatListener != null) {
			mChatListener.sendChatMessage(participanId, message);
		}

		if (mChatUpdateListener != null && currentChatId != null
				&& currentChatId.equals(participanId)) {
			mChatUpdateListener.update();
		}
		this.sendMess(id, message);
	}

	public void disconnect() {

		if (mXmppConnection != null && mXmppConnection.isConnected()) {
			try {
				mXmppConnection.disconnect();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("FacebookLogin", "XMPP Login disconnect isConnect "
					+ mXmppConnection.isConnected());
		}
	}

	@Override
	public void connectionClosed() {
		Log.d(TAG, "connectionClosed");
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		Log.d(TAG, "connectionClosedOnError");
		try {
			if (mXmppConnection != null)
				mXmppConnection.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reconnectingIn(int arg0) {
		Log.d(TAG, "reconnectingIn");
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		Log.d(TAG, "reconnectionFailed");
	}

	@Override
	public void reconnectionSuccessful() {
		Log.d(TAG, "reconnectionSuccessful");
	}

	@Override
	public void chatCreated(Chat chat, boolean arg1) {
		Log.d(TAG, "Receiver Participant Id " + chat.getParticipant());
		if (chat != null) {
			chatList.put(chat.getParticipant(), chat);
			chat.addMessageListener(this);
		}
	}

	@Override
	public void processMessage(Chat chat, Message message) {

		if (mChatListener != null && message != null) {
			String participantId = chat.getParticipant();
			// Log.i(TAG, "Receiver Participant Id " + chat.getParticipant());
			// Log.i(TAG, "Parsed Participant Id " +
			// parserParticipantId(participantId));
			String participanId = parserParticipantId(participantId);
			String mess = message.getBody();
			if(mess!=null&&isMissedCallMsg(mess))	
				return;	
			else if(mess!=null)
			{
				boolean isChatWindowOpen = mChatUpdateListener != null && currentChatId != null
						&& currentChatId.equals(participanId);
				mChatListener.receivedChatMessage(participanId, mess, !isChatWindowOpen);
				if (isChatWindowOpen) {
					mChatUpdateListener.update();
				} else {
					mChatListener.showChatNotification(participanId, mess);
				}
				
			}
		}
	}

	private boolean isMissedCallMsg(String message) {
		Pattern p = Pattern.compile("you've missed a call from" );
		Matcher m = p.matcher(message);
		if (m.find()) 
			return true;	
		return false;
	}

	private String parserParticipantId(String participantId) {
		String _id = null;
		if (participantId != null)
			_id = participantId.substring(1, participantId.indexOf("@"));
		return _id;
	}

	public void setXMPPChatUpdateListener(String chatId,
			XMPPChatUpdateListener listener) {
		this.mChatUpdateListener = listener;
		this.currentChatId = chatId;
	}

	public interface XMPPChatUpdateListener {
		public void update();
	}

	public interface XMPPChatListener {
		public void receivedChatMessage(String receiverid, String message, boolean isNew);

		public void sendChatMessage(String senderId, String message);

		public void showChatNotification(String receiverid, String mess);
	}

	private RosterListener mRosterListener = new RosterListener() {

		@Override
		public void presenceChanged(Presence presence) {
			updatePresence(presence);
		}

		@Override
		public void entriesUpdated(Collection<String> arg0) {
		}

		@Override
		public void entriesDeleted(Collection<String> arg0) {
		}

		@Override
		public void entriesAdded(Collection<String> arg0) {
		}
	};

	private void updatePresence(Presence presence) {
		
		}

	}

