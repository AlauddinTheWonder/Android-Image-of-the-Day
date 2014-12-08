package com.alauddin.imageoftheday;

import static com.alauddin.imageoftheday.Common.Bing;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageDownloader 
{
	private Object c;
	private Method success;
	private Method error;
	private Boolean isCallback = false;
	
	public ImageDownloader(Object scope, String successCallback, String errorCallback)
	{
		this.c = scope;
		
		if(scope != null && 
				!successCallback.isEmpty() && successCallback != null &&
				!errorCallback.isEmpty() && errorCallback != null)
		{
			try {
				this.success = c.getClass().getMethod(successCallback, Bitmap.class);
				this.error = c.getClass().getMethod(errorCallback, String.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			isCallback = true;
		}
	}
	
	public void DownloadBingImageFromJson(String URL)
	{
		new HttpAsyncTask().execute(URL);
	}
	
	/**
	 * Get json data from server
	 * @param URL : Required server url
	 * @return : JSonObject encoded data
	 */
	private JSONObject getJsonData(String URL)
	{
		String jsonString = "";
		JSONObject jObject = null;
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(URL);
		
		try {
			HttpResponse response = httpclient.execute(request);
			jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
			jObject = new JSONObject(jsonString);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jObject;
	}
	
	/**
	 * Split json data and find url from it and then proceed to downloader task
	 * @param jsonData
	 */
	private void getImageFromJson(JSONObject jsonData)
	{
		try {
			JSONArray images = jsonData.getJSONArray("images");
			JSONObject imageData = images.getJSONObject(0);
			String imageURL = imageData.getString("url");
			String ImgUrl = Bing + imageURL;
			
			// Download image
			new DownloadImageTask().execute(ImgUrl);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	class HttpAsyncTask extends AsyncTask<String, Void, JSONObject>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			//pDialog.setMessage("Getting Image URL from server...");
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
			return getJsonData(urls[0]);
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			getImageFromJson(result);
		}
	}
	
	class DownloadImageTask extends AsyncTask<String, Integer, Bitmap>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			//pDialog.setMessage("Downloading Image...");
		}
		
		@Override
		protected Bitmap doInBackground(String... args)
		{
			Bitmap bitmap = null;
			try
			{
				bitmap = BitmapFactory.decodeStream( (InputStream) new URL(args[0]).getContent() );
				//publishProgress(1); // to call onProgressUpdate();
			}
			catch (Exception e)
			{
				if(isCallback){
					try {
						error.invoke(c, "Error in downloading image");
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException se) {
						se.printStackTrace();
					}
				}
				e.printStackTrace();
			}
			return bitmap;
		}
		
		/*@Override
		protected void onProgressUpdate(Integer... progress)
		{
			super.onProgressUpdate(progress);
			pDialog.setMessage("Downloading Image... " + progress[0] + "%");
		}*/
		
		@Override
		protected void onPostExecute(Bitmap image)
		{
			if(image != null){
				if(isCallback){
					try {
						success.invoke(c, image);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			else
			{
				if(isCallback){
					try {
						error.invoke(c, "Image Does Not exist or Network Error");
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
