package com.atik_faysal.diualumni.others;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.JobPortal;
import com.atik_faysal.diualumni.main.PostNewJob;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditJobPost extends PostNewJob
{
    private Map<String,String>infoMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //initialize all component
    @SuppressLint("SetTextI18n")
    @Override
    public void initComponent()
    {
        txtTitle = findViewById(R.id.txtTitle);
        txtDes = findViewById(R.id.txtDes);
        txtEdu = findViewById(R.id.txtEdu);
        txtReq = findViewById(R.id.txtReq);
        txtSalary = findViewById(R.id.txtSalary);
        txtPhone = findViewById(R.id.txtPhone);
        txtCompany = findViewById(R.id.txtSource);
        txtEmail = findViewById(R.id.txtEmail);
        sCategory = findViewById(R.id.sCategory);
        sLocation = findViewById(R.id.sLocation);
        TextView txtLink = findViewById(R.id.txtLink);
        bDone = findViewById(R.id.bDone);
        txtDeadLine = findViewById(R.id.txtDeadLine);
        progressBar = findViewById(R.id.progress);
        txtExp = findViewById(R.id.txtExp);
        txtComUrl = findViewById(R.id.txtComUrl);
        txtComAddress = findViewById(R.id.txtComAddress);
        txtVacancy = findViewById(R.id.txtVacancy);
        imgRmv = findViewById(R.id.imgRmv);
        imgAdd = findViewById(R.id.imgAdd);
        RadioButton rContact = findViewById(R.id.rContract);
        RadioButton rOthers = findViewById(R.id.rOther);
        RadioButton rFullTime = findViewById(R.id.rFullTime);
        RadioButton rPartTime = findViewById(R.id.rPartTime);
        RadioButton rIntern = findViewById(R.id.rIntern);

        infoMap = new HashMap<>();
        infoMap = (Map<String, String>) getIntent().getSerializableExtra("maps");
        txtTitle.setText(infoMap.get("title"));
        txtDes.setText(infoMap.get("des"));
        txtEdu.setText(infoMap.get("edu"));
        txtReq.setText(infoMap.get("req"));
        txtExp.setText(infoMap.get("expe"));
        txtDeadLine.setText(infoMap.get("deadLine"));
        txtSalary.setText(infoMap.get("salary"));
        txtCompany.setText(infoMap.get("company"));
        txtPhone.setText(infoMap.get("phone"));
        txtEmail.setText(infoMap.get("email"));
        txtVacancy.setText(infoMap.get("vacancy"));
        txtComAddress.setText(infoMap.get("comAddress"));
        txtComUrl.setText(infoMap.get("comUrl"));
        switch (Objects.requireNonNull(infoMap.get("type"))) {
            case "full time":
                rFullTime.setChecked(true);
                rPartTime.setChecked(false);
                rOthers.setChecked(false);
                rContact.setChecked(false);
                rIntern.setChecked(false);
                jobType="full time";
                break;
            case "part time":
                rFullTime.setChecked(false);
                rPartTime.setChecked(true);
                rOthers.setChecked(false);
                rContact.setChecked(false);
                rIntern.setChecked(false);
                jobType="part time";
                break;
            case "other":
                rFullTime.setChecked(false);
                rPartTime.setChecked(false);
                rOthers.setChecked(true);
                rContact.setChecked(false);
                rIntern.setChecked(false);
                jobType="other";
                break;
            case "contract":
                rFullTime.setChecked(false);
                rPartTime.setChecked(false);
                rOthers.setChecked(false);
                rContact.setChecked(true);
                rIntern.setChecked(false);
                jobType="contract";
                break;
            case "internship":
                rFullTime.setChecked(false);
                rPartTime.setChecked(false);
                rOthers.setChecked(false);
                rContact.setChecked(true);
                rIntern.setChecked(true);
                jobType="contract";
                break;
        }

        //set click listener
        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                bDone.setEnabled(false);
                bDone.setBackgroundDrawable(getResources().getDrawable(R.drawable.disable_button));
                updatePost();
            }
        });
        imgAdd.setOnClickListener(this);
        imgRmv.setOnClickListener(this);
        txtLink.setOnClickListener(this);
        txtDeadLine.setOnClickListener(this);
        bDone.setText("Save changes");//change text for update

        displayMessage = new DisplayMessage(this);
        internetConnection = new CheckInternetConnection(this);
        backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);
        sharedPreferencesData = new SharedPreferencesData(this);
        methods = new RequireMethods(this);
        Calendar calendar = Calendar.getInstance();

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        super.setToolbar();
        super.textChangeListener();

        spinnerCategory();
        spinnerLocation();
    }

    //update job post
    private void updatePost()
    {
        if(super.dataValidator())
        {
            Map<String,String> maps = new HashMap<>();
            maps.put("option","update");
            maps.put("id",infoMap.get("id"));
            maps.put("email",email);
            maps.put("phone",phone);
            maps.put("type",jobType);
            maps.put("title",title);
            maps.put("des",description);
            maps.put("edu",education);
            maps.put("req",requirement);
            maps.put("expe",experience);
            maps.put("category",category);
            maps.put("loc",division);
            maps.put("company",company);
            maps.put("deadLine",deadLine);
            maps.put("salary",salary);
            maps.put("comUrl",comUrl);
            maps.put("comAddress",comAddress);
            maps.put("vacancy",vacancy);

            if(internetConnection.isOnline())
                backgroundTask.insertData(getString(R.string.postJob),maps);
            else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
        }else displayMessage.errorMessage(getString(R.string.infoErr));
    }

    //set job location in spinner
    protected void spinnerLocation()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.division,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sLocation.setAdapter(adapter);
        int pos = adapter.getPosition(infoMap.get("city"));
        sLocation.setSelection(pos);
        sLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                division = parent.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    //set job category in spinner
    protected void spinnerCategory()
    {
        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this,R.array.category,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sCategory.setAdapter(adapter);
        int pos = adapter.getPosition(infoMap.get("category"));
        sCategory.setSelection(pos);
        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                String item = parent.getItemAtPosition(i).toString();
                if(!item.equals("Select a category"))
                    category = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    //define member type
    public void chooseJobNature(View view)
    {
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId())
        {
            case R.id.rFullTime:
                if(checked)jobType="full time";
                break;

            case R.id.rPartTime:
                if(checked)jobType="part time";
                break;

            case R.id.rOther:
                if(checked)jobType="other";
                break;

            case R.id.rIntern:
                if(checked)jobType="internship";
                break;

            case R.id.rContract:
                if(checked)jobType="contract";
                break;
        }
    }

    private OnResponseTask onResponseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(final String value) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("error",value);
                    if(value.equals("success"))
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try
                                {
                                    Thread.sleep(getResources().getInteger(R.integer.progTime));
                                    methods.closeActivity(EditJobPost.this,JobPortal.class);
                                }catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }
                    else {
                        bDone.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_done));
                        bDone.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
                    }
                }
            });
        }
    };
}
