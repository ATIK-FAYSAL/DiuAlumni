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
    private static final String IMAGE_NAME = "imageName";
    private static final String NOTIFICATION_SETTINGS = "notification";

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

    public void setNotificationSettings(String option)
    {
         sharedPreferences = context.getSharedPreferences(NOTIFICATION_SETTINGS,Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();
         editor.putString("option",option);
         editor.apply();
    }

    //save current user info
    @SuppressLint("CommitPrefEdits")
    public void currentUserInfo() {
        sharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("stdId",maps.get("stdId"));
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

    public void userImageName(String imageName)
    {
        sharedPreferences = context.getSharedPreferences(IMAGE_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("imageName",imageName);
        editor.apply();
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

    //get current user id from SharedPreferences
    public String getCurrentUserId()
    {
        sharedPreferences = context.getSharedPreferences(USER_INFO,Context.MODE_PRIVATE);
        return sharedPreferences.getString("stdId","none");
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

    public String getImageName()
    {
        sharedPreferences = context.getSharedPreferences(IMAGE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString("imageName","none");
    }

     //get student id from SharedPreferences
     public String getNotificationSettings()
     {
          sharedPreferences = context.getSharedPreferences(NOTIFICATION_SETTINGS,Context.MODE_PRIVATE);
          return sharedPreferences.getString("option","disable");
     }

    public void clearData()
    {
        String[] prefNames = new String[]{USER_INFO,IMAGE_NAME};
        for(int i=0;i<prefNames.length-1;i++)
        {
            sharedPreferences = context.getSharedPreferences(prefNames[i],Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
        }
    }

}
