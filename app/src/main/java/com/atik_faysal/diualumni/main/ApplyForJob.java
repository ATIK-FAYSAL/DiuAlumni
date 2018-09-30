package com.atik_faysal.diualumni.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;

public class ApplyForJob extends AppCompatActivity implements Methods,View.OnClickListener
{

     protected TextView txtTitle,txtPhone,txtEmail,txtDes,txtEdu,txtReq,txtSalary,txtCompany,txtExp,
          txtComUrl,txtComAddress,txtLocation,txtCategory,txtDeadLine,txtVacancy,txtDate,txtType,txtJobId,txtUsername;

     private SharedPreferencesData sharedPreferencesData;
     private DisplayMessage displayMessage;
     private CheckInternetConnection internetConnection;
     private ProgressDialog progressDialog;
     private RequireMethods requireMethods;
     private String toEmail;

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

          Map<String,String> info = (Map<String, String>) getIntent().getExtras().getSerializable("maps");
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

          bApplyJob.setOnClickListener(this);
          txtUsername.setOnClickListener(this);
          txtComUrl.setOnClickListener(this);

          sharedPreferencesData = new SharedPreferencesData(this);
          internetConnection = new CheckInternetConnection(this);
          displayMessage = new DisplayMessage(this);
          progressDialog = new ProgressDialog(this);
          requireMethods = new RequireMethods(this);

          //calling method
          setToolbar();
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
     public void processJsonData(String jsonData) {}

     @Override
     public void onClick(View view) {
          switch (view.getId())
          {
               case R.id.bApply:
                    Map<String,String> map = new HashMap<>();
                    map.put("name",sharedPreferencesData.getUserName());
                    map.put("stdId",sharedPreferencesData.getCurrentUserId());
                    map.put("phone",sharedPreferencesData.getUserPhone());
                    map.put("email",sharedPreferencesData.getUserEmail());
                    map.put("toEmail",toEmail);
                    if(internetConnection.isOnline())
                    {
                         PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(this,responseTask);
                         backgroundTask.InsertData(getResources().getString(R.string.sendResume),map);
                         progressDialog.setCancelable(false);
                         progressDialog.setTitle("Please wait");
                         progressDialog.setMessage("Sending your resume");
                         progressDialog.show();
                    }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
                    break;
               case R.id.txtComUrl:
                    break;
               case R.id.txtUserName:
                    break;
          }
     }


     private OnResponseTask responseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               if(value!=null)
               {
                    if(progressDialog.isShowing())
                    {
                         progressDialog.dismiss();
                         //Toast.makeText(ApplyForJob.this,"Your Resume is send successfully",Toast.LENGTH_SHORT).show();
                         displayMessage.congratesMessage("Your Resume is send successfully");
                    }
               }
          }
     };
}
