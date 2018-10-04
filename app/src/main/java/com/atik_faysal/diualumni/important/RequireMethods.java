package com.atik_faysal.diualumni.important;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;

@SuppressLint("Registered")
public class RequireMethods extends AppCompatActivity
{
     private Context context;
     private Activity activity;
     private CheckInternetConnection internetConnection;
     private SharedPreferencesData sharedPreferencesData;
     private boolean flag;
     private DisplayMessage displayMessage;
     private DesEncryptionAlgo desEncryptionAlgo;
     private String newPass;


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

     //calculate date for checking start date is smaller than end date
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

     //change password
     public void changePassword()
     {
          internetConnection = new CheckInternetConnection(context);
          sharedPreferencesData = new SharedPreferencesData(context);
          displayMessage = new DisplayMessage(context);
          desEncryptionAlgo = new DesEncryptionAlgo(context);
          final EditText txtOldPass,txtNewPass,txtConPass;
          Button bChange;
          final CheckBox cRemember;
          AlertDialog alertDialog;
          AlertDialog.Builder builder;
          @SuppressLint("InflateParams")
          View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_pass,null);
          builder = new AlertDialog.Builder(context);
          txtOldPass = view.findViewById(R.id.txtOldPass);
          txtNewPass = view.findViewById(R.id.txtNewPass);
          txtConPass = view.findViewById(R.id.txtConPass);
          bChange = view.findViewById(R.id.bChange);
          cRemember = view.findViewById(R.id.cRememberPass);

          builder.setView(view);
          builder.setCancelable(true);
          alertDialog = builder.create();
          Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          alertDialog.show();

          bChange.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    Map<String,String> map = new HashMap<>();
                    String oldPass = desEncryptionAlgo.encryptPass(txtOldPass.getText().toString());//decrypt password by des method
                    newPass = desEncryptionAlgo.encryptPass(txtNewPass.getText().toString());//decrypt password by des method
                    String conPass = desEncryptionAlgo.encryptPass(txtConPass.getText().toString());//decrypt password by des method

                    if((newPass.length()<8||newPass.length()>20))
                    {
                         Toast.makeText(context,"Invalid password.Password must be in 8-20 characters",Toast.LENGTH_LONG).show();
                         return;
                    }

                    if(newPass.equals(conPass))
                    {
                         map.put("option","changePass");
                         map.put("stdId",sharedPreferencesData.getCurrentUserId());
                         map.put("oldPass",oldPass);
                         map.put("newPass",newPass);
                         if(internetConnection.isOnline())
                         {
                              PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(context,onResponseTask);
                              backgroundTask.InsertData(activity.getResources().getString(R.string.updateOperation),map);
                         }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
                    }else Toast.makeText(context,"Password does not matched",Toast.LENGTH_LONG).show();
               }
          });

          cRemember.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    flag = cRemember.isChecked();
               }
          });

          alertDialog.dismiss();
     }

     private OnResponseTask onResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               switch (value) {
                    case "success":
                         if (flag)
                              sharedPreferencesData.rememberMe(sharedPreferencesData.getCurrentUserId(),newPass,sharedPreferencesData.checkBoxStatus());
                         break;
                    case "not found":
                         displayMessage.errorMessage("Your old password does not match.Please try again");
                         break;
                    default:
                         displayMessage.errorMessage(activity.getResources().getString(R.string.executionFailed));
                         break;
               }
          }
     };
}
