package com.alauddin.imageoftheday;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;

public class Common 
{
	public final static String SP_TAG = "IOTD"; // SharedPreference TAG
	public final static String ImageDir = "ImageOfTheDay"; // Wallpaper directory name
	public final static String Bing = "http://www.bing.com";
	public final static String JSonURL = Bing + "/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
	
	public static String getImageName()
	{
		Date date = new Date();
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
		dFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return "Wallpaper-" + dFormat.format(date) + ".jpg";
	}
	
	public static Boolean saveImageToSD(Context context, Bitmap image, String imageName)
	{
		ImageStorage imageStorage = new ImageStorage(context);
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
	
	public static Boolean internalStorageExists()
	{
		return (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()));
	}
	
	public static String getInternalStorage()
	{
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		return "";
	}
	
	public static Boolean externalStorageExists()
	{
		String extSdCardStr = System.getenv("SECONDARY_STORAGE");
		if(extSdCardStr != null)
		{
			File extSdCardFile = new File(extSdCardStr);
			return (extSdCardFile.isDirectory());
		}
		return false;
	}
	
	public static String getExternalStorage()
	{
		String extSdCardStr = System.getenv("SECONDARY_STORAGE");
		if(extSdCardStr != null)
		{
			File extSdCardFile = new File(extSdCardStr);
			if(extSdCardFile.isDirectory())
				return extSdCardFile.getAbsolutePath();
		}
		
		return "";
	}
	
	public static String getPrefferedStorage(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("defaultStorage", Common.getInternalStorage());
	}
	

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
