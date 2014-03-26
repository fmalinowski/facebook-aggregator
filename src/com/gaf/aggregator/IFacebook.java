package com.gaf.aggregator;

import org.json.JSONArray;

/**
 * Interface to get the NewsFeed posts
 */
public interface IFacebook {

	/**
	 * Callback to be given to refreshNewsFeed method
	 * @param postsObj	JSONArray containing all the posts of the news feed
	 */
	void callbackFBLogged();
	
	void callbackFBPosts(JSONArray postsObj);
}
