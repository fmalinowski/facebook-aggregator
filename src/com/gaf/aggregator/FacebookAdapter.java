package com.gaf.aggregator;

import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FacebookAdapter extends BaseAdapter {

	JSONArray postsObj;
	LayoutInflater inflater;
	
	public FacebookAdapter(Context context, JSONArray postsObj) {
		this.postsObj = postsObj;
		this.inflater = LayoutInflater.from(context);
	}
	
	
	/**
	 * Number of Facebook posts
	 */
	@Override
	public int getCount() {
		return postsObj.length();
	}

	/**
	 * One of the posts
	 */
	@Override
	public Object getItem(int position) {
		return postsObj.optJSONObject(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		String strFrom;
		String strTo;
		String strCreatedTime;
		String strMsgStory;
		String strLinkDescription;
		String strTagged;
		String strLikes;
		String strComments;
		String strPictureURL;
		
		TextView txtFrom;
		TextView txtTo;
		TextView txtCreatedTime;
		TextView txtMsgStory;
		TextView txtLinkDescription;
		TextView txtTagged;
		TextView txtLikes;
		TextView txtComments;
		ImageView imgView;
		
		URL pictureURL;
		Bitmap bmp;
		
		
		convertView			= inflater.inflate(R.layout.facebook_listview_row, null);
		
		//We retrieve the post at the position position
		JSONObject post 	= postsObj.optJSONObject(position);
		

		strFrom				= makeFrom(post);
		strTo				= makeTo(post);
		strCreatedTime		= makeCreatedTime(post);
		strMsgStory			= makeMessageStory(post);
		strLinkDescription	= makeLinkDescription(post);
		strTagged			= makeTagged(post);
		strLikes			= makeLikes(post);
		strComments			= makeComments(post);
		strPictureURL		= makePictureURL(post);
		
		txtFrom				= (TextView) convertView.findViewById(R.id.from);
		txtTo				= (TextView) convertView.findViewById(R.id.to);
		txtCreatedTime		= (TextView) convertView.findViewById(R.id.createdTime);
		txtMsgStory			= (TextView) convertView.findViewById(R.id.msgStory);
		txtLinkDescription	= (TextView) convertView.findViewById(R.id.description);
		txtTagged			= (TextView) convertView.findViewById(R.id.tagged);
		txtLikes			= (TextView) convertView.findViewById(R.id.likes);
		txtComments			= (TextView) convertView.findViewById(R.id.comments);
		imgView 			= (ImageView)convertView.findViewById(R.id.list_picture);
		
		
		if((strPictureURL != null) && (!strPictureURL.equals(""))){
			
			//Thread imgThread = new Thread(new CollectFBData(imgView, strPictureURL));
			//imgThread.start();
			
			FBPictureTaskParams fbPicParams =  new FBPictureTaskParams(imgView, strPictureURL);
			new FBPictureLoadTask().execute(fbPicParams);
		}
		
		txtFrom.setText(strFrom);
		txtTo.setText(strTo);
		txtCreatedTime.setText(strCreatedTime);
		txtMsgStory.setText(strMsgStory);
		txtLinkDescription.setText(strLinkDescription);
		txtTagged.setText(strTagged);
		txtLikes.setText(strLikes);
		txtComments.setText(strComments);
		
		

		return convertView;
	}
	
	
	/**
	 * Construct the FROM string for a given post
	 * @param post	Facebook Post
	 * @return		FROM string for the given post
	 */
	public String makeFrom(JSONObject post) {
	
		String str;
		
		str = "";
		
		try {
		
			if(post.has("from"))
				str	= post.getJSONObject("from").getString("name");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return str;
	}
	
	
	
	/**
	 * Construct the TO string for a given post
	 * @param post	Facebook Post
	 * @return		TO string for the given post
	 */
	public String makeTo(JSONObject post) {
		
		JSONObject to;
		JSONArray toArray;
		JSONObject toCur;
		String str;
		
		str = "";
		
		if(post.has("to")) {
			
			to	= post.optJSONObject("to");
			
			try {
			
				toArray	= to.getJSONArray("data");
				for(int i = 0; i < toArray.length(); i++) {
					
					toCur 	= toArray.getJSONObject(i);
					str 	+= toCur.getString("name");
					str		+= "  ";
				}
				
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	
	
	/**
	 * Construct the CREATED_TIME string for a given post
	 * @param post	Facebook Post
	 * @return		CREATED_TIME string for the given post
	 */
	public String makeCreatedTime(JSONObject post) {
		
		String str;
		
		str = "";
		
		if(post.has("created_time"))
			str	= "POSTED AT: " + post.optString("created_time");
		
		return str;
	}
	
	
	
	/**
	 * Construct the MESSAGE/STORY string for a given post
	 * @param post	Facebook Post
	 * @return		MESSAGE/STORY string for the given post
	 */
	public String makeMessageStory(JSONObject post) {
		
		String str;
		
		str = "";
		
		if(post.has("message"))
			str = post.optString("message");
		
		if(post.has("story"))
			str = post.optString("story");
		
		return str;
	}
	
	
	/**
	 * Construct the DESCRIPTION string for a given post
	 * @param post	Facebook Post
	 * @return		DESCRIPTION string for the given post
	 */
	public String makeLinkDescription(JSONObject post) {
		
		String str;
		
		str = "";
		
		if(post.has("name"))
			str = "NAME: " + post.optString("name");
		
		if(post.has("name"))
			str += "\n";
		
		if(post.has("link"))
			str += "LINK: " + post.optString("link");
		
		if(post.has("link"))
			str += "\n";
		
		if(post.has("description"))
			str += "DESCRIPTION: " + post.optString("description");
		
		return str;
	}
	
	
	/**
	 * Construct the TAGGED string for a given post
	 * @param post	Facebook Post
	 * @return		TAGGED string for the given post
	 */
	public String makeTagged(JSONObject post) {
		
		JSONObject tags;
		JSONObject tagCur;
		JSONArray tagArray;
		Iterator<String> tagsNameIt;
		String tagKey;
		String str;
		
		str 	= "";
		tags 	= null;
			
		try {
			
			if(post.has("message_tags"))
				tags = post.getJSONObject("message_tags");
			
			if(post.has("story_tags"))
				tags = post.getJSONObject("story_tags");
			
			if(tags != null) {
				
				str = "PEOPLE TAGGED: ";
			
				tagsNameIt	= tags.keys();
				while(tagsNameIt.hasNext()) {
					
					tagKey 		= (String)tagsNameIt.next();
					tagArray	= tags.getJSONArray(tagKey);
					
					for(int i = 0; i < tagArray.length(); i++) {
						
						tagCur 	= tagArray.getJSONObject(i);
						str 	+= tagCur.getString("name");
						str		+= "  ";
					}
				}
			}
			
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return str;
	}
	
	
	/**
	 * Construct the LIKES string for a given post
	 * @param post	Facebook Post
	 * @return		LIKES string for the given post
	 */
	public String makeLikes(JSONObject post) {
		
		JSONObject likes;
		JSONArray likesArray;
		JSONObject likeCur;
		String str;
		
		str = "";
		
		likes = null;
		
		if(post.has("likes"))
			likes = post.optJSONObject("likes");
	
		if(likes != null) {
			
			try {
				
				str = "LIKES: ";
			
				likesArray	= likes.getJSONArray("data");
				for(int i = 0; i < likesArray.length(); i++) {
					
					likeCur = likesArray.getJSONObject(i);
					str 	+= likeCur.getString("name");
					str		+= "  ";
				}
				
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	
	
	/**
	 * Construct the COMMENTS string for a given post
	 * @param post	Facebook Post
	 * @return		COMMENTS string for the given post
	 */
	public String makeComments(JSONObject post) {
		
		JSONObject comments;
		JSONArray commentsArray;
		JSONObject commentCur;
		String str;
		int commentCount;
		
		str = "";
		comments = null;
		
		if(post.has("comments"))
			comments = post.optJSONObject("comments");
		
		if(comments != null) {
			
			try {

				commentCount = comments.getInt("count");
				
				if(commentCount > 0) {
					str = "COMMENTS:\n";
					
					
					commentsArray = comments.getJSONArray("data");
					for(int i = 0; i < commentsArray.length(); i++) {
						
						commentCur = commentsArray.getJSONObject(i);
						
						str	+= "(";
						str	+= commentCur.getString("created_time");
						str	+= " - FROM: ";
						str	+= commentCur.getJSONObject("from").getString("name");
						str	+= ") - Message: ";
						str += commentCur.getString("message");
						
						if(i != commentsArray.length()-1)
							str	+= "\n-----------------\n";
					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	
	
	/**
	 * Construct the URL of the picture for a given post
	 * @param post	Facebook Post
	 * @return		URL of the picture for the given post
	 */
	public String makePictureURL(JSONObject post) {
		
		String str;
		
		str = "";
		
		if(post.has("picture"))
			str	= post.optString("picture");
		
		return str;
	}
	
	
	private static class FBPictureTaskParams {
		
		private ImageView imgView;
		private String url;
		private Bitmap bitmap;
		
		FBPictureTaskParams(ImageView imgView, String url) {
			this.imgView	= imgView;
			this.url		= url;
		}
		
		void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}
		
		Bitmap getBitmap() {
			return this.bitmap;
		}
		
		ImageView getImageView() {
			return this.imgView;
		}
		
		String getURL() {
			return this.url;
		}
	}
	
	
	
	private class FBPictureLoadTask extends AsyncTask<FBPictureTaskParams, Void, FBPictureTaskParams> {

		@Override
		protected FBPictureTaskParams doInBackground(FBPictureTaskParams... params) {
			
			URL pictureURL;
			Bitmap bmp;
			
			try {
				
				pictureURL	= new URL(params[0].getURL());
				params[0].setBitmap(BitmapFactory.decodeStream(pictureURL.openConnection().getInputStream()));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return params[0];
		}
		
		protected void onPostExecute(FBPictureTaskParams result) {
	         ImageView imgView;
	         
	         imgView = result.getImageView();
	         imgView.setImageBitmap(result.getBitmap());
	     }
		
	}
	
	
	private class CollectFBData implements Runnable {

		String url;
		ImageView imgView;
		
		public CollectFBData(ImageView imgView, String url) {
			this.imgView 	= imgView;
			this.url		= url;
		}
		
		@Override
		public void run() {
			
			URL pictureURL;
			Bitmap bmp;
			
			try {
				
				pictureURL	= new URL(url);
				bmp			= BitmapFactory.decodeStream(pictureURL.openConnection().getInputStream());
				imgView.setImageBitmap(bmp);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
