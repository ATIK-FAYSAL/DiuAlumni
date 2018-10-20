package com.atik_faysal.diualumni.messages;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.AlumniAdapter;
import com.atik_faysal.diualumni.adapter.MessageAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.AlumniMembers;
import com.atik_faysal.diualumni.main.JobPortal;
import com.atik_faysal.diualumni.models.AlumniModel;
import com.atik_faysal.diualumni.models.ChatModel;
import com.atik_faysal.diualumni.models.MessageModel;

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

public class AllMessages extends AppCompatActivity implements Methods
{

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RelativeLayout emptyView;
    private TextView txtNoResult;
    private ProgressBar progressBar;
    private SharedPreferencesData sharedPreferencesData;
    private CheckInternetConnection internetConnection;
    private DisplayMessage displayMessage;
    private int modelSize = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alumnis);
        initComponent();//initialize
        setToolbar();//set toolbar
        retrieveChatList();//get chat List
        //reloadMessages();
    }

    //initialize all component
    @Override
    public void initComponent() {
        TextView txtResult = findViewById(R.id.txtNumberOfRResult);
        TextView txtFilter = findViewById(R.id.txtFilter);
        TextView txtText = findViewById(R.id.textView2);
        txtText.setVisibility(View.GONE);//invisible TextView field
        txtFilter.setVisibility(View.GONE);//invisible TextView field
        txtResult.setVisibility(View.GONE);//invisible TextView field

        recyclerView = findViewById(R.id.lists);
        emptyView = findViewById(R.id.emptyView);
        txtNoResult = findViewById(R.id.txtNoResult);
        txtNoResult.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.progressBar);
        layoutManager = new LinearLayoutManager(this);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setColorSchemeResources(R.color.color1,R.color.color1,R.color.color1);

        internetConnection = new CheckInternetConnection(this);
        sharedPreferencesData = new SharedPreferencesData(this);
        RequireMethods methods = new RequireMethods(this);
        methods.reloadPage(refreshLayout,AllMessages.class);//reload this current page
    }


    private void retrieveChatList()
    {
        Map<String,String> map = new HashMap<>();
        map.put("option","allMessages");
        map.put("sender",sharedPreferencesData.getCurrentUserId());

        if(internetConnection.isOnline())
        {
            PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);
            backgroundTask.insertData(getResources().getString(R.string.chatMessages),map);
        }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
    }

    //set toolbar in top of the activity
    @Override
    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));//set title text color white
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.icon_back);//set back icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//finish current activity
            }
        });
    }


    @Override
    public void processJsonData(String jsonData) {
        try {
            String message,count,imageUrl,personName,personId;
            List<ChatModel>modelList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.optJSONArray("chat");

            int counter=0;
            while (counter<jsonArray.length())
            {
                JSONObject object = jsonArray.getJSONObject(counter);
                personId = object.getString("person");
                personName = object.getString("name");
                message = object.getString("message");
                count = object.getString("count");
                imageUrl = object.getString("imageUrl");

                modelList.add(new ChatModel(personId,personName,message,count,imageUrl));
                counter++;
            }

            if(modelSize<modelList.size())
                viewInUI(modelList);

            modelSize = modelList.size();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void viewInUI(final List<ChatModel>modelList)
    {
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
                    }else//if result found
                    {
                        emptyView.setVisibility(View.INVISIBLE);//empty view invisible
                        recyclerView.setVisibility(View.VISIBLE);//no result text invisible
                        MessageAdapter adapter = new MessageAdapter(AllMessages.this, modelList);//create adapter
                        recyclerView.setAdapter(adapter);//set adapter in recyler view
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                    }
                    progressBar.setVisibility(View.GONE);
                    timer.cancel();

                }catch (NullPointerException e)
                {
                    Log.d("error",e.toString());
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
        },2000);
    }

    //start a thread to reload messages in every second
    private void reloadMessages()
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                retrieveChatList();
                handler.postDelayed(this,1000);
            }
        },1000);
    }

    private OnResponseTask onResponseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {
            processJsonData(value);
        }
    };
}
