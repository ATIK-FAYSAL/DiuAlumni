package com.atik_faysal.diualumni.important;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;

@SuppressLint("Registered")
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

     public int calculateDate(String date)
     {
          int totalDays=0,day,month,year;

          try {
               String value[];
               value = date.split("-");
               day = Integer.parseInt(value[0]);
               month = Integer.parseInt(value[1]);
               year = Integer.parseInt(value[2]);

               totalDays = day+month*30+year*365;
          }catch (NumberFormatException e)
          {
               Toast.makeText(context,"",Toast.LENGTH_LONG).show();
          }

          return totalDays;
     }
}
