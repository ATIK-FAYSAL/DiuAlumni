package com.atik_faysal.diualumni.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.JobsAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.MyFabJob;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.models.JobsModel;
import com.atik_faysal.diualumni.others.AboutUs;
import com.atik_faysal.diualumni.others.Feedback;
import com.atik_faysal.diualumni.others.SetTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class JobPortal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,Methods
{

     private DrawerLayout drawerLayout;
     private ActionBarDrawerToggle mToggle;
     private SharedPreferencesData sharedPreferencesData;
     private DisplayMessage displayMessage;
     private NavigationView navigationView;
     private RecyclerView recyclerView;
     private LinearLayoutManager layoutManager;
     private PostInfoBackgroundTask backgroundTask;
     private CheckInternetConnection internetConnection;
     private RequireMethods methods;

     private TextView txtName,txtPhone,txtNoResult,txtNumOfJobs;
     private ProgressBar progressBar;
     private RelativeLayout emptyView;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.job_portal);
          drawerLayout = findViewById(R.id.drawer);
          mToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
          drawerLayout.setDrawerListener(mToggle);
          mToggle.syncState();
          Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
          initComponent();
          getAllPostedJobFromServer();
     }

     @Override
     protected void onStart() {
          super.onStart();
          if(sharedPreferencesData.getIsUserLogin())
          {
               Menu menu = navigationView.getMenu();
               menu.findItem(R.id.navSignInOut).setTitle("Sign out");
          }
          if(sharedPreferencesData.checkBoxStatus())
          {
               txtName.setText(sharedPreferencesData.getUserName());
               txtPhone.setText(sharedPreferencesData.getUserPhone());
          }else
          {
               txtPhone.setVisibility(View.INVISIBLE);
               txtName.setVisibility(View.INVISIBLE);
          }
     }

     //initialize component
     @Override
     public void initComponent()
     {
          navigationView = findViewById(R.id.nav_view);
          navigationView.setNavigationItemSelectedListener(this);
          View view = navigationView.inflateHeaderView(R.layout.nav_header);
          txtName = view.findViewById(R.id.txtName);
          txtPhone = view.findViewById(R.id.txtPhone);
          recyclerView = findViewById(R.id.jobList);
          txtNumOfJobs = findViewById(R.id.txtNumberOfJob);
          layoutManager = new LinearLayoutManager(this);
          emptyView = findViewById(R.id.emptyView);
          progressBar = findViewById(R.id.progressBar);
          txtNoResult = findViewById(R.id.txtNoResult);
          txtNoResult.setVisibility(View.INVISIBLE);
          SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
          refreshLayout.setColorSchemeResources(R.color.color1,R.color.color1,R.color.color1);

          sharedPreferencesData = new SharedPreferencesData(this);
          displayMessage = new DisplayMessage(this);
          backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);
          internetConnection = new CheckInternetConnection(this);
          methods = new RequireMethods(this);

          methods.reloadPage(refreshLayout,JobPortal.class);//reload this current page
     }

     //get all job and current user favourite jobs
     private void getAllPostedJobFromServer()
     {
          Map<String,String>maps = new HashMap<>();
          maps.put("option","allJobs");
          maps.put("stdId",sharedPreferencesData.getStudentId());
          if(internetConnection.isOnline())
               backgroundTask.InsertData(getString(R.string.readInfo),maps);
          else displayMessage.errorMessage(getString(R.string.noInternet));
     }

     //use toolbar ,still this method is not use
     @Override
     public void setToolbar() {}

     //process json data to view
     @Override
     public void processJsonData(String jsonData)
     {}

     //return list of information
     public List<JobsModel> processJson(String jsonData)
     {
          final List<JobsModel>jobsModels = new ArrayList<>();

          String name,title,description,education,deadLine,date,company,stdId,jobId;
          String requirement,type,category,salary,phone,email,experience;
          boolean flag;
          try {
               JSONObject rootObj = new JSONObject(jsonData);
               JSONArray rootArray = rootObj.optJSONArray("info");

               int count=0;
               while(count<rootArray.length())
               {
                    JSONObject dataObj = rootArray.getJSONObject(count);//object for data
                    JSONArray dataArray = dataObj.getJSONArray("data");//array for data
                    JSONObject userObj = dataArray.getJSONObject(0);//object for user info
                    JSONArray userArray = userObj.getJSONArray("person");//array for user info
                    JSONObject desObject = dataArray.getJSONObject(1);//object for description info
                    JSONArray desArray = desObject.getJSONArray("description");//array for job description info
                    JSONObject contactObj = dataArray.getJSONObject(2);//object for contact info
                    JSONArray contactArray = contactObj.getJSONArray("contact");//array for contact info
                    JSONObject dateObj = dataArray.getJSONObject(3);//object for date info
                    JSONArray dateArray = dateObj.getJSONArray("date");//array for date info

                    JSONObject object1 = userArray.getJSONObject(0);
                    JSONObject object2 = desArray.getJSONObject(0);
                    JSONObject object3 = contactArray.getJSONObject(0);
                    JSONObject object4 = dateArray.getJSONObject(0);


                    name = object1.getString("name");//person name
                    stdId = object1.getString("stdId");//person id
                    jobId = object2.getString("jobId");//job id
                    title = object2.getString("title");//job title
                    description = object2.getString("description");//job description
                    education = object2.getString("education");//job education
                    experience = object2.getString("experience");
                    requirement = object2.getString("requirement");//job requirement
                    type = object2.getString("type");//job type
                    category = object2.getString("category");//job category
                    salary = object2.getString("salary");//job salary
                    flag = object2.getBoolean("favJob");
                    company = object3.getString("company");//job company
                    phone = object3.getString("phone");//phone number
                    email = object3.getString("email");//email
                    date = object4.getString("postedDate");//job posted date
                    deadLine = object4.getString("deadLine");//dead line

                    jobsModels.add(new JobsModel(name,stdId,jobId,title,
                         description,education,experience,
                         requirement,type,category,salary,
                         company,phone,email,date,deadLine,flag));

                    count++;//increment
               }
          } catch (JSONException e) {
               //displayMessage.errorMessage(e.toString());
               Log.d("error",e.toString());
               return null;
          }
          return jobsModels;
     }

     //view all job information in UI
     private void viewJobInfo(String json)
     {
          final List<JobsModel>jobsModels = processJson(json);

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
                             txtNoResult.setVisibility(View.VISIBLE);//no result text visible
                             recyclerView.setVisibility(View.INVISIBLE);//list invisible
                             txtNumOfJobs.setText("0");
                        }
                        else//if jobs found
                        {
                             emptyView.setVisibility(View.INVISIBLE);//empty view invisible
                             recyclerView.setVisibility(View.VISIBLE);//no result text invisible
                             JobsAdapter adapter = new JobsAdapter(JobPortal.this, jobsModels,"jobPortal");//create adapter
                             recyclerView.setAdapter(adapter);//set adapter in recyler view
                             layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                             recyclerView.setLayoutManager(layoutManager);
                             recyclerView.setItemAnimator(new DefaultItemAnimator());
                             txtNumOfJobs.setText(String.valueOf(jobsModels.size()));
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

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
          return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
     }

     public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          // Handle navigation view item clicks here.

          switch (item.getItemId())
          {
               case R.id.navProfile:
                    Intent intent = new Intent(JobPortal.this,SetTabLayout.class);
                    intent.putExtra("user",sharedPreferencesData.getStudentId());
                    startActivity(intent);
                    break;
               case R.id.navAlumni:
                    startActivity(new Intent(JobPortal.this,AlumniMembers.class));
                    break;
               case R.id.navPost:
                    startActivity(new Intent(JobPortal.this,PostNewJob.class));
                    break;
               case R.id.navUpload:
                    Toast.makeText(JobPortal.this,"option 3",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navFabJob:
                    startActivity(new Intent(JobPortal.this, MyFabJob.class));
                    break;
               case R.id.navSetting:
                    Toast.makeText(JobPortal.this,"option 5",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navFeedback:
                    startActivity(new Intent(JobPortal.this, Feedback.class));
                    break;
               case R.id.navAbout:
                    startActivity(new Intent(JobPortal.this, AboutUs.class));
                    break;
               case R.id.navSignInOut:
                    startActivity(new Intent(JobPortal.this,SignIn.class));
                    break;
          }
          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
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
