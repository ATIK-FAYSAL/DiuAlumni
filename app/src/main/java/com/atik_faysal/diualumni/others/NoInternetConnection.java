package com.atik_faysal.diualumni.others;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.important.CheckInternetConnection;

import java.util.Objects;

public class NoInternetConnection extends Activity
{
     private CheckInternetConnection internetConnection;
     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.no_internet);
          Button bTry = findViewById(R.id.bTry);
          internetConnection = new CheckInternetConnection(this);

          bTry.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    try {
                         Class<?> className = Class.forName(Objects.requireNonNull(getIntent().getExtras()).getString("class"));//get class name from previous class
                         if(internetConnection.isOnline())
                         {
                              Intent intent = new Intent(NoInternetConnection.this,className);
                              if(getIntent().getExtras().getString("user")!=null)//use for get student id to retrieve student info
                                   intent.putExtra("user",getIntent().getExtras().getString("user"));
                              startActivity(intent);
                              finish();
                         }
                    } catch (ClassNotFoundException e) {
                         e.printStackTrace();
                    }
               }
          });
     }
}
