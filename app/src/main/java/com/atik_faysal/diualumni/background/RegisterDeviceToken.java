package com.atik_faysal.diualumni.background;

import android.util.Log;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by USER on 2/14/2018.
 */

public class RegisterDeviceToken
{
     /**when user successfully log in ,it will take device token for send notification
      *only first time take this token.
      */

     public static void registerToken(final String token,String stdId,String status)
     {

          OkHttpClient client = new OkHttpClient();
          RequestBody body = new FormBody.Builder()
               .add("option","deviceToken")
               .add("token",token)
               .add("stdId",stdId)
               .add("status",status)
               .build();

          final Request request = new Request.Builder()
               .url("http://diualumni.bdtechnosoft.com/diuAlumni/otherInsertion.php")
               .post(body)
               .build();

          client.newCall(request).enqueue(new Callback() {
               @Override
               public void onFailure(Call call, IOException e) {}

               @Override
               public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful())
                    {
                         String str = response.body().string();
                         Log.d("response",str);
                    }
               }
          });
     }
}
