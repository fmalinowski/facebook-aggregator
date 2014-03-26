package com.gaf.aggregator;

import java.util.Arrays;

import org.json.JSONArray;

import android.app.Activity;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.model.*;


public class Facebook {

	private Activity activity;
	private boolean isSessionOpened;
	private Session session;
	private SessionState sessionState;
	
	/**
	 * 
	 * @param activity	The activity which is opening the new Facebook session
	 */
	public Facebook(Activity activity) {
		this.isSessionOpened 	= false;
		this.activity			= activity;
	}
	
	/**
	 * Open a Facebook session (log in if needed)
	 * @param fbInterface	Object that contain the callback which will be called when logged
	 */
	public void login(final IFacebook fbInterface) {
		
		//start Facebook Login
		Session.openActiveSession(activity, true, new Session.StatusCallback() {

			//callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				onSessionStateChange(session, state, exception, fbInterface);
			}
		});
	}
	
	
	/**
	 * Close the Facebook session and deletes the token and cookies (Facebook logout)
	 */
	public void logout() {
		this.session.closeAndClearTokenInformation();
	}
	
	
	
	/**
	 * When Facebook session state changes
	 * @param session
	 * @param state
	 * @param exception
	 */
	private void onSessionStateChange(Session session, SessionState state, Exception exception, IFacebook fbInterface) {
		
		this.session			= session;
		this.sessionState		= state;
		
		//When Facebook session is opened
		if (session.isOpened()) {
			
			if(!this.isSessionOpened) {
				
				fbInterface.callbackFBLogged();
			}
			
			this.isSessionOpened 	= true;			
		}
		  
		else if(session.isClosed()) {
			this.isSessionOpened = false;
		}
	}
	
	
	/**
	 * Get the last Facebook news feed
	 * @param fbInterface	Object that contain the callback which will be called when results are ready
	 */
	public void refreshNewsFeed(final IFacebook fbInterface) {
		
		NewPermissionsRequest npr;
		
		
		if(session.isOpened()) {
		
			//Set permissions to read user-location, user_birthday, user_likes
			npr = new Session.NewPermissionsRequest(activity, 
					Arrays.asList("user_location", "user_birthday", "user_likes", "read_stream"));
			session.requestNewReadPermissions(npr);
			
			
			//Create a request to get facebook news feed
			Request request = new Request(session, "me/home", null, null, new Request.Callback() {
	
				//When we get the news feed
				@Override
				public void onCompleted(Response response) {
					
					GraphObject graphObject = response.getGraphObject();
					JSONArray fbPosts;
										
					if (graphObject != null) {
						fbPosts = (JSONArray)graphObject.getProperty("data"); 
						fbInterface.callbackFBPosts(fbPosts);					
					}
				}
				  
			  });
	
			  //Send the request
			  Request.executeBatchAsync(request);
		}
	}
	
}
