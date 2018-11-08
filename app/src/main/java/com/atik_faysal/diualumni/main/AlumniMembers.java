package com.atik_faysal.diualumni.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.AlumniAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.models.AlumniModel;
import com.atik_faysal.diualumni.models.ContactModel;
import com.atik_faysal.diualumni.models.SearchModel;
import com.atik_faysal.diualumni.others.ContactSearchDialogCompat;
import com.atik_faysal.diualumni.others.FilterResult;
import com.atik_faysal.diualumni.others.NoInternetConnection;
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

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

public class AlumniMembers extends AppCompatActivity implements Methods
{
    private TextView numberOfResult,txtNoResult;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressBar progressBar;
    private RelativeLayout emptyView;
    private ProgressDialog progressDialog;

    private CheckInternetConnection internetConnection;
    private PostInfoBackgroundTask backgroundTask;
    private FilterResult filterResult;
    private ArrayList<ContactModel> searchModels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alumnis);
        initComponent();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(!internetConnection.isOnline())//if internet is not connect go to no internet page ,
        {
            String className = AlumniMembers.class.getName();
            Intent intent = new Intent(AlumniMembers.this,NoInternetConnection.class);
            intent.putExtra("class",className);//send current class name to NoInternetConnection class
            startActivity(intent);
            finish();
        }
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
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setColorSchemeResources(R.color.color1,R.color.color1,R.color.color1);

        layoutManager = new LinearLayoutManager(this);
        internetConnection = new CheckInternetConnection(this);
        RequireMethods methods = new RequireMethods(this);
        backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);
        filterResult = new FilterResult(this);

        txtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterResult.filterAlumni();
                filterResult.onSuccessListener(responseTask);
            }
        });

        //calling method
        retrieveAlumni();//get all alumni information
        methods.reloadPage(refreshLayout,AlumniMembers.class);
        setToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id;
        res_id = item.getItemId();
        if(res_id==R.id.search)
        {
            if(internetConnection.isOnline())
            {
                new ContactSearchDialogCompat<>(AlumniMembers.this, "Search...",
                        "What are you looking for...?", null, searchModels,
                        new SearchResultListener<ContactModel>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog, ContactModel item, int position) {

                                Intent intent = new Intent(AlumniMembers.this, SetTabLayout.class);
                                intent.putExtra("user",item.getStdId());
                                startActivity(intent);

                                dialog.dismiss();
                            }
                        }
                ).show();
            }
        }
        return true;
    }


    //get member list from server
    private void retrieveAlumni()
    {
        Map<String,String>maps = new HashMap<>();
        maps.put("option","alumni");
        if(internetConnection.isOnline())
            backgroundTask.insertData(getResources().getString(R.string.readInfo),maps);
    }

    @Override
    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);toolbar.setTitleTextColor(getResources().getColor(R.color.white));
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
    public void processJsonData(String jsonData)
    {}

    public List<AlumniModel> jsonDataProcess(String jsonData)
    {
        String name,id,phone,email,gender,batch,imageName;
        final List<AlumniModel>modelList = new ArrayList<>();
        searchModels = new ArrayList<>();

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
                imageName = object.getString("imageName");
                String position = object.getString("position");
                String company = object.getString("company");
                String department = object.getString("department");
                searchModels.add(new ContactModel(name,id,imageName));
                modelList.add(new AlumniModel(name,id,gender,batch,phone,email,imageName,department,company,position,""));
                count++;
            }
        } catch (JSONException | NullPointerException e) {
            Log.d("error",e.toString());
            return null;
        }

        return modelList;
    }

    //view all job information in UI
    private void viewResultInUi(String json)
    {
        final List<AlumniModel>modelList = jsonDataProcess(json);

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
                    } else//if result found
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

    OnResponseTask onResponseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(final String value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewResultInUi(value);
                }
            });
        }
    };

    OnResponseTask responseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {
            progressDialog.show();
            viewResultInUi(value);
        }
    };
}