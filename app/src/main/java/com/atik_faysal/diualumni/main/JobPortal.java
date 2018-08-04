package com.atik_faysal.diualumni.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.JobsAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.models.JobsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

     private TextView txtName,txtPhone;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.job_portal);
          drawerLayout = findViewById(R.id.drawer);
          mToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
          drawerLayout.setDrawerListener(mToggle);
          mToggle.syncState();
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

     @Override
     public void initComponent()
     {
          navigationView = findViewById(R.id.nav_view);
          navigationView.setNavigationItemSelectedListener(this);
          View view = navigationView.inflateHeaderView(R.layout.nav_header);
          txtName = view.findViewById(R.id.txtName);
          txtPhone = view.findViewById(R.id.txtPhone);
          recyclerView = findViewById(R.id.jobList);
          layoutManager = new LinearLayoutManager(this);

          sharedPreferencesData = new SharedPreferencesData(this);
          displayMessage = new DisplayMessage(this);
          backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);
          internetConnection = new CheckInternetConnection(this);
     }

     private void getAllPostedJobFromServer()
     {
          Map<String,String>maps = new HashMap<>();
          maps.put("option","allJobs");
          if(internetConnection.isOnline())
               backgroundTask.InsertData(getString(R.string.getPostedJob),maps);
          else displayMessage.errorMessage(getString(R.string.noInternet));
     }

     @Override
     public void setToolbar() {}

     @Override
     public void processJsonData(String jsonData)
     {
          List<JobsModel>jobsModels = new ArrayList<>();

          String name,title,description,education,deadLine,date,company,stdId,jobId;
          String requirement,type,category,salary,phone,email,experience;
          boolean flag;
          try {
               JSONObject rootObj = new JSONObject(jsonData);
               JSONArray rootArray = rootObj.optJSONArray("info");

               int count=0;
               while(count<rootArray.length())
               {
                    JSONObject dataObj = rootArray.getJSONObject(count);
                    JSONArray dataArray = dataObj.getJSONArray("data");
                    JSONObject userObj = dataArray.getJSONObject(0);
                    JSONArray userArray = userObj.getJSONArray("person");
                    JSONObject desObject = dataArray.getJSONObject(1);
                    JSONArray desArray = desObject.getJSONArray("description");
                    JSONObject contactObj = dataArray.getJSONObject(2);
                    JSONArray contactArray = contactObj.getJSONArray("contact");
                    JSONObject dateObj = dataArray.getJSONObject(3);
                    JSONArray dateArray = dateObj.getJSONArray("date");

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

                    jobsModels.add(new JobsModel(name,stdId,jobId,title,description,education,experience,requirement,type,category,salary,company,phone,email,date,deadLine,flag));
                    count++;
               }
          } catch (JSONException e) {
               Log.d("json error1",e.toString());
          }finally {
               JobsAdapter adapter = new JobsAdapter(JobPortal.this, jobsModels);
               recyclerView.setAdapter(adapter);
               layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
               recyclerView.setLayoutManager(layoutManager);
               recyclerView.setItemAnimator(new DefaultItemAnimator());
          }
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
          return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
     }

     public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          // Handle navigation view item clicks here.

          switch (item.getItemId())
          {
               case R.id.navAlumni:
                    Toast.makeText(JobPortal.this,"option 1",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navPost:
                    startActivity(new Intent(JobPortal.this,PostNewJob.class));
                    break;
               case R.id.navUpload:
                    Toast.makeText(JobPortal.this,"option 3",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navFabJob:
                    Toast.makeText(JobPortal.this,"option 4",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navSetting:
                    Toast.makeText(JobPortal.this,"option 5",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navFeedback:
                    Toast.makeText(JobPortal.this,"option 6",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navAbout:
                    Toast.makeText(JobPortal.this,"option 7",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navSignInOut:
                    startActivity(new Intent(JobPortal.this,SignIn.class));
                    break;
          }
          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
     }

     OnResponseTask onResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         processJsonData(value);
                    }
               });
          }
     };
}
