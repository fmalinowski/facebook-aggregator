package com.gaf.aggregator;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.model.*;


public class MainActivity extends Activity {

	
	Activity currentActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		final Facebook fb;
		fb = new Facebook(this);		
		
		IFacebook iFb = new IFacebook() {
			
			@Override
			public void callbackFBPosts(JSONArray postsObj) {
			
				FacebookAdapter facebookAdapter;
				ListView fbListView;
			
				fbListView = (ListView)findViewById(R.id.facebookListView);
				
				facebookAdapter	= new FacebookAdapter(getApplicationContext(), postsObj);
				fbListView.setAdapter(facebookAdapter);
			}

			@Override
			public void callbackFBLogged() {
				fb.refreshNewsFeed(this);				
			}
		};
		
		
		fb.login(iFb);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
	
	
	