/**
 * Image of the Day
 * Developer: Alauddin Ansari
 * Git: https://github.com/AlauddinTheWonder
 * Date: 4 Dec, 2014
 */

package com.alauddin.imageoftheday;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static com.alauddin.imageoftheday.Common.JSonURL;

public class MainActivity extends ActionBarActivity 
{
	Context context;
	
	ProgressDialog pDialog;
	Bitmap ImageBitmap = null;
	String TodaysImageName = "";
	
	ImageStorage imageStorage;
	
	Button loadBtn, setWallpaperBtn, saveSDBtn;//, browseBtn;
	TextView tView;
	ImageView imgView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		
		imageStorage = new ImageStorage(context); // Initializing ImageStorage class
		pDialog = new ProgressDialog(this);
		
		// Initialzing views
		tView = (TextView) findViewById(R.id.text_output);
		loadBtn = (Button) findViewById(R.id.load_btn);
		setWallpaperBtn = (Button) findViewById(R.id.set_wallpaper);
		saveSDBtn = (Button) findViewById(R.id.save_sdcard);
		//browseBtn = (Button) findViewById(R.id.browse_dir);
		imgView = (ImageView) findViewById(R.id.img);
		
		setWallpaperBtn.setEnabled(false);
		saveSDBtn.setEnabled(false);
		//browseBtn.setEnabled(false);
		// Initialzing views ends
		
		// Generate today's image filename
		TodaysImageName = Common.getImageName();
		
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
					ImageDownloader imgDownloader = new ImageDownloader(MainActivity.this, "processImage", "imageDownloadError");
					imgDownloader.DownloadBingImageFromJson(JSonURL);
					pDialog.setMessage("Getting Image from server...");
					pDialog.show();
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
			
			/*browseBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					browseDirectory();
				}
			});*/
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
		//browseBtn.setEnabled(true);
		pDialog.dismiss();
	}
	
	public void imageDownloadError(String str)
	{
		Toast.makeText(this, "Download Error: "+str, Toast.LENGTH_LONG).show();
		pDialog.dismiss();
	}
	
	public void saveImageToSD(Bitmap image)
	{
		if(Common.saveImageToSD(context, image, TodaysImageName))
		{
			Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(context, "Error in saving image", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setAsWallpaper(Bitmap image)
	{
		if(Common.setAsWallpaper(context, image))
		{
			Toast.makeText(context, "Wallpaper Changed", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(context, "Error in changing wallpaper", Toast.LENGTH_SHORT).show();
		}
	}
	
	/*public void browseDirectory()
	{
		String imagePath = imageStorage.filepath;
		Log.i("IOTD", "IMG: "+imagePath);
		if(!imagePath.isEmpty())
		{
			Uri uri = Uri.parse("file://"+imagePath);
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(uri, "image/*");
			startActivity(i);
		}
		else
		{
			Toast.makeText(context, "Image not found!", Toast.LENGTH_LONG).show();
		}
	}*/
	
	
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id)
		{
			case R.id.action_settings:
				Intent settings = new Intent(this, SettingsPreference.class);
				startActivity(settings);
				return true;
			case R.id.action_about:
				Intent about = new Intent(this, AboutActivity.class);
				startActivity(about);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
