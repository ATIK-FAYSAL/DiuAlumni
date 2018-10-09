package com.atik_faysal.diualumni.services;

import com.atik_faysal.diualumni.background.RegisterDeviceToken;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseInstanceIdServices extends FirebaseInstanceIdService
{

     @Override
     public void onTokenRefresh() {
          String token = FirebaseInstanceId.getInstance().getToken();

          SharedPreferencesData sharedPreferenceData = new SharedPreferencesData(this);

          RegisterDeviceToken.registerToken(token,sharedPreferenceData.getCurrentUserId(),"enable");
     }

}
