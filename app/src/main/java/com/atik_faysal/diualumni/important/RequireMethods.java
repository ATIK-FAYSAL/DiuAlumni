package com.atik_faysal.diualumni.important;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.atik_faysal.diualumni.R;

public class RequireMethods extends AppCompatActivity
{
     private Context context;
     private Activity activity;
     public RequireMethods(Context context)
     {
          this.context = context;
          this.activity = (Activity)context;
     }

     //get current time and date
     @SuppressLint("SimpleDateFormat")
     public String getDateWithTime()
     {
          Calendar calendar = Calendar.getInstance();
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MMM.dd hh:mm aaa");
          return dateFormat.format(calendar.getTime());
     }

     //get current time and date
     @SuppressLint("SimpleDateFormat")
     public String getDate()
     {
          Calendar calendar = Calendar.getInstance();
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMMM-dd");
          return dateFormat.format(calendar.getTime());
     }


     //facebook profile
     public Intent getFbContent(String fbUrl)
     {
          try {
               context.getPackageManager().getPackageInfo("com.facebook.katana",0);
               String myFbProfile = "fb://profile/"+fbUrl;
               return new Intent(Intent.ACTION_VIEW, Uri.parse(myFbProfile));
          } catch (PackageManager.NameNotFoundException e) {
               String facebookProfileUri = "https://www.facebook.com/" + fbUrl;
               return new Intent(Intent.ACTION_VIEW, Uri.parse(facebookProfileUri));
          }
     }

     //show my linkedin profile
     public void openLinkedin(String myLinkedin)
     {
          Uri uri = Uri.parse(myLinkedin);
          Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
          activity.startActivity(intent);
     }

     ////show my logo_github profile
     public void openGithub(String myGithub)
     {
          Uri uri = Uri.parse(myGithub);
          Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
          startActivity(intent);
     }

     //close top all activity and go to specific activity
     public void closeActivity(Activity context, Class<?> clazz) {
          Intent intent = new Intent(context, clazz);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          context.startActivity(intent);
          context.finish();
     }

     //refresh current page
     public void reloadPage(final SwipeRefreshLayout refreshLayout, final Class<?>nameOfClass)
     {
          refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
               @Override
               public void onRefresh() {
                    refreshLayout.setRefreshing(true);

                    (new Handler()).postDelayed(new Runnable() {
                         @Override
                         public void run() {
                              refreshLayout.setRefreshing(false);
                              context.startActivity(new Intent(context,nameOfClass));
                              activity.finish();
                         }
                    },2500);
               }
          });
     }

}
