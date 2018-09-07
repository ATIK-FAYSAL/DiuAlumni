package com.atik_faysal.diualumni.others;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;

import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.AboutProfile;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import android.content.Context;

@SuppressLint("ValidFragment")
public class AdditionalInfo extends AboutProfile
{
     private Context context;
     private Activity activity;
     private OnResponseTask onResponseTask;

     @SuppressLint("ValidFragment")
     public AdditionalInfo(Context context)
     {
          this.context = context;
          activity = (Activity)context;
          sharedPreferencesData = new SharedPreferencesData(context);
          internetConnection = new CheckInternetConnection(context);
     }

     public void onResultSuccess(OnResponseTask responseTask)
     {
          this.onResponseTask = responseTask;
     }

     //user url update and insert new url
     @SuppressLint("SetTextI18n")
     public void userUrls()
     {
          Button bAdd;
          final EditText txtUrl;ImageView imgClear;
          @SuppressLint("InflateParams")
          View view = LayoutInflater.from(context).inflate(R.layout.add_url,null);
          bAdd = view.findViewById(R.id.bAdd);
          txtUrl = view.findViewById(R.id.txtUrl);
          imgClear = view.findViewById(R.id.imgClear);
          builder = new AlertDialog.Builder(context);
          super.builder.setView(view);
          super.builder.setCancelable(false);
          alertDialog = builder.create();
          Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          alertDialog.show();

          imgClear.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    alertDialog.dismiss();
               }
          });

          bAdd.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    String url = txtUrl.getText().toString();
                    if(TextUtils.isEmpty(url)||url.length()<15)
                         Toast.makeText(context,"Invalid Url",Toast.LENGTH_LONG).show();
                    else
                    {
                         backgroundTask = new PostInfoBackgroundTask(context,responseTask);
                         postNewUrl(url);
                    }
                    alertDialog.dismiss();
               }
          });
     }

     private void postNewUrl(String url)
     {
          Map<String,String> maps = new HashMap<>();
          maps.put("option","putUrl");
          maps.put("stdId",sharedPreferencesData.getCurrentUserId());
          maps.put("url",url);
          if(internetConnection.isOnline())
               backgroundTask.InsertData(Objects.requireNonNull(activity).getResources().getString(R.string.otherInsertion),maps);
          else Toast.makeText(context, Objects.requireNonNull(activity).getResources().getString(R.string.noInternet),Toast.LENGTH_LONG).show();
     }

     //update url server response
     private OnResponseTask responseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               switch (value) {
                    case "success":
                         onResponseTask.onResultSuccess(value);
                         Toast.makeText(context, "This url is inserted successfully", Toast.LENGTH_LONG).show();
                         break;
                    case "overflow":
                         Toast.makeText(context, "Your limit is over,please remove one url and try again", Toast.LENGTH_LONG).show();
                         break;
                    default:
                         Toast.makeText(context, "Sorry insertion failed.please try again", Toast.LENGTH_LONG).show();
                         break;
               }
          }
     };

}
