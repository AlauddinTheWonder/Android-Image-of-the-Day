/**
 * Image of the Day
 * Developer: Alauddin Ansari
 * Git: https://github.com/AlauddinTheWonder
 * Date: 4 Dec, 2014
 */

package com.alauddin.imageoftheday;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageStorage 
{
	final static String ImageDir = "ImageOfDay"; // Local Path where images will be stored
	
	public String dirPath = "";
	public String filepath = "";
	public String errorInfo = "";
	
	public ImageStorage()
	{
		File sdcard = Environment.getExternalStorageDirectory();
		File folder = new File(sdcard.getAbsoluteFile(), ImageDir);
		if(!folder.exists()){
			folder.mkdir();
			System.out.println("Creating Directory: " + folder.getAbsolutePath());
		}
		
		this.dirPath = folder.getAbsolutePath();
	}
	
	public Boolean saveToSdCard(Bitmap bitmap, String filename) 
	{
		Boolean stored = false;
		
		if(isExternalStorageWritable())
		{
			File file = new File(this.dirPath, filename);
			if(file.exists()){
				this.errorInfo = "Image file already exists at: " + file.getAbsolutePath();
				return false;
			}
			
			try
			{
				FileOutputStream fOut = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				fOut.flush();
				fOut.close();
				
				this.errorInfo = "";
				filepath = file.getAbsolutePath();
				
				return true;
			}
			catch (Exception e)
			{
				this.errorInfo = "Error in saving image on SDCard";
				e.printStackTrace();
			}
		}
		else
		{
			this.errorInfo = "SDCard not writable!";
		}
		
		return stored;
	}
	
	public Bitmap getImage(String imagename)
	{
		File mediaImage = new File(this.dirPath, imagename);
		
		if(mediaImage.exists())
			return BitmapFactory.decodeFile(mediaImage.getAbsolutePath());
		else
			return null;
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
