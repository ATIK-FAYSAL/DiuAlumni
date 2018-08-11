package com.atik_faysal.diualumni.important;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.Context;
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


     //close top all activity and go to specific activity
     public void closeActivity(Activity context, Class<?> clazz) {
          Intent intent = new Intent(context, clazz);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          context.startActivity(intent);
          context.finish();
     }

}