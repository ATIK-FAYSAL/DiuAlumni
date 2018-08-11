package com.atik_faysal.diualumni.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.atik_faysal.diualumni.R;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

     private ProgressBar progressBar;

     @SuppressLint("SetTextI18n")
     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.splash_screen);
          TextView txtCurVersion = findViewById(R.id.txtVersion);
          progressBar = findViewById(R.id.progressBar);
          if(getCurrentVersion()!=null)
               txtCurVersion.setText("Version : "+getCurrentVersion());//set current version in textView
          loading();
     }

     //get current version from system
     private String getCurrentVersion()
     {
          String version = null;

          try {
               PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
               version = packageInfo.versionName;
          } catch (PackageManager.NameNotFoundException e) {
               e.printStackTrace();
          }
          return version;
     }

     private void loading()
     {
          Timer timer = new Timer();
          final Handler handler = new Handler();
          final Runnable runnable = new Runnable() {
               @Override
               public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(SplashScreen.this,JobPortal.class));
               }
          };
          timer.schedule(new TimerTask() {
               @Override
               public void run() {
                    handler.post(runnable);
               }
          },2500);
     }

}