package com.alauddin.imageoftheday;

import static com.alauddin.imageoftheday.Common.JSonURL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver
{
	Context context;
	String TodaysImageName;
	
	@Override
    public void onReceive(Context context, Intent intent)
	{
		this.context = context;
		
		int netState = NetworkUtil.getConnectivityStatus(context);
		
        if(netState > 0)
        {
        	TodaysImageName = Common.getImageName();
        	
        	ImageStorage imageStorage = new ImageStorage(context);
        	Bitmap tImage = imageStorage.getImage(TodaysImageName);
    		if(tImage == null)
    		{
    			ImageDownloader imgDownloader = new ImageDownloader(NetworkReceiver.this, "successHandler", "errorHandler");
				imgDownloader.DownloadBingImageFromJson(JSonURL);
    		}
        }
	}
	
	public void successHandler(Bitmap image)
	{
		if(Common.saveImageToSD(context, image, TodaysImageName)){
			if(Common.setAsWallpaper(context, image))
			{
				Toast.makeText(context, "Wallpaper: Image of the Day", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public void errorHandler(String str)
	{
		//Toast.makeText(context, "Download Error: "+str, Toast.LENGTH_LONG).show();
	}
}
