package com.atik_faysal.diualumni.others;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.MyCv;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by USER on 1/29/2018.
 */

public class Feedback extends AppCompatActivity
{
     private EditText eFeedback;
     private Button bFeedback;
     private Toolbar toolbar;
     private TextView txtName;

     private DisplayMessage displayMessage;
     private RequireMethods methods;
     private CheckInternetConnection internetConnection;
     private ProgressDialog progressDialog;

     private String currentUser;
     //private static final String FILE_URL = "http://192.168.56.1/feedback.php";

     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.feedback);
          initComponent();
     }

     @Override
     protected void onStart() {
          super.onStart();
          if(!internetConnection.isOnline())//if internet is not connect go to no internet page ,
          {
               String className = Feedback.class.getName();
               Intent intent = new Intent(Feedback.this,NoInternetConnection.class);
               intent.putExtra("class",className);//send current class name to NoInternetConnection class
               startActivity(intent);
               finish();
          }
     }

     //initialize all user information related variable by getText from textView or editText
     @SuppressLint("ClickableViewAccessibility")
     private void initComponent()
     {
          eFeedback = findViewById(R.id.eFeedback);
          txtName = findViewById(R.id.txtName);
          bFeedback = findViewById(R.id.bFeedback);
          bFeedback.setBackgroundDrawable(getDrawable(R.drawable.disable_button));
          TextView txtDate = findViewById(R.id.txtDate);
          toolbar = findViewById(R.id.toolbar1);
          setSupportActionBar(toolbar);

          displayMessage = new DisplayMessage(this);
          methods = new RequireMethods(this);
          progressDialog = new ProgressDialog(this);
          SharedPreferencesData sharedPreferenceData = new SharedPreferencesData(this);
          internetConnection = new CheckInternetConnection(this);
          currentUser = sharedPreferenceData.getCurrentUserId();

          txtName.setText(sharedPreferenceData.getUserName());
          txtDate.setText(methods.getDateWithTime());
          if(eFeedback.getText().toString().isEmpty())bFeedback.setEnabled(false);

          //on text changed listener and take action
          eFeedback.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {}

               @Override
               public void afterTextChanged(Editable s) {
                    if(eFeedback.getText().toString().length()<20)
                    {
                         bFeedback.setEnabled(false);
                         bFeedback.setBackgroundDrawable(getDrawable(R.drawable.disable_button));
                    }
                    else
                    {
                         bFeedback.setEnabled(true);
                         bFeedback.setBackgroundDrawable(getDrawable(R.drawable.button_done));
                    }
               }
          });

          //calling method
          onButtonClick();//button click method
          setToolbar();//set toolbar in top
     }

     //set a toolbar,above the page
     private void setToolbar()
     {
          toolbar.setTitleTextColor(getResources().getColor(R.color.white));
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          getSupportActionBar().setDisplayShowHomeEnabled(true);
          toolbar.setNavigationIcon(R.drawable.icon_back);
          toolbar.setNavigationOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    finish();
               }
          });
     }

     //button click
     private void onButtonClick()
     {
          bFeedback.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    if(internetConnection.isOnline())
                    {
                         Map<String,String> map = new HashMap<>();
                         map.put("option","feedback");
                         map.put("stdId",currentUser);
                         map.put("feedback",eFeedback.getText().toString());
                         map.put("date",methods.getDateWithTime());
                         if(internetConnection.isOnline())
                         {
                              progressDialog.setTitle("Please wait....");
                              progressDialog.setMessage("Adding your feedback");
                              progressDialog.show();
                              PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(Feedback.this,responseTask);
                              backgroundTask.insertData(getResources().getString(R.string.otherInsertion),map);
                         }
                    }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));

               }
          });

          txtName.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    Intent page = new Intent(Feedback.this,Feedback.class);
                    page.putExtra("userName",currentUser);
                    startActivity(page);
               }
          });

     }

     //get server response for post feedback
     OnResponseTask responseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String message) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         switch (message)
                         {
                              case "success":
                                   Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                             try
                                             {
                                                  Thread.sleep(getResources().getInteger(R.integer.progTime));
                                                  runOnUiThread(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                            progressDialog.dismiss();
                                                            finish();
                                                            Toast.makeText(Feedback.this,"Your feedback is added.",Toast.LENGTH_LONG).show();
                                                       }
                                                  });
                                             }catch (InterruptedException e)
                                             {
                                                  e.printStackTrace();
                                             }
                                        }
                                   });
                                   thread.start();
                                   break;
                              default:
                                   displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
                                   break;
                         }
                    }
               });
          }
     };
}
