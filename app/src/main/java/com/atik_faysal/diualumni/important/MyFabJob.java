package com.atik_faysal.diualumni.important;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.atik_faysal.diualumni.adapter.JobsAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.AlumniMembers;
import com.atik_faysal.diualumni.main.JobPortal;
import com.atik_faysal.diualumni.models.JobsModel;
import com.atik_faysal.diualumni.others.NoInternetConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyFabJob extends AppCompatActivity implements Methods
{
     private ProgressBar progressBar;
     private RelativeLayout emptyView;
     private TextView numOfResult,txtNoresult;

     private RecyclerView recyclerView;
     private LinearLayoutManager layoutManager;
     private PostInfoBackgroundTask backgroundTask;
     private CheckInternetConnection internetConnection;
     private RequireMethods methods;
     private DisplayMessage displayMessage;
     private SharedPreferencesData sharedPreferencesData;
     private JobPortal portal;


     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.alumnis);
          initComponent();
     }


     @Override
     protected void onStart() {
          super.onStart();
          if(!internetConnection.isOnline())//if internet is not connect go to no internet page ,
          {
               String className = MyFabJob.class.getName();
               Intent intent = new Intent(MyFabJob.this,NoInternetConnection.class);
               intent.putExtra("class",className);//send current class name to NoInternetConnection class
               startActivity(intent);
               finish();
          }
     }

     public void initComponent()
     {
          progressBar = findViewById(R.id.progressBar);
          recyclerView = findViewById(R.id.lists);
          emptyView = findViewById(R.id.emptyView);
          txtNoresult = findViewById(R.id.txtNoResult);
          numOfResult = findViewById(R.id.txtNumberOfRResult);
          txtNoresult.setVisibility(View.INVISIBLE);
          SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
          refreshLayout.setColorSchemeResources(R.color.color1,R.color.color1,R.color.color1);

          layoutManager = new LinearLayoutManager(this);
          backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);
          internetConnection = new CheckInternetConnection(this);
          methods = new RequireMethods(this);
          displayMessage = new DisplayMessage(this);
          sharedPreferencesData = new SharedPreferencesData(this);
          portal = new JobPortal();

          //calling method
          setToolbar();
          retrieveMyFavJob();//retrieve my fav job
          methods.reloadPage(refreshLayout,MyFabJob.class);
     }

     //set toolbar
     @Override
     public void setToolbar()
     {
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
     public void processJsonData(String jsonData) {

     }

     //get all job and current user favourite jobs
     private void retrieveMyFavJob()
     {
          Map<String,String> maps = new HashMap<>();
          maps.put("option","fabJob");
          maps.put("stdId",sharedPreferencesData.getCurrentUserId());
          if(internetConnection.isOnline())
               backgroundTask.InsertData(getString(R.string.readInfo),maps);
     }

     //view all job information in UI
     private void viewJobInfo(String json)
     {
          final List<JobsModel> jobsModels = portal.processJson(json);

          //add progress bar ...
          final Timer timer = new Timer();
          final Handler handler = new Handler();
          final  Runnable runnable = new Runnable() {
               @Override
               public void run() {
                    try {
                         if(jobsModels.isEmpty())//if no jobs found
                         {
                              emptyView.setVisibility(View.VISIBLE);//empty view visible
                              txtNoresult.setVisibility(View.VISIBLE);//no result text visible
                              recyclerView.setVisibility(View.INVISIBLE);//list invisible
                              numOfResult.setText("0");
                         }
                         else//if jobs found
                         {
                              emptyView.setVisibility(View.INVISIBLE);//empty view invisible
                              recyclerView.setVisibility(View.VISIBLE);//no result text invisible
                              JobsAdapter adapter = new JobsAdapter(MyFabJob.this, jobsModels,"fabJob");//create adapter
                              recyclerView.setAdapter(adapter);//set adapter in recyler view
                              layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                              recyclerView.setLayoutManager(layoutManager);
                              recyclerView.setItemAnimator(new DefaultItemAnimator());
                              numOfResult.setText(String.valueOf(jobsModels.size()));
                         }
                         progressBar.setVisibility(View.GONE);
                         timer.cancel();
                    }catch (NullPointerException e)
                    {
                         displayMessage.errorMessage(e.toString());
                    }
               }
          };
          timer.schedule(new TimerTask() {
               @Override
               public void run() {
                    handler.post(runnable);
               }
          },2800);
     }

     //get json data from server
     OnResponseTask onResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         viewJobInfo(value);
                    }
               });
          }
     };
}
