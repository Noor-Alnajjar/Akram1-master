package com.libraries.bitmap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class MGImageUtils {

	public static Bitmap getBitmap(String imagePath) {

	    long size_file = getFileSize(new File(imagePath));

	    size_file = (size_file) / 1000; // in Kb now
	    int ample_size = 1;
	    // ample size is used to reduce size of bitmap
	    if (size_file <= 250) {
	         ample_size = 2;
	    } else if (size_file > 251 && size_file < 1500) {  
	        ample_size = 4; 
	    } else if (size_file >= 1500 && size_file < 3000){
	        ample_size = 8; 
	    } else if (size_file >= 3000 && size_file <= 4500){ 
	        ample_size = 12; 
	    } else if (size_file >= 4500) {
	       ample_size = 16;
	  }

	      Bitmap bitmap = null;
	      BitmapFactory.Options bitoption = new BitmapFactory.Options();
	      bitoption.inSampleSize = ample_size;

	      Bitmap bitmapPhoto = BitmapFactory.decodeFile(imagePath, bitoption);

	      ExifInterface exif = null;
	      try {
	         exif = new ExifInterface(imagePath);
	        } catch (IOException e) {
	        e.printStackTrace(); 
	        }
	     int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
	     Matrix matrix = new Matrix();

	     if ((orientation == 3)) {
	               matrix.postRotate(180);
	               bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
	               bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
	                true);
	      } else if (orientation == 6) {
	               matrix.postRotate(90);
	               bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
	               bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
	                true);
	      } else if (orientation == 8) {
	               matrix.postRotate(270);
	               bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
	               bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
	               true);

	     } else {
	               matrix.postRotate(0);
	               bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
	               bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
	               true);
	     }
	   return bitmap;
	}
	
	public static long getFileSize(final File file) {
        if (file == null || !file.exists())
             return 0;
        if (!file.isDirectory())
             return file.length();
         final List<File> dirs = new LinkedList<File>();
               dirs.add(file);
               long result = 0;
              
              while (!dirs.isEmpty()) {
                      final File dir = dirs.remove(0);
                      if (!dir.exists())
                      continue;
                      final File[] listFiles = dir.listFiles();
                      if (listFiles == null || listFiles.length == 0)
                      continue;
                     for (final File child : listFiles) {
                              result += child.length();
                              if (child.isDirectory())
                               dirs.add(child);
                               }
                 }
              return result;
	}

	public static String getRealPathFromURI(Uri contentUri, Context c) {
		
	    String[] proj = { MediaStore.Images.Media.DATA };
	    CursorLoader loader = new CursorLoader(c, contentUri, proj, null, null, null);
	    Cursor cursor = loader.loadInBackground();
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
}
