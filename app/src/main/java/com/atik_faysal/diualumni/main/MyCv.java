package com.atik_faysal.diualumni.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.BooleanResponse;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.others.CvViewer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class MyCv extends AppCompatActivity implements Methods
{

     private CheckInternetConnection internetConnection;
     private SharedPreferencesData sharedPreferencesData;
     private DisplayMessage displayMessage;
     private RequireMethods methods;
     private PostInfoBackgroundTask backgroundTask;
     private TextView txtId,txtChoose;
     private ImageView imgDoc;
     private String path;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.my_cv);
          initComponent();//initialize component
     }

     //initialize all component
     @Override
     public void initComponent() {
          txtChoose =  findViewById(R.id.choose);
          sharedPreferencesData = new SharedPreferencesData(this);
          internetConnection = new CheckInternetConnection(this);
          displayMessage = new DisplayMessage(this);
          methods = new RequireMethods(this);
          txtId = findViewById(R.id.docId);
          imgDoc = findViewById(R.id.imgDoc);
          
          txtChoose.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View arg0) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    try {
                         startActivityForResult(Intent.createChooser(intent, "Choose file"), 1);
                    }catch (Exception e)
                    {
                         Log.d("excp",e.toString());
                    }
               }
          });

          imgDoc.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                         showPopup();
               }
          });
          
          //call method
          retrieveResume();
          setToolbar();//toolbar
     }

     //retrieve resume from server
     private void retrieveResume()
     {
          Map<String,String> map = new HashMap<>();
          map.put("option","resume");
          map.put("stdId",sharedPreferencesData.getCurrentUserId());
          if(internetConnection.isOnline())
          {
               backgroundTask = new PostInfoBackgroundTask(this,responseTask);
               backgroundTask.insertData(getResources().getString(R.string.readInfo),map);
          }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
     }

     //show a popup menu for delete and edit
     private void showPopup()
     {
          PopupMenu popupMenu = new PopupMenu(this, imgDoc);
          popupMenu.getMenuInflater().inflate(R.menu.resum_option, popupMenu.getMenu());
          popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                         case R.id.mView:
                              Intent intent = new Intent(MyCv.this, CvViewer.class);
                              intent.putExtra("url",path);
                              startActivity(intent);
                              break;
                         case R.id.mRemove:
                              displayMessage.onResultSuccess(booleanResponse);
                              displayMessage.warning("Do you want to remove this job?");
                              break;

                    }
                    return true;
               }
          });
          popupMenu.show();
     }

     @Override
     public void setToolbar() {
          Toolbar toolbar = findViewById(R.id.toolbar);
          setSupportActionBar(toolbar);toolbar.setTitleTextColor(getResources().getColor(R.color.white));
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

     @SuppressLint("SetTextI18n")
     @Override
     public void processJsonData(String jsonData)
     {
          try {
               JSONObject jsonObject = new JSONObject(jsonData);
               JSONArray jsonArray = jsonObject.optJSONArray("info");
               JSONObject object = jsonArray.getJSONObject(0);
               String id = object.getString("id");
               path = object.getString("path");
               String file = object.getString("file");
               txtId.setText(file);
               txtChoose.setText("Change Resume");
          } catch (JSONException e) {
               e.printStackTrace();
          }

     }
     
     //upload image,show a alert box
     private void uploadResume(final Uri uri, final String fileName)
     {
          final AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setCancelable(false);
          builder.setTitle("Resume");
          builder.setMessage(fileName);
          if(!(fileName.endsWith(".docx")||fileName.endsWith(".pdf")))
          {
               Toast.makeText(MyCv.this,"Please select only pdf or docx file",Toast.LENGTH_LONG).show();
               return;
          }

          builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                    Map<String,String> maps = new HashMap<>();
                    maps.put("stdId",sharedPreferencesData.getCurrentUserId());//pass current user student id
                    maps.put("file",getBytes(uri));//pass file
                    if (fileName.endsWith(".pdf"))
                         maps.put("type","pdf");
                    else if(fileName.endsWith(".docx"))
                         maps.put("type","docx");
                    maps.put("date",methods.getDate());//current date
                    if(internetConnection.isOnline())
                    {
                         backgroundTask = new PostInfoBackgroundTask(MyCv.this,onResponseTask);
                         backgroundTask.insertData(getResources().getString(R.string.uploadCv),maps);
                    }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
               }
          });
          builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {}
          });

          builder.show();
     }

     //remove this resume
     private void removeResume()
     {
          Map<String,String> map = new HashMap<>();
          map.put("option","resume");
          map.put("stdId",sharedPreferencesData.getCurrentUserId());
          if(internetConnection.isOnline())
          {
               backgroundTask = new PostInfoBackgroundTask(this,getOnResponseTask);
               backgroundTask.insertData(getResources().getString(R.string.deleteOperation),map);
          }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
     }

     public void onActivityResult(int requestCode, int resultCode, Intent data) {
          if (resultCode == RESULT_OK&&data!=null) {
               Uri uri = data.getData();
               assert uri != null;
               String uriString = uri.toString();
               File file = new File(uriString);
               String fileName = file.getName();

               uploadResume(uri,fileName);
          }
     }

     //uri to get byts
     private String getBytes(Uri selectedImage)
     {
          assert selectedImage != null;
          InputStream iStream;
          byte[] inputData = new byte[0];
          try {
               iStream = getContentResolver().openInputStream(selectedImage);
               assert iStream != null;
               ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
               int bufferSize = 1024;
               byte[] buffer = new byte[bufferSize];

               int len ;
               while ((len = iStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
               }
               inputData = byteBuffer.toByteArray();
          } catch (IOException e) {
               e.printStackTrace();
          }
          return Base64.encodeToString(inputData,Base64.NO_WRAP);
     }

     //upload resume server response
     private OnResponseTask onResponseTask = new OnResponseTask() {
          @SuppressLint("SetTextI18n")
          @Override
          public void onResultSuccess(String value) {
               if(value.equals("success"))
               {
                    txtId.setVisibility(View.GONE);
                    imgDoc.setVisibility(View.VISIBLE);
                    txtChoose.setText("Change resume");
                    Toast.makeText(MyCv.this,"Your resume is uploaded",Toast.LENGTH_LONG).show();
               }
               else displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
          }
     };

     //get user resume at json format
     private OnResponseTask responseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               if(value.equals("empty"))
               {
                    imgDoc.setVisibility(View.INVISIBLE);
                    txtId.setVisibility(View.VISIBLE);
               }
               else processJsonData(value);
          }
     };

     //resume remove server response
     private OnResponseTask getOnResponseTask = new OnResponseTask() {
          @SuppressLint("SetTextI18n")
          @Override
          public void onResultSuccess(String value) {
               if(value.equals("success"))
               {
                    txtId.setText("No resume is uploaded yet ! Please choose PDF or docx file");
                    txtChoose.setText("Choose resume");
                    txtId.setVisibility(View.VISIBLE);
                    imgDoc.setVisibility(View.INVISIBLE);
                    Toast.makeText(MyCv.this,"Your resume is removed",Toast.LENGTH_LONG).show();
               }
               else displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
          }
     };

     //get confirmation for remove resume
     private BooleanResponse booleanResponse = new BooleanResponse() {
          @Override
          public void onCompleteResult(boolean flag) {
               if(flag)
                    removeResume();
          }
     };
}
