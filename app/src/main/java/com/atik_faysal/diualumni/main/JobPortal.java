package com.atik_faysal.diualumni.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.JobsAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.RegisterDeviceToken;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.MyFabJob;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.messages.AllMessages;
import com.atik_faysal.diualumni.messages.PersonMessage;
import com.atik_faysal.diualumni.models.JobsModel;
import com.atik_faysal.diualumni.others.AboutUs;
import com.atik_faysal.diualumni.others.Feedback;
import com.atik_faysal.diualumni.others.FilterResult;
import com.atik_faysal.diualumni.others.NoInternetConnection;
import com.atik_faysal.diualumni.others.SetTabLayout;
import com.atik_faysal.diualumni.others.UserPermission;
import com.bumptech.glide.Glide;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

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

import de.hdodenhof.circleimageview.CircleImageView;


public class JobPortal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,Methods
{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private SharedPreferencesData sharedPreferencesData;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private PostInfoBackgroundTask backgroundTask;
    private CheckInternetConnection internetConnection;
    private RequireMethods methods;
    private FilterResult filterResult;
    private ProgressDialog progressDialog;
    private DisplayMessage displayMessage;
    private UserPermission permission;

    private TextView txtName,txtPhone,txtNoResult,txtNumOfJobs;
    private ProgressBar progressBar;
    private RelativeLayout emptyView;
    private CircleImageView imgUser;
    private SwitchCompat switchCompat;
    private FloatingActionButton fab;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_portal);
        FirebaseApp.initializeApp(JobPortal.this);
        drawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.setDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //calling method
        initComponent();//initialize component
        getAllPostedJobFromServer();//get job from server
        closeApp();//exit from app
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onStart() {
        super.onStart();

        permission.userPermission();

        if(!internetConnection.isOnline())//if internet is not connect go to no internet page ,
        {
            String className = JobPortal.class.getName();
            Intent intent = new Intent(JobPortal.this,NoInternetConnection.class);
            intent.putExtra("class",className);//send current class name to NoInternetConnection class
            startActivity(intent);
            finish();
        }
        Menu menu = navigationView.getMenu();
        if(sharedPreferencesData.getIsUserLogin())
        {
            menu.findItem(R.id.navSignInOut).setTitle("Sign out");
            txtName.setText(sharedPreferencesData.getUserName());
            txtPhone.setText(sharedPreferencesData.getUserPhone());
            if(sharedPreferencesData.getNotificationSettings().equals("enable"))
            {
                switchCompat.setChecked(true);
                menu.findItem(R.id.navStopNotification).setTitle("Disable notification");
                menu.findItem(R.id.navStopNotification).setIcon(R.drawable.icon_notification_off);
            }else{
                switchCompat.setChecked(false);
                menu.findItem(R.id.navStopNotification).setTitle("Enable notification");
                menu.findItem(R.id.navStopNotification).setIcon(R.drawable.icon_notification_on);
            }
            retrieveUserImage();//get image from server using glide and show

            if(sharedPreferencesData.getMessageSettings().equals("disable"))
                fab.setVisibility(View.GONE);
        }else
        {
            menu.findItem(R.id.navSignInOut).setTitle("Sign in");
            txtPhone.setVisibility(View.INVISIBLE);
            txtName.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.GONE);
        }
    }

    private void retrieveUserImage()
    {
        if(!sharedPreferencesData.getImageName().equals("none"))
            Glide.with(this).
                    load(getResources().getString(R.string.address)+sharedPreferencesData.getImageName()+".png").
                    into(imgUser);
    }

    //when user click on back button,terminate app
    @Override
    public void onBackPressed() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        iOSDialogBuilder builder = new iOSDialogBuilder(JobPortal.this);

        builder.setTitle("App termination")
                .setSubtitle("Do you want to close app?")
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("Close App",new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        Intent intent = new Intent(getApplicationContext(), JobPortal.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("flag",true);
                        startActivity(intent);
                        dialog.dismiss();

                    }
                })
                .setNegativeListener("cancel", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    //exit from app
    private void closeApp()
    {
        if(getIntent().getBooleanExtra("flag",false))finish();
    }

    //initialize component
    @Override
    public void initComponent()
    {

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.navStopNotification);
        View actionView = MenuItemCompat.getActionView(menuItem);
        txtName = view.findViewById(R.id.txtName);
        txtPhone = view.findViewById(R.id.txtPhone);
        switchCompat = actionView.findViewById(R.id.drawer_switch);
        recyclerView = findViewById(R.id.jobList);
        txtNumOfJobs = findViewById(R.id.txtNumberOfJob);
        layoutManager = new LinearLayoutManager(this);
        emptyView = findViewById(R.id.emptyView);
        progressBar = findViewById(R.id.progressBar);
        txtNoResult = findViewById(R.id.txtNoResult);
        txtNoResult.setVisibility(View.INVISIBLE);
        imgUser = view.findViewById(R.id.imgUserImage);
        TextView txtFilter = findViewById(R.id.txtFilter);
        fab = findViewById(R.id.fab);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Searching data");
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setColorSchemeResources(R.color.color1,R.color.color1,R.color.color1);

        sharedPreferencesData = new SharedPreferencesData(this);
        backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);
        internetConnection = new CheckInternetConnection(this);
        methods = new RequireMethods(this);
        filterResult = new FilterResult(this);
        displayMessage = new DisplayMessage(this);
        permission = new UserPermission(this);

        methods.reloadPage(refreshLayout,JobPortal.class);//reload this current page

        if(sharedPreferencesData.getIsUserLogin())
        {
            FirebaseMessaging.getInstance().subscribeToTopic("DiuAlumni");
            String token = FirebaseInstanceId.getInstance().getToken();
            RegisterDeviceToken.registerToken(token,sharedPreferencesData.getCurrentUserId(),
                    sharedPreferencesData.getNotificationSettings());
        }

        //switch button,user can disable or enable by this button
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String setting ;
                Map<String,String>map = new HashMap<>();
                map.put("option","notification");
                map.put("stdId",sharedPreferencesData.getCurrentUserId());

                if(sharedPreferencesData.getNotificationSettings().equals("enable"))
                    setting = "disable";
                else setting = "enable";
                map.put("setting",setting);
                if(internetConnection.isOnline())
                {
                    backgroundTask = new PostInfoBackgroundTask(JobPortal.this,task);
                    backgroundTask.insertData(getResources().getString(R.string.updateOperation),map);
                }
            }
        });

        //filter/search job by deadline,location,category
        txtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterResult.onSuccessListener(responseTask);
                filterResult.filterJob();
            }
        });

        //fab button,message option,
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JobPortal.this,AllMessages.class));
                //startActivity(new Intent(JobPortal.this,PersonMessage.class));
            }
        });
    }

    //get all job and current user favourite jobs
    private void getAllPostedJobFromServer()
    {
        Map<String,String>maps = new HashMap<>();
        maps.put("option","allJobs");
        maps.put("stdId",sharedPreferencesData.getCurrentUserId());
        if(internetConnection.isOnline())
            backgroundTask.insertData(getString(R.string.readInfo),maps);
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

        String name,mType,title,description,education,deadLine,date,company,stdId,jobId;
        String requirement,type,category,salary,phone,email,experience,city,vacancy,comUrl,comAddress;
        boolean favJob,appliedJob;
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
                mType = object1.getString("mtype");
                jobId = object2.getString("jobId");//job id
                title = object2.getString("title");//job title
                description = object2.getString("description");//job description
                education = object2.getString("education");//job education
                experience = object2.getString("experience");
                requirement = object2.getString("requirement");//job requirement
                type = object2.getString("type");//job type
                city = object2.getString("city");
                category = object2.getString("category");//job category
                salary = object2.getString("salary");//job salary
                vacancy = object2.getString("vacancy");//number of vacancy
                favJob = object2.getBoolean("favJob");
                appliedJob = object2.getBoolean("appliedJob");
                company = object3.getString("company");//job company
                phone = object3.getString("phone");//phone number
                email = object3.getString("email");//email
                comAddress = object3.getString("comAddress");//company address
                comUrl = object3.getString("comUrl");//company url
                date = object4.getString("postedDate");//job posted date
                deadLine = object4.getString("deadLine");//dead line

                jobsModels.add(new JobsModel(name,stdId,mType,jobId,title,
                        description,education,experience,city,
                        requirement,type,category,salary,vacancy,
                        company,comUrl,comAddress,phone,email,date,deadLine,favJob,appliedJob));

                count++;//increment
            }
        } catch (JSONException | NullPointerException e) {
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
            @SuppressLint("SetTextI18n")
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
                    progressDialog.dismiss();
                    timer.cancel();
                }catch (NullPointerException e)
                {
                    txtNoResult.setVisibility(View.VISIBLE);
                    txtNoResult.setText(getResources().getString(R.string.noResult));
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },getResources().getInteger(R.integer.progTime));
    }

    //sing out
    private void userSignOut()
    {
        final ProgressDialog progressDialog = ProgressDialog.show(JobPortal.this, "Please wait", "User Sign out...", true);
        progressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (Exception e) {
                }
                progressDialog.dismiss();
            }
        }).start();
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sharedPreferencesData.clearData();
                sharedPreferencesData.isUserLogin(false);
                methods.closeActivity(JobPortal.this,JobPortal.class);
            }
        });
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
                if(sharedPreferencesData.getIsUserLogin())
                {
                    Intent intent = new Intent(JobPortal.this,SetTabLayout.class);
                    intent.putExtra("user",sharedPreferencesData.getCurrentUserId());//send current user id to setTabLayout class
                    startActivity(intent);
                }else
                    startActivity(new Intent(JobPortal.this,SignIn.class));
                break;

            case R.id.navAlumni:
                startActivity(new Intent(JobPortal.this,AlumniMembers.class));
                break;

            case R.id.navPost:
                if(sharedPreferencesData.getIsUserLogin())
                    startActivity(new Intent(JobPortal.this,PostNewJob.class));
                else startActivity(new Intent(JobPortal.this,SignIn.class));
                break;

            case R.id.navUpload:
                if(sharedPreferencesData.getIsUserLogin())
                    startActivity(new Intent(JobPortal.this,MyCv.class));
                else startActivity(new Intent(JobPortal.this,SignIn.class));
                break;

            case R.id.navFabJob:
                if(sharedPreferencesData.getIsUserLogin())
                    startActivity(new Intent(JobPortal.this, MyFabJob.class));
                else startActivity(new Intent(JobPortal.this,SignIn.class));
                break;

            case R.id.navChangePass:
                methods.changePassword();//change password
                break;

//               case R.id.navStopNotification:
//                    if(switchCompat.isChecked())
//                    {
//                         switchCompat.setChecked(false);
//                         sharedPreferencesData.setNotificationSettings("disable");
//                         menu.findItem(R.id.navStopNotification).setTitle("Disable notification");
//                         menu.findItem(R.id.navStopNotification).setIcon(R.drawable.icon_notification_off);
//                    }
//                    else{
//                         switchCompat.setChecked(true);
//                         sharedPreferencesData.setNotificationSettings("enable");
//                         menu.findItem(R.id.navStopNotification).setTitle("Enable notification");
//                         menu.findItem(R.id.navStopNotification).setIcon(R.drawable.icon_notification_on);
//                    }
//
//                    break;

            case R.id.navFeedback:
                if(sharedPreferencesData.getIsUserLogin())
                    startActivity(new Intent(JobPortal.this, Feedback.class));
                else startActivity(new Intent(JobPortal.this,SignIn.class));
                break;

            case R.id.navAbout:
                startActivity(new Intent(JobPortal.this, AboutUs.class));
                break;

            case R.id.navSignInOut:
                if(sharedPreferencesData.getIsUserLogin())
                    userSignOut();
                else
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

    //filter job response
    private OnResponseTask responseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {
            if(value!=null)
            {
                progressDialog.show();
                viewJobInfo(value);
            }
        }
    };


    private OnResponseTask task = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {
            if(!value.equals("success"))
                displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
            else {
                if(switchCompat.isChecked())
                {
                    switchCompat.setChecked(true);
                    sharedPreferencesData.setNotificationSettings("enable");
                    menu.findItem(R.id.navStopNotification).setTitle("Disable notification");
                    menu.findItem(R.id.navStopNotification).setIcon(R.drawable.icon_notification_on);
                }
                else{
                    switchCompat.setChecked(false);
                    sharedPreferencesData.setNotificationSettings("disable");
                    menu.findItem(R.id.navStopNotification).setTitle("Enable notification");
                    menu.findItem(R.id.navStopNotification).setIcon(R.drawable.icon_notification_off);
                }
            }
        }
    };
}
