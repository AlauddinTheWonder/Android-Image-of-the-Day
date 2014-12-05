/**
 * Image of the Day
 * Developer: Alauddin Ansari
 * Git: https://github.com/AlauddinTheWonder
 * Date: 4 Dec, 2014
 */

package com.alauddin.imageoftheday;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alauddin.imageoftheday.R;

public class MainActivity extends ActionBarActivity 
{
	final String Bing = "http://www.bing.com";
	final String JSonURL = Bing + "/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
	
	ProgressDialog pDialog;
	Bitmap ImageBitmap = null;
	String TodaysImageName = "";
	
	ImageStorage imageStorage;
	
	Button loadBtn;
	Button setWallpaperBtn;
	Button saveSDBtn;
	TextView tView;
	ImageView imgView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tView = (TextView) findViewById(R.id.text_output);
		loadBtn = (Button) findViewById(R.id.load_btn);
		setWallpaperBtn = (Button) findViewById(R.id.set_wallpaper);
		saveSDBtn = (Button) findViewById(R.id.save_sdcard);
		imgView = (ImageView) findViewById(R.id.img);
		
		setWallpaperBtn.setEnabled(false);
		saveSDBtn.setEnabled(false);
		
		imageStorage = new ImageStorage();
		
		// Generate today's image filename
		Date date = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
		dFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		TodaysImageName = "Wallpaper-" + dFormat.format(date) + ".jpg";
		
		// Check if today's image already downloaded
		Bitmap tImage = imageStorage.getImage(TodaysImageName);
		if(tImage != null)
		{
			processImage(tImage);
		}
		
		if(isConnected()){
			loadBtn.setEnabled(true);
			
			loadBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new HttpAsyncTask().execute(JSonURL);
				}
			});
			
			saveSDBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					saveImageToSD(ImageBitmap);
				}
			});
			
			setWallpaperBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setAsWallpaper(ImageBitmap);
				}
			});
        }
        else{
        	loadBtn.setEnabled(false);
        	tView.setText("No Internet Connectivity");
        	Toast.makeText(this, "No Internet Connectivity", Toast.LENGTH_LONG).show();
        }
		
	}
	
	/**
	 * Image has been downloaded from server
	 * @param image: Bitmap data downloaded from server
	 */
	public void processImage(Bitmap image)
	{
		ImageBitmap = image;
		
		imgView.setImageBitmap(image);
		setWallpaperBtn.setEnabled(true);
		saveSDBtn.setEnabled(true);
	}
	
	public void saveImageToSD(Bitmap image)
	{
		Boolean stored = imageStorage.saveToSdCard(image, TodaysImageName);
		if(stored)
		{
			Toast.makeText(this, "Image saved at: " + imageStorage.filepath, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this, imageStorage.errorInfo, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setAsWallpaper(Bitmap image)
	{
		WallpaperManager wm = WallpaperManager.getInstance(getApplicationContext());
		try {
			wm.setBitmap(ImageBitmap);
			Toast.makeText(MainActivity.this, "Wallpaper Changed", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Check if device is connected to the Internet or not
	 * @return true if connected otherwise false
	 */
	private boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null && networkInfo.isConnected()) 
            return true;
        else
            return false;
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
	private void getImage(JSONObject jsonData)
	{
		try {
			JSONArray images = jsonData.getJSONArray("images");
			JSONObject imageData = images.getJSONObject(0);
			String imageURL = imageData.getString("url");
			String ImgUrl = Bing + imageURL;
			
			// Download and process image
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
			
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Getting Image URL from server...");
			pDialog.show();
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
			return getJsonData(urls[0]);
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			getImage(result);
		}
	}
	
	class DownloadImageTask extends AsyncTask<String, Integer, Bitmap>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pDialog.setMessage("Downloading Image...");
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
				processImage(image);
				pDialog.dismiss();
			}
			else
			{
				pDialog.dismiss();
		        Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
