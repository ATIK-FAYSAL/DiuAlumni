package com.atik_faysal.diualumni.background;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

public class SharedPreferencesData
{
     private Context context;
     private SharedPreferences sharedPreferences;
     private SharedPreferences.Editor editor;
     private Map<String,String>maps;

     private static final String IS_LOGIN = "login";
     private static final String REMEMBER_ME = "remember";
     private static final String USER_INFO = "userInfo";


     public SharedPreferencesData(Context context)
     {
          this.context = context;
     }

     public SharedPreferencesData(Context context,Map<String,String> map)
     {
          this.maps = map;
          this.context = context;
     }

     //save user id,password and check box status
     @SuppressLint("CommitPrefEdits")
     public void rememberMe(String id,String pass,boolean flag)
     {
          sharedPreferences = context.getSharedPreferences(REMEMBER_ME,Context.MODE_PRIVATE);
          editor = sharedPreferences.edit();
          editor.putString("id",id);
          editor.putString("pass",pass);
          editor.putBoolean("check",flag);
          editor.apply();
     }

     //store user log in status
     public void isUserLogin(boolean flag)
     {
          sharedPreferences = context.getSharedPreferences(IS_LOGIN,Context.MODE_PRIVATE);
          editor = sharedPreferences.edit();
          editor.putBoolean("login",flag);
          editor.apply();
     }

     //save current user info
     @SuppressLint("CommitPrefEdits")
     public void currentUserInfo() {
          sharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
          editor = sharedPreferences.edit();
          editor.putString("name",maps.get("name"));
          editor.putString("email", maps.get("email"));
          editor.putString("phone", maps.get("phone"));
          editor.putString("type", maps.get("type"));
          editor.apply();
     }

     //get check box status
     public boolean checkBoxStatus()
     {
          sharedPreferences = context.getSharedPreferences(REMEMBER_ME,Context.MODE_PRIVATE);
          return sharedPreferences.getBoolean("check",false);
     }

     //get user log in status
     public boolean getIsUserLogin()
     {
          sharedPreferences = context.getSharedPreferences(IS_LOGIN,Context.MODE_PRIVATE);
          return sharedPreferences.getBoolean("login",false);
     }

     //get student id from SharedPreferences
     public String getStudentId()
     {
          sharedPreferences = context.getSharedPreferences(REMEMBER_ME,Context.MODE_PRIVATE);
          return sharedPreferences.getString("id","none");
     }

     //get user password
     public String getPassword()
     {
          sharedPreferences = context.getSharedPreferences(REMEMBER_ME,Context.MODE_PRIVATE);
          return sharedPreferences.getString("pass","none");
     }

     //get user name
     @SuppressLint("CommitPrefEdits")
     public String getUserName()
     {
          sharedPreferences = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
          return sharedPreferences.getString("name","none");
     }

     //get user email
     public String getUserEmail()
     {
          sharedPreferences = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
          return sharedPreferences.getString("email","none");
     }

     //get user name
     @SuppressLint("CommitPrefEdits")
     public String getUserPhone()
     {
          sharedPreferences = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
          return sharedPreferences.getString("phone","none");
     }

     //get user email
     public String getUserType()
     {
          sharedPreferences = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
          return sharedPreferences.getString("type","none");
     }

}
