package com.libraries.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import com.joserv.Akram.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MGUtilities {

	public static void showToastShort(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static boolean hasConnection(Context c) {

		try {
			ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetwork != null && wifiNetwork.isConnected()) {
				return true;
			}

			NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobileNetwork != null && mobileNetwork.isConnected()) {
				return true;
			}

			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			if (activeNetwork != null && activeNetwork.isConnected()) {
				return true;
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	    return false;
	    
	}

	public static void showAlertView(Activity act, int resIdTitle, int resIdMessage) {
		AlertDialog.Builder alert = new AlertDialog.Builder(act);
	    alert.setTitle(resIdTitle);
	    alert.setMessage(resIdMessage);
	    alert.setPositiveButton(act.getResources().getString(R.string.ok), 
	    		new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
	    });

	    alert.create();
	    alert.show();
	}

	public static void showAlertView(Activity act, int resIdTitle, String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(act);
	    alert.setTitle(resIdTitle);
	    alert.setMessage(message);
	    alert.setPositiveButton(act.getResources().getString(R.string.ok), 
	    		new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
	    });

	    alert.create();
	    alert.show();
	}
	
	public static String getStringFromResource(Context c, int resid) {
		try{
		return c.getResources().getString(resid);
		}catch (Exception ex){
			return "error accrued";
		}
	}

	public static boolean isLocationEnabled(Context context) {
		try {

			LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			boolean isEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
					service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			return isEnabled;
		}catch (Exception e){
			return false;
		}
	}

	public static String filterInvalidChars(String text) {
		String newStr = text.replace("<p dir=\"ltr\">", "");
		newStr = newStr.replace("</p>", "");
		newStr = newStr.replace("\n", "");
		newStr = newStr.replace("\\n", "");
		newStr = newStr.replace("\\\n", "");
		return newStr;
	}

	public static String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

    public static String getDeviceID(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return md5(android_id).toUpperCase();
    }
}
