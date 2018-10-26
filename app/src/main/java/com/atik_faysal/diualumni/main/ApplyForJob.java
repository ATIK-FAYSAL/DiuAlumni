package com.atik_faysal.diualumni.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.others.SetTabLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplyForJob extends AppCompatActivity implements Methods,View.OnClickListener
{

    protected TextView txtTitle,txtPhone,txtEmail,txtDes,txtEdu,txtReq,txtSalary,txtCompany,txtExp,
            txtComUrl,txtComAddress,txtLocation,txtCategory,txtDeadLine,txtVacancy,txtDate,txtType,txtJobId,txtUsername;

    private SharedPreferencesData sharedPreferencesData;
    private DisplayMessage displayMessage;
    private CheckInternetConnection internetConnection;
    private ProgressDialog progressDialog;
    private String toEmail;
    private RequireMethods methods;
    private Map<String,String>info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_job);
        initComponent();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initComponent() {

        txtTitle = findViewById(R.id.txtTitle);
        txtCompany = findViewById(R.id.txtCompany);
        txtDate = findViewById(R.id.txtDate);
        txtDes = findViewById(R.id.txtDescription);
        txtExp = findViewById(R.id.txtExp);
        txtReq = findViewById(R.id.txtReq);
        txtEdu = findViewById(R.id.txtEdu);
        txtCategory = findViewById(R.id.txtCategory);
        txtDeadLine = findViewById(R.id.txtDeadLine);
        txtVacancy = findViewById(R.id.txtVacancy);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtSalary = findViewById(R.id.txtSalary);
        txtComAddress = findViewById(R.id.txtComAddress);
        txtType = findViewById(R.id.txtType);
        txtComUrl = findViewById(R.id.txtComUrl);
        txtLocation = findViewById(R.id.txtLocation);
        txtJobId = findViewById(R.id.txtJobId);
        txtUsername = findViewById(R.id.txtUserName);
        Button bApplyJob = findViewById(R.id.bApply);

        info = (Map<String, String>) getIntent().getExtras().getSerializable("maps");
        assert info != null;
        txtJobId.setText(info.get("id"));
        txtEmail.setText(info.get("email"));
        txtPhone.setText(info.get("phone"));
        txtType.setText(info.get("type"));
        txtTitle.setText(info.get("title"));
        txtDes.setText(info.get("des"));
        txtEdu.setText(info.get("edu"));
        txtReq.setText(info.get("req"));
        txtExp.setText(info.get("expe"));
        txtCategory.setText(info.get("category"));
        txtCompany.setText(info.get("company"));
        txtDeadLine.setText(info.get("deadLine"));
        txtSalary.setText(info.get("salary"));
        txtLocation.setText(info.get("city"));
        txtVacancy.setText(info.get("vacancy"));
        txtComUrl.setText(info.get("comUrl"));
        txtComAddress.setText(info.get("comAddress"));
        txtDate.setText(info.get("date"));
        txtUsername.setText("Posted by "+info.get("name"));
        toEmail = info.get("email");
        String appliedJob = info.get("appliedJob");

        bApplyJob.setOnClickListener(this);
        txtUsername.setOnClickListener(this);
        txtComUrl.setOnClickListener(this);

        assert appliedJob != null;
        if(appliedJob.equals("yes"))
        {
            bApplyJob.setEnabled(false);
            bApplyJob.setBackgroundDrawable(getResources().getDrawable(R.drawable.disable_button));
            bApplyJob.setText("Already applied");
        }

        sharedPreferencesData = new SharedPreferencesData(this);
        internetConnection = new CheckInternetConnection(this);
        displayMessage = new DisplayMessage(this);
        progressDialog = new ProgressDialog(this);
        methods = new RequireMethods(this);

        //calling method
        setToolbar();
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
    public void processJsonData(String jsonData) {}

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.bApply:
                if(sharedPreferencesData.getIsUserLogin())
                {
                    Map<String,String> map = new HashMap<>();
                    map.put("name",sharedPreferencesData.getUserName());
                    map.put("stdId",sharedPreferencesData.getCurrentUserId());
                    map.put("phone",sharedPreferencesData.getUserPhone());
                    map.put("email",sharedPreferencesData.getUserEmail());
                    map.put("toEmail",toEmail);
                    map.put("jobId",txtJobId.getText().toString());
                    map.put("date",methods.getDate());
                    if(internetConnection.isOnline())
                    {
                        PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(this,responseTask);
                        backgroundTask.insertData(getResources().getString(R.string.sendResume),map);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("Please wait");
                        progressDialog.setMessage("Sending your resume");
                        progressDialog.show();
                    }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
                }else Toast.makeText(this,"Please sign in first and retry",Toast.LENGTH_LONG).show();
                break;
            case R.id.txtComUrl:
                Uri uri = Uri.parse(info.get("comUrl"));
                Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                break;
            case R.id.txtUserName:
                Intent profile = new Intent(ApplyForJob.this, SetTabLayout.class);
                profile.putExtra("user",info.get("stdId"));
                startActivity(profile);
                break;
        }
    }


    private OnResponseTask responseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {

            switch (value) {
                case "null value":
                    progressDialog.dismiss();
                    displayMessage.errorMessage("Sorry! You have no resume.Please first upload a resume and retry.");
                    break;
                case "success":
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(getResources().getInteger(R.integer.progTime));
                            } catch (Exception e) {
                                displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
                            }
                            progressDialog.dismiss();
                        }
                    }).start();
                    progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            displayMessage.congratesMessage("Your Resume has been send successfully");
                        }
                    });
                    break;
                default:
                    progressDialog.dismiss();
                    displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
                    break;
            }
        }
    };
}
