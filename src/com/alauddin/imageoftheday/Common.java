package com.alauddin.imageoftheday;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;

public class Common 
{
	public final static String SP_TAG = "IOTD";
	public final static String Bing = "http://www.bing.com";
	public final static String JSonURL = Bing + "/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
	
	public static String getImageName()
	{
		Date date = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
		dFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return "Wallpaper-" + dFormat.format(date) + ".jpg";
	}
	
	public static Boolean saveImageToSD(Bitmap image, String imageName)
	{
		ImageStorage imageStorage = new ImageStorage();
		return imageStorage.saveToSdCard(image, imageName);
	}
	
	public static Boolean setAsWallpaper(Context context, Bitmap image)
	{
		WallpaperManager wm = WallpaperManager.getInstance(context);
		try {
			wm.setBitmap(image);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
