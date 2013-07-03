package com.facebook.chat;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.Sasl;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;

import android.util.Log;

public class SASLXFacebookPlatformMechanism extends SASLMechanism {

	private static final String NAME = "X-FACEBOOK-PLATFORM";

	private String apiKey = "";
	private String applicationSecret = "";
	private String sessionKey = "";
	private String tocken="";

	/**
	 * Constructor.
	 */
	public SASLXFacebookPlatformMechanism(SASLAuthentication saslAuthentication) {
		super(saslAuthentication);
	}

	@Override
	protected void authenticate() throws IOException, XMPPException {

		getSASLAuthentication().send(new AuthMechanism(NAME, ""));
	}

	@Override
	public void authenticate(String apiKeyAndSessionKey, String host,
			String applicationSecret) throws IOException, XMPPException {
		if (apiKeyAndSessionKey == null || applicationSecret == null) {
			throw new IllegalArgumentException("Invalid parameters");
		}

		String[] keyArray = apiKeyAndSessionKey.split("\\|", 2);
		if (keyArray.length < 2) {
			throw new IllegalArgumentException(
					"API key or session key is not present");
		}

		this.apiKey = keyArray[0];
		this.applicationSecret = applicationSecret;
		this.sessionKey = keyArray[1];

		this.authenticationId = sessionKey;
		this.password = applicationSecret;
		this.hostname = host;

		String[] mechanisms = {"X-FACEBOOK-PLATFORM"};
		Map<String, String> props = new HashMap<String, String>();
		this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props,
				this);
		authenticate();
	}
	

	@Override
	public void authenticate(String username, String host, String password,
			String tocken) throws IOException, XMPPException {
		
		if (username == null || password == null) {
			throw new IllegalArgumentException("Invalid parameters");
		}

		String[] keyArray = username.split("\\|", 2);
		if (keyArray.length < 2) {
			throw new IllegalArgumentException(
					"API key or session key is not present");
		}

		this.apiKey = keyArray[0];
		this.applicationSecret = password;
		this.sessionKey = keyArray[1];
		this.tocken=tocken;

		this.authenticationId = sessionKey;
		this.password = applicationSecret;
		this.hostname = host;

		String[] mechanisms = {"X-FACEBOOK-PLATFORM"};
		Map<String, String> props = new HashMap<String, String>();
		this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props,
				this);
		authenticate();
	
	}

	@Override
	public void authenticate(String username, String host, CallbackHandler cbh)
			throws IOException, XMPPException {
		String[] mechanisms = { "DIGEST-MD5" };
		Map<String, String> props = new HashMap<String, String>();
		this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props,
				cbh);
		authenticate();
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	public void challengeReceived(String challenge) throws IOException {
		byte[] response = null;
		if (challenge != null) {
			String decodedChallenge = new String(Base64.decode(challenge));
			Log.i("VIVOX",""+decodedChallenge);
			Map<String, String> parameters = getQueryMap(decodedChallenge);

			String version =parameters.get("version");
			String nonce = parameters.get("nonce");
			String method = parameters.get("method");
			
			float callId =(float) System.currentTimeMillis();
			
			String demoText="method=" +URLEncoder.encode(method,"utf-8")+
							"&nonce=" + URLEncoder.encode(nonce, "utf-8")+
			                "&access_token=" +URLEncoder.encode(tocken,"utf-8")+
			                "&api_key=" +URLEncoder.encode(apiKey,"utf-8")+
			                "&callId="+URLEncoder.encode(""+callId,"utf-8")+
			                "&v=" + URLEncoder.encode(version, "utf-8");
			     
			Log.i("VIVOX", "Response============"+demoText);
			
			response=demoText.getBytes("utf-8");

		}
		
		
		String authenticationText = "";

		if (response != null) {
			authenticationText = Base64.encodeBytes(response,
					Base64.DONT_BREAK_LINES);
		}
		Log.i("VIVOX","authenticationtext:::  "+authenticationText);

		// Send the authentication to the server
		getSASLAuthentication().send(new Response(authenticationText));
	}
	

	private Map<String, String> getQueryMap(String query) {
		Map<String, String> map = new HashMap<String, String>();
		String[] params = query.split("\\&");

		for (String param : params) {
			String[] fields = param.split("=", 2);
			map.put(fields[0], (fields.length > 1 ? fields[1] : null));
		}

		return map;
	}

}
