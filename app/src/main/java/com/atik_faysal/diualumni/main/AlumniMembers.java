package com.atik_faysal.diualumni.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.AlumniAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.models.AlumniModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AlumniMembers extends AppCompatActivity implements Methods
{
     private TextView numberOfResult,txtNoResult;
     private RecyclerView recyclerView;
     private LinearLayoutManager layoutManager;
     private ProgressBar progressBar;
     private RelativeLayout emptyView;

     private DisplayMessage displayMessage;
     private CheckInternetConnection internetConnection;
     private RequireMethods methods;
     private PostInfoBackgroundTask backgroundTask;

     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.alumnis);
          initComponent();
     }

     @Override
     public void initComponent() {
          numberOfResult = findViewById(R.id.txtNumberOfRResult);
          recyclerView = findViewById(R.id.lists);
          progressBar = findViewById(R.id.progressBar);
          txtNoResult = findViewById(R.id.txtNoResult);
          txtNoResult.setVisibility(View.INVISIBLE);
          emptyView = findViewById(R.id.emptyView);
          SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
          refreshLayout.setColorSchemeResources(R.color.color1,R.color.color1,R.color.color1);

          layoutManager = new LinearLayoutManager(this);
          displayMessage = new DisplayMessage(this);
          internetConnection = new CheckInternetConnection(this);
          methods = new RequireMethods(this);
          backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);

          //calling method
          retrieveAlumnis();//get all alumni information
          methods.reloadPage(refreshLayout,AlumniMembers.class);//reload this current page
          setToolbar();
     }

     //get member list from server
     private void retrieveAlumnis()
     {
          Map<String,String>maps = new HashMap<>();
          maps.put("option","alumni");
          if(internetConnection.isOnline())
               backgroundTask.InsertData(getResources().getString(R.string.readInfo),maps);
          else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
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

     @Override
     public void processJsonData(String jsonData)
     {
          String name,id,phone,email,gender,batch;
          final List<AlumniModel>modelList = new ArrayList<>();

          try {
               JSONObject rootObj = new JSONObject(jsonData);
               JSONArray rootArray = rootObj.optJSONArray("alumni");
               int count=0;
               while (count<rootArray.length())
               {
                    JSONObject object = rootArray.getJSONObject(count);
                    name = object.getString("name");
                    id = object.getString("id");
                    gender = object.getString("gender");
                    batch = object.getString("batch");
                    phone = object.getString("phone");
                    email = object.getString("email");
                    modelList.add(new AlumniModel(name,id,gender,batch,phone,email));
                    count++;
               }
          } catch (JSONException e) {
               displayMessage.errorMessage(e.toString());
          }finally {
               final Timer timer = new Timer();
               final Handler handler = new Handler();
               final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                         try {
                              if (modelList.isEmpty())//if no jobs found
                              {
                                   emptyView.setVisibility(View.VISIBLE);//empty view visible
                                   txtNoResult.setVisibility(View.VISIBLE);//no result text visible
                                   recyclerView.setVisibility(View.INVISIBLE);//list invisible
                                   numberOfResult.setText("0");
                              } else//if jobs found
                              {
                                   emptyView.setVisibility(View.INVISIBLE);//empty view invisible
                                   recyclerView.setVisibility(View.VISIBLE);//no result text invisible
                                   AlumniAdapter adapter = new AlumniAdapter(AlumniMembers.this, modelList);//create adapter
                                   recyclerView.setAdapter(adapter);//set adapter in recyler view
                                   layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                   recyclerView.setLayoutManager(layoutManager);
                                   recyclerView.setItemAnimator(new DefaultItemAnimator());
                                   numberOfResult.setText(String.valueOf(modelList.size()));
                              }
                              progressBar.setVisibility(View.GONE);
                              timer.cancel();
                         } catch (NullPointerException e) {
                              displayMessage.errorMessage(e.toString());
                         }
                    }
               };
               timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                         handler.post(runnable);
                    }
               },2500);
          }
     }

     OnResponseTask onResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         if(value!=null)
                              processJsonData(value);
                    }
               });
          }
     };
}
