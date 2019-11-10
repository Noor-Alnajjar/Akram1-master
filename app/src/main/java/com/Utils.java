package com;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.joserv.Akram.R;

/**
 * Created by user on 2/6/18.
 */

public class Utils {
    private static Dialog dialog;
    public static void showLoading(Context context,boolean autostop) {
        if (dialog != null && dialog.isShowing())
            return;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


//        progressDialog = new ProgressDialog(context );
//        progressDialog.setMessage("Loading...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setContentView(R.layout.loading);
//        progressDialog.setIndeterminate(true);
        //       progressDialog.setCancelable(false);
        //      progressDialog.show();

        dialog.show();

        if(autostop){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoading();
            }
        }, 5000);
        }
    }
    public static void hideLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//            progressDialog = null;
//        }

    }
}
