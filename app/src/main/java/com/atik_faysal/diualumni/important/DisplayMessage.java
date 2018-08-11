package com.atik_faysal.diualumni.important;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DisplayMessage extends AlertDialog
{
     private Context context;
     private AlertDialog.Builder builder;
     private AlertDialog alertDialog;
     private RequireMethods methods;
     private PostInfoBackgroundTask backgroundTask;
     private SharedPreferencesData sharedPreferencesData;
     private CheckInternetConnection internetConnection;

     private Activity activity;


     //constructor
     public DisplayMessage(Context context)
     {
          super(context);
          this.context = context;
          builder = new AlertDialog.Builder(context);
          activity = (Activity) context;
          methods = new RequireMethods(context);
          sharedPreferencesData = new SharedPreferencesData(context);
          internetConnection = new CheckInternetConnection(context);
     }


     //execution failed
     public void errorMessage(String message)
     {
          Objects.requireNonNull(getWindow()).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
          iOSDialogBuilder builder = new iOSDialogBuilder(context);

          builder.setTitle("Attention")
               .setSubtitle(message)
               .setBoldPositiveLabel(true)
               .setCancelable(false)
               .setPositiveListener("ok",new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                         dialog.dismiss();
                    }
               }).build().show();
     }

     public void progressDialog(String message1, String message2, final Class<?>className)
     {
          final ProgressDialog ringProgressDialog = ProgressDialog.show(getContext(), message1,message2, true);
          ringProgressDialog.setCancelable(true);
          new Thread(new Runnable() {
               @Override
               public void run() {
                    try {
                         Thread.sleep(2500);
                    } catch (Exception e) {
                    }
                    ringProgressDialog.dismiss();
               }
          }).start();
          ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
               @Override
               public void onDismiss(DialogInterface dialog) {
                    methods.closeActivity(activity,className);
               }
          });
     }

}
