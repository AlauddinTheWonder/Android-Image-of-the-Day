/**
 * Image of the Day
 * Developer: Alauddin Ansari
 * Git: https://github.com/AlauddinTheWonder
 * Date: 4 Dec, 2014
 */

package com.alauddin.imageoftheday;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static com.alauddin.imageoftheday.Common.ImageDir;

public class ImageStorage 
{
	private Context context;
	public String dirPath = "";
	public String filepath = "";
	public String errorInfo = "";
	
	public ImageStorage(Context ctx)
	{
		this.context = ctx;
		
		File sdcard = new File(Common.getPrefferedStorage(this.context));
		File folder = new File(sdcard.getAbsoluteFile(), ImageDir);
		if(!folder.isDirectory() || !folder.exists()){
			folder.mkdir();
		}
		this.dirPath = folder.getAbsolutePath();
	}
	
	public Boolean saveToSdCard(Bitmap bitmap, String filename) 
	{
		Boolean stored = false;
		
		if(Common.isExternalStorageWritable())
		{
			File file = new File(this.dirPath, filename);
			/*if(file.exists()){
				this.errorInfo = "Image file already exists at: " + file.getAbsolutePath();
				return false;
			}*/
			
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
		
		if(mediaImage.exists()){
			filepath = mediaImage.getAbsolutePath();
			return BitmapFactory.decodeFile(mediaImage.getAbsolutePath());
		} 
		else
			return null;
	}
}
