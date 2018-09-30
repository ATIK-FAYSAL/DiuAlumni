package com.atik_faysal.diualumni.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DesEncryptionAlgo;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity implements Methods,View.OnClickListener
{
     private EditText txtStdId,txtPass;
     private CheckBox checkBox;
     private TextView txtErrMsg;

     private CheckInternetConnection internetConnection;
     private PostInfoBackgroundTask backgroundTask;
     private DisplayMessage dialogClass;
     private DesEncryptionAlgo encryptionAlgo;
     private SharedPreferencesData sharedPreferencesData;
     private RequireMethods requireMethods;

     private String studentId,password;

     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.sign_in);
          initComponent();
     }


     @Override
     protected void onStart() {
          super.onStart();
          if(sharedPreferencesData.checkBoxStatus())//if check box status is true,save id and password
          {
               checkBox.setChecked(true);
               txtStdId.setText(sharedPreferencesData.getStudentId());
               txtPass.setText(sharedPreferencesData.getPassword());
          }else//remove student id and password
          {
               checkBox.setChecked(false);
               txtStdId.setText("");
               txtPass.setText("");
          }
     }

     //initialize component
     @Override
     public void initComponent()
     {
          TextView txtSignUp = findViewById(R.id.txtSignUp);
          txtStdId = findViewById(R.id.txtStdId);
          txtPass = findViewById(R.id.txtPass);
          Button bSignin = findViewById(R.id.bSignIn);
          TextView txtForPass = findViewById(R.id.txtForgotPass);
          checkBox = findViewById(R.id.cRemember);
          txtErrMsg = findViewById(R.id.txtErrMsg);
          txtErrMsg.setVisibility(View.INVISIBLE);

          //set on click listener
          bSignin.setOnClickListener(this);
          txtSignUp.setOnClickListener(this);
          txtForPass.setOnClickListener(this);
          checkBox.setOnClickListener(this);

          internetConnection = new CheckInternetConnection(this);
          backgroundTask = new PostInfoBackgroundTask(this,responseTask);
          dialogClass = new DisplayMessage(this);
          encryptionAlgo = new DesEncryptionAlgo(this);
          sharedPreferencesData = new SharedPreferencesData(this);
          requireMethods = new RequireMethods(this);
     }

     //button click
     @Override
     public void onClick(View view) {
          switch (view.getId())
          {
               case R.id.bSignIn://user sign in button
                    if(userLogIn())
                    {
                         if(internetConnection.isOnline())
                         {
                              if(studentId!=null&&password!=null)
                              {
                                   Map<String,String>map = new HashMap<>();
                                   map.put("option","logIn");
                                   map.put("id",studentId);
                                   map.put("pass",encryptionAlgo.encryptPass(password));
                                   backgroundTask.InsertData(getString(R.string.login),map);//send request to server
                              }
                         }else
                              dialogClass.errorMessage(getString(R.string.noInternet));
                    }
                    break;
               case R.id.txtForgotPass:
                    Toast.makeText(this,"Forgot password",Toast.LENGTH_LONG).show();
                    break;
               case R.id.txtSignUp:
                    startActivity(new Intent(SignIn.this,UserRegistration.class));//go to registration page
                    finish();
                    break;
               case R.id.cRemember:
                    if(userLogIn())//check user information
                    {
                         if(checkBox.isChecked())//this mehtod contain username ,password,and checkbox status
                              sharedPreferencesData.rememberMe(studentId,password,true);
                         else
                              sharedPreferencesData.rememberMe(studentId,password,false);
                    }else checkBox.setChecked(false);
                    break;
          }
     }

     //user log in info validation
     private boolean userLogIn()
     {
          boolean flag = true;
          studentId = txtStdId.getText().toString();
          password = txtPass.getText().toString();

          if (studentId.length() < 8 || studentId.length() > 15)
               flag = false;
          for (int i = 0; i < studentId.length(); i++) {
               if ((studentId.charAt(i) >= '0' && studentId.charAt(i) <= '9') || studentId.charAt(i) == '-') {

               } else
                    flag = false;
          }

          if(password.length() < 8||password.length() >= 16)
               flag = false;
          if(!flag)
               txtErrMsg.setVisibility(View.VISIBLE);
          else txtErrMsg.setVisibility(View.INVISIBLE);

          return flag;
     }

     @Override
     public void setToolbar() {}

     //process json data
     @Override
     public void processJsonData(String jsonData)
     {
          String email = null,phone = null,type = null,name=null,imageName=null;

          try {
               JSONObject jObject = new JSONObject(jsonData);
               JSONArray jArray = jObject.optJSONArray("info");
               int count=0;
               while(count<jArray.length())
               {
                    JSONObject object = jArray.getJSONObject(count);
                    name = object.getString("name");
                    email = object.getString("email");
                    phone = object.getString("phone");
                    type = object.getString("type");
                    imageName = object.getString("imageName");
                    count++;
               }
          } catch (JSONException e) {
               dialogClass.errorMessage(getString(R.string.jsonErr));
          }finally
          {
               Map<String,String>maps = new HashMap<>();
               maps.put("stdId",studentId);
               maps.put("name",name);
               maps.put("email",email);
               maps.put("phone",phone);
               maps.put("type",type);
               sharedPreferencesData = new SharedPreferencesData(this,maps);//context and user info map
               sharedPreferencesData.currentUserInfo();//store current user information
               sharedPreferencesData.isUserLogin(true);//user log in status true
               sharedPreferencesData.userImageName(imageName);//store user image name


               final ProgressDialog ringProgressDialog = ProgressDialog.show(this, "Please wait","Authenticating", true);
               ringProgressDialog.setCancelable(true);
               new Thread(new Runnable() {
                    @Override
                    public void run() {
                         try {
                              Thread.sleep(2500);
                         } catch (Exception e) {
                         }
                         ringProgressDialog.dismiss();
                    }
               }).start();
               ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                         requireMethods.closeActivity(SignIn.this,JobPortal.class);
                    }
               });

          }
     }

     //check user log in is success
     OnResponseTask responseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               if(value!=null)
               {
                    switch (value)
                    {
                         case "failed":
                              dialogClass.errorMessage(getString(R.string.executionFailed));
                              break;
                         default:
                              processJsonData(value);//processing json data
                              break;
                    }
               }
          }
     };
}
