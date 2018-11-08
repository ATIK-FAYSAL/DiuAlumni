package com.atik_faysal.diualumni.others;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.AlumniAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.AlumniMembers;
import com.atik_faysal.diualumni.models.AlumniModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class AppliedHistory extends AppCompatActivity implements Methods
{

    private TextView numberOfResult,txtNoResult;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressBar progressBar;
    private RelativeLayout emptyView;
    private ProgressDialog progressDialog;

    private CheckInternetConnection internetConnection;
    private AlumniMembers alumniMembers;

    private static String jobId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alumnis);
        initComponent();//initialize all component
        retrieveHistory();//retrieve history from server
        setToolbar();//toolbar at top
    }



    @Override
    public void initComponent() {
        numberOfResult = findViewById(R.id.txtNumberOfRResult);
        recyclerView = findViewById(R.id.lists);
        progressBar = findViewById(R.id.progressBar);
        txtNoResult = findViewById(R.id.txtNoResult);
        txtNoResult.setVisibility(View.INVISIBLE);
        emptyView = findViewById(R.id.emptyView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Searching data");
        TextView txtFilter = findViewById(R.id.txtFilter);
        txtFilter.setVisibility(View.GONE);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setEnabled(false);

        jobId = Objects.requireNonNull(getIntent().getExtras()).getString("jobId");//get jobId from jobAdapter

        internetConnection = new CheckInternetConnection(this);
        layoutManager = new LinearLayoutManager(this);
        alumniMembers = new AlumniMembers();

    }

    //get information about person who has applied this job
    private void retrieveHistory()
    {
        Map<String,String> map = new HashMap<>();
        map.put("option","history");
        map.put("jobId",jobId);

        if(internetConnection.isOnline())
        {
            PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(this, onResponseTask);
            backgroundTask.insertData(getResources().getString(R.string.readInfo),map);
        }
    }

    @Override
    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
    public void processJsonData(String jsonData) {}

    private void historyOfJob(String json)
    {
        final List<AlumniModel> modelList = alumniMembers.jsonDataProcess(json);


        //add progress bar ...
        final Timer timer = new Timer();
        final Handler handler = new Handler();
        final  Runnable runnable = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                try {
                    if (modelList==null)//if no results found
                    {
                        emptyView.setVisibility(View.VISIBLE);//empty view visible
                        txtNoResult.setVisibility(View.VISIBLE);//no result text visible
                        recyclerView.setVisibility(View.INVISIBLE);//list invisible
                        numberOfResult.setText("0");
                        Log.d("error1","empty");
                    } else//if result found
                    {
                        emptyView.setVisibility(View.INVISIBLE);//empty view invisible
                        recyclerView.setVisibility(View.VISIBLE);//no result text invisible
                        AlumniAdapter adapter = new AlumniAdapter(AppliedHistory.this, modelList);//create adapter
                        recyclerView.setAdapter(adapter);//set adapter in recycler view
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        numberOfResult.setText(String.valueOf(modelList.size()));
                        Log.d("error","done");
                    }
                    progressBar.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    timer.cancel();

                }catch (NullPointerException e)
                {
                    Log.d("error2",e.toString());
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

    private OnResponseTask onResponseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {
            historyOfJob(value);
        }
    };

}
