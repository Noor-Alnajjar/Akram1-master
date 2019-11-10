package com.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by King Jocoa on 4/21/2017.
 */

public class PermissionManager {
    public static int CODE_READ_PERM = 10;
    public static int CODE_WRITE_PERM = 11;
    public static int CODE_STORAGE_PERM = 13;
    public static int CODE_CUSTOM_PERM = 14;

    /**
     * Check permissions
     *
     * @param context
     * @return success:true
     */

    public static boolean isReadStorageAllowed(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isWriteStorageAllowed(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isStorageAllowed(Context context) {
        return isReadStorageAllowed(context) && isWriteStorageAllowed(context);
    }

    public static boolean isCameraAllowed(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isAccessLocationAllowed(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }




    /**
     * request permissions
     *
     * @param activity
     * @param permissions
     * @param code
     */
    public static void requestPermission(Activity activity, String[] permissions, int code) {
        ActivityCompat.requestPermissions(activity, permissions, code);
    }

    /**
     * check grant result
     *
     * @param grantResults
     * @return success:true
     */
    public static boolean checkPermissionGrantResult(@NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        } else
            return false;
    }
}
