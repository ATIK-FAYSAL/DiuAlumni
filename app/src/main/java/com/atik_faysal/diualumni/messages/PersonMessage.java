package com.atik_faysal.diualumni.messages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.JobsAdapter;
import com.atik_faysal.diualumni.adapter.MessageAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.JobPortal;
import com.atik_faysal.diualumni.models.MessageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PersonMessage extends AppCompatActivity implements Methods
{

    private EditText txtMessage;
    private ImageView bSend;
    private RecyclerView recyclerView;
    private CheckInternetConnection internetConnection;
    private PostInfoBackgroundTask backgroundTask;
    private RequireMethods methods;
    private SharedPreferencesData sharedPreferencesData;
    private DisplayMessage displayMessage;
    private RelativeLayout emptyView;
    private TextView txtNoResult;
    private LinearLayoutManager layoutManager;
    private Thread thread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_message);
        initComponent();//initialize all component
        retrieveMessage();//get all chat message from db
        reloadMessages();
        setToolbar();//set toolbar in top
    }


    @Override
    public void initComponent() {
        txtMessage = findViewById(R.id.txtTextMsg);
        bSend = findViewById(R.id.imgSend);
        bSend.setVisibility(View.INVISIBLE);//send button invisible,
        recyclerView = findViewById(R.id.msgList);
        emptyView = findViewById(R.id.emptyView);
        txtNoResult = findViewById(R.id.txtNoResult);
        txtNoResult.setVisibility(View.GONE);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(this);

        internetConnection = new CheckInternetConnection(this);
        methods = new RequireMethods(this);
        sharedPreferencesData = new SharedPreferencesData(this);
        displayMessage = new DisplayMessage(this);

        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!txtMessage.getText().toString().trim().isEmpty())//if input text has some value then send button is visible otherwise it's invisible
                    bSend.setVisibility(View.VISIBLE);//Visible send button
                else bSend.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,String> map = new HashMap<>();
                String message = txtMessage.getText().toString();
                String sender = sharedPreferencesData.getCurrentUserId();
                String receiver = "161-35-1413";
                String time = methods.getDateWithTime();

                if(!message.isEmpty()&&!sender.isEmpty()&&!receiver.isEmpty()&&!time.isEmpty())
                {
                    map.put("option","message");//option for call method
                    map.put("sender",sender);//sender id
                    map.put("receiver",receiver);//receiver id
                    map.put("message",message);//text message
                    map.put("time",time);//sending time
                }else return;

                if(internetConnection.isOnline())
                {
                    backgroundTask = new PostInfoBackgroundTask(PersonMessage.this,responseTask);
                    backgroundTask.insertData(getResources().getString(R.string.chatMessages),map);
                }else
                    displayMessage.errorMessage(getResources().getString(R.string.noInternet));//if no internet access,show a message

                txtMessage.setText("");//empty input text
            }
        });
    }

    //retrieve all message from db,recall this method after send message
    private void retrieveMessage()
    {
        Map<String,String>map = new HashMap<>();

        String sender = sharedPreferencesData.getCurrentUserId();
        String receiver = "161-35-1413";
        if(!sender.isEmpty()&&!receiver.isEmpty())
        {
            map.put("option","fetchMsg");
            map.put("sender",sender);
            map.put("receiver",receiver);
        }else return;

        if(internetConnection.isOnline())
        {
            backgroundTask = new PostInfoBackgroundTask(PersonMessage.this,onResponseTask);
            backgroundTask.insertData(getResources().getString(R.string.chatMessages),map);
        }else
            displayMessage.errorMessage(getResources().getString(R.string.noInternet));
    }

    //set toolbar in top of the layout
    @Override
    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Atik Faysal");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thread.isAlive())
                    thread.interrupt();//interrupt running thread
                finish();//finish current activity
            }
        });
    }

    //process json data to messages
    @Override
    public void processJsonData(String jsonData) {

        List<MessageModel> models = new ArrayList<>();

        try {
            String sender,receiver,time,text,messageId;

            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            int count=0;
            while(count<jsonArray.length())
            {
                JSONObject object = jsonArray.getJSONObject(count);
                messageId = object.getString("messageId");
                sender = object.getString("sender");
                receiver = object.getString("receiver");
                time = object.getString("time");
                text = object.getString("message");

                models.add(new MessageModel(messageId,sender,receiver,text,time));
                count++;
            }

            viewAllMessage(models);//all data show on UI

        } catch (JSONException e) {
            Log.d("error",e.toString());
        }

    }

    //view messages in ui
    private void viewAllMessage(List<MessageModel>models)
    {
        if(models.isEmpty())//if no jobs found
        {
            emptyView.setVisibility(View.VISIBLE);//empty view visible
            txtNoResult.setVisibility(View.VISIBLE);//no result text visible
            recyclerView.setVisibility(View.INVISIBLE);//list invisible
        }
        else//if jobs found
        {
            emptyView.setVisibility(View.INVISIBLE);//empty view invisible
            recyclerView.setVisibility(View.VISIBLE);//no result text invisible
            MessageAdapter adapter = new MessageAdapter(this, models);
            recyclerView.setAdapter(adapter);//set adapter in recyler view
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    //start a thread to reload messages in every second
    private void reloadMessages()
    {
         thread = new Thread() {
             @Override
             public void run() {
                 try {
                     while (!thread.isInterrupted()) {
                         Thread.sleep(1000);//sleap for 1s
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 retrieveMessage();//reload data
                             }
                         });
                     }
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
        };thread.start();
    }

    //insert message server response
    private OnResponseTask responseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {
            if(!value.equals("success"))
                Toast.makeText(PersonMessage.this,"Message couldn't send.Please retry",Toast.LENGTH_LONG).show();
            else retrieveMessage();//again fetch data
        }
    };

    //insert message server response
    private OnResponseTask onResponseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {
            processJsonData(value);//processing json data
        }
    };

}
