package com.atik_faysal.diualumni.others;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class UserPermission
{
    private Context context;

    public UserPermission(Context context)
    {
        this.context = context;
    }

    public void userPermission()
    {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)//for choose image from gallery
            ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)//for get phone number for registration
            ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.READ_PHONE_STATE},1);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED)//fot get verification code for registration
            ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.RECEIVE_SMS},1);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED)//access internet
            ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},1);

    }

}
