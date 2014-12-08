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
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.alauddin.imageoftheday.Common.JSonURL;
import static com.alauddin.imageoftheday.Common.SP_TAG;

public class MainActivity extends ActionBarActivity 
{
	Context context;
	
	SharedPreferences sp;
	ProgressDialog pDialog;
	Bitmap ImageBitmap = null;
	String TodaysImageName = "";
	
	ImageStorage imageStorage;
	
	Button loadBtn;
	Button setWallpaperBtn;
	Button saveSDBtn;
	TextView tView;
	ImageView imgView;
	CheckBox autoDownl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;
		
		sp = this.getSharedPreferences(SP_TAG, MODE_PRIVATE); // Initializing shared preference
		imageStorage = new ImageStorage(); // Initializing ImageStorage class
		pDialog = new ProgressDialog(this);
		
		// Initialzing views
		tView = (TextView) findViewById(R.id.text_output);
		loadBtn = (Button) findViewById(R.id.load_btn);
		setWallpaperBtn = (Button) findViewById(R.id.set_wallpaper);
		saveSDBtn = (Button) findViewById(R.id.save_sdcard);
		imgView = (ImageView) findViewById(R.id.img);
		autoDownl = (CheckBox) findViewById(R.id.autoDownload);
		
		setWallpaperBtn.setEnabled(false);
		saveSDBtn.setEnabled(false);
		autoDownl.setChecked(sp.getBoolean("AUTODOWNLDCHK", true));
		// Initialzing views ends
		
		// Generate today's image filename
		TodaysImageName = Common.getImageName();
		
		// Check if today's image already downloaded
		Bitmap tImage = imageStorage.getImage(TodaysImageName);
		if(tImage != null)
		{
			processImage(tImage);
		}
		
		// Assigning listener for Enable-Disable Auto Download image
		autoDownl.setOnCheckedChangeListener(new AutoDownloadChk());
		
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
		pDialog.dismiss();
	}
	
	public void imageDownloadError(String str)
	{
		Toast.makeText(this, "Download Error: "+str, Toast.LENGTH_LONG).show();
		pDialog.dismiss();
	}
	
	public void saveImageToSD(Bitmap image)
	{
		if(Common.saveImageToSD(image, TodaysImageName))
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
	
	class AutoDownloadChk implements OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			ComponentName receiver = new ComponentName(context, NetworkReceiver.class);
			PackageManager pm = context.getPackageManager();
			
			if(isChecked)
			{
				pm.setComponentEnabledSetting(receiver,
				        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				        PackageManager.DONT_KILL_APP);
				
				Toast.makeText(context, "Enabled auto-downloader", Toast.LENGTH_SHORT).show();
			}
			else
			{
				pm.setComponentEnabledSetting(receiver,
				        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				        PackageManager.DONT_KILL_APP);
				
				Toast.makeText(context, "Disabled auto-downloader", Toast.LENGTH_SHORT).show();
			}
			Editor editor = sp.edit();
			editor.putBoolean("AUTODOWNLDCHK", isChecked);
			editor.commit();
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
