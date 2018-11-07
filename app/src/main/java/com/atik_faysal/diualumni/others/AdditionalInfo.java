package com.atik_faysal.diualumni.others;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;

import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.AboutProfile;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import android.content.Context;

@SuppressLint("ValidFragment")
public class AdditionalInfo extends AboutProfile implements DatePickerDialog.OnDateSetListener
{
    private Context context;
    private Activity activity;
    private OnResponseTask onResponseTask;
    private Spinner spinner;
    private int day,month,year;
    private boolean flag;
    private TextView txtForm,txtTo;
    private String city;
    private RequireMethods requireMethods;

    @SuppressLint("ValidFragment")
    public AdditionalInfo(Context context)
    {
        this.context = context;
        activity = (Activity)context;
        sharedPreferencesData = new SharedPreferencesData(context);
        internetConnection = new CheckInternetConnection(context);
        requireMethods = new RequireMethods(context);
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

    }

    public void onResultSuccess(OnResponseTask responseTask)
    {
        this.onResponseTask = responseTask;
    }

    //user url update and insert new url
    @SuppressLint("SetTextI18n")
    public void userUrls()
    {
        Button bAdd;
        final EditText txtUrl;ImageView imgClear;
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_url,null);
        bAdd = view.findViewById(R.id.bAdd);
        txtUrl = view.findViewById(R.id.txtUrl);
        txtUrl.setText("http://");
        Selection.setSelection(txtUrl.getText(),txtUrl.getText().length());
        imgClear = view.findViewById(R.id.imgClear);
        builder = new AlertDialog.Builder(context);
        super.builder.setView(view);
        super.builder.setCancelable(false);
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        txtUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().startsWith("http://"))//set default text http
                {
                    txtUrl.setText("http://");
                    Selection.setSelection(txtUrl.getText(),txtUrl.getText().length());
                }
            }
        });

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = txtUrl.getText().toString();
                if(TextUtils.isEmpty(url)||url.length()<15)
                    Toast.makeText(context,"Invalid Url",Toast.LENGTH_LONG).show();
                else
                {
                    backgroundTask = new PostInfoBackgroundTask(context,responseTask);
                    postNewUrl(url);
                }
                alertDialog.dismiss();
            }
        });
    }

    //insert new url
    private void postNewUrl(String url)
    {
        Map<String,String> maps = new HashMap<>();
        maps.put("option","putUrl");
        maps.put("stdId",sharedPreferencesData.getCurrentUserId());
        maps.put("url",url);
        if(internetConnection.isOnline())
            backgroundTask.insertData(Objects.requireNonNull(activity).getResources().getString(R.string.otherInsertion),maps);
        else Toast.makeText(context, Objects.requireNonNull(activity).getResources().getString(R.string.noInternet),Toast.LENGTH_LONG).show();
    }

    //work experience
    public void workExperience()
    {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.work_experience,null);
        builder = new AlertDialog.Builder(context);
        super.builder.setView(view);
        super.builder.setCancelable(false);
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        final EditText txtCompany,txtPos,txtDes;
        final CheckBox checkBox;Button bSave;ImageView imgCancel;
        txtForm = view.findViewById(R.id.txtForm);
        txtTo = view.findViewById(R.id.txtTo);
        txtCompany = view.findViewById(R.id.txtCompany);
        txtPos = view.findViewById(R.id.txtPos);
        txtDes = view.findViewById(R.id.txtDes);
        checkBox = view.findViewById(R.id.checkBox);
        spinner = view.findViewById(R.id.sCity);
        bSave = view.findViewById(R.id.bSave);
        imgCancel = view.findViewById(R.id.imgCancel);

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundTask = new PostInfoBackgroundTask(context,getOnResponseTask);
                Map<String,String>maps = new HashMap<>();
                String company,position,from,to,description;
                company = txtCompany.getText().toString();
                position = txtPos.getText().toString();
                from = txtForm.getText().toString();
                to = txtTo.getText().toString();
                description = txtDes.getText().toString();
                if(!company.isEmpty()&&!position.isEmpty())
                {
                    maps.put("option","experience");
                    maps.put("stdId",sharedPreferencesData.getCurrentUserId());
                    maps.put("company",company);
                    maps.put("position",position);
                    maps.put("city",city);
                    if(!description.isEmpty())
                        maps.put("description",description);
                    else maps.put("description","none");
                    if(checkBox.isChecked())
                    {
                        maps.put("from","present");
                        maps.put("to","present");
                    }else
                    {
                        if(!from.isEmpty()&&!to.isEmpty()&&(requireMethods.calculateDate(from)<requireMethods.calculateDate(to)))
                        {
                            maps.put("from",from);
                            maps.put("to",to);
                        }else {
                            Toast.makeText(context,"Please insert valid dates.",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    if(internetConnection.isOnline())
                        backgroundTask.insertData(context.getResources().getString(R.string.otherInsertion),maps);
                    else
                        Toast.makeText(context,getResources().getString(R.string.noInternet),Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(context,"Please input valid data",Toast.LENGTH_LONG).show();

            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked())
                {
                    txtForm.setEnabled(false);
                    txtTo.setEnabled(false);
                    txtForm.setVisibility(View.GONE);
                    txtTo.setVisibility(View.GONE);
                    txtForm.setText("");txtTo.setText("");
                }else
                {
                    txtForm.setEnabled(true);
                    txtTo.setEnabled(true);
                    txtForm.setVisibility(View.VISIBLE);
                    txtTo.setVisibility(View.VISIBLE);
                }
            }
        });

        txtForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, AdditionalInfo.this, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                flag = true;
            }
        });

        txtTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, AdditionalInfo.this, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                flag = false;
            }
        });

        spinnerLocation();
    }

    //set job location in spinner
    private void spinnerLocation()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.division,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                city = parent.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    //select date from date picker
    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker datePicker, int yy, int mm, int dd)
    {
        int month1 = mm+1;
        String d,m;
        d = dd +"";m = month1+"";
        if(dd <10)
            d="0"+ dd;
        if(month1<10)
            m = "0"+month1;

        if(flag)
            txtForm.setText(d+"-"+m+"-"+ yy);
        else
            txtTo.setText(d+"-"+m+"-"+ yy);
    }

    //update url server response
    private OnResponseTask responseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {
            switch (value) {
                case "success":
                    onResponseTask.onResultSuccess(value);
                    Toast.makeText(context, "This url is inserted successfully", Toast.LENGTH_LONG).show();
                    break;
                case "overflow":
                    Toast.makeText(context, "Your limit is over,please remove one url and try again", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(context, "Sorry insertion failed.please try again", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private OnResponseTask getOnResponseTask = new OnResponseTask() {
        @Override
        public void onResultSuccess(String value) {
            switch (value) {
                case "success":
                    alertDialog.dismiss();
                    onResponseTask.onResultSuccess(value);
                    Toast.makeText(context, "Your work experience is added successfully.", Toast.LENGTH_LONG).show();
                    break;
                case "overflow":
                    Toast.makeText(context, "Your work experience limit is over,please remove one and retry", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                    break;
                default:
                    Toast.makeText(context, "Failed to added experience,please try again.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

}
