package com.atik_faysal.diualumni.main;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.DatePicker;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;

import java.util.Calendar;

public class PostNewJob extends AppCompatActivity implements Methods,View.OnClickListener,DatePickerDialog.OnDateSetListener
{

     private EditText txtTitle,txtPhone,txtEmail,txtDes,txtEdu,txtReq,txtSalary,txtCompany;
     private Spinner sLocation,sCategory;
     private String division,jobType,category;
     private TextView txtDeadLine;

     private int day,month,year;

     private CheckInternetConnection internetConnection;
     private DisplayMessage displayMessage;
     private PostInfoBackgroundTask backgroundTask;


     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.layout_job);
          initComponent();
     }

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
          Button bDone = findViewById(R.id.bDone);
          txtDeadLine = findViewById(R.id.txtDeadLine);

          //set click listener
          bDone.setOnClickListener(this);
          txtLink.setOnClickListener(this);
          txtDeadLine.setOnClickListener(this);

          displayMessage = new DisplayMessage(this);
          internetConnection = new CheckInternetConnection(this);
          backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);
          Calendar calendar = Calendar.getInstance();

          day = calendar.get(Calendar.DAY_OF_MONTH);
          month = calendar.get(Calendar.MONTH);
          year = calendar.get(Calendar.YEAR);

          //call method
          setToolbar();
          spinnerCategory();
          spinnerLocation();
     }

     @Override
     public void setToolbar()
     {
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
     public void onClick(View view)
     {
          switch (view.getId())
          {
               case R.id.bDone:
                    break;
               case R.id.txtLink:
                    break;
               case R.id.txtDeadLine:
                    DatePickerDialog datePickerDialog = new DatePickerDialog(PostNewJob.this, PostNewJob.this, day, month, year);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                    break;
          }
     }

     @Override
     public void processJsonData(String jsonData)
     {

     }

     //set job location in spinner
     private void spinnerLocation()
     {
          ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this,R.array.division,R.layout.support_simple_spinner_dropdown_item);
          adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
          sLocation.setAdapter(adapter);
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
     private void spinnerCategory()
     {
          ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this,R.array.category,R.layout.support_simple_spinner_dropdown_item);
          adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
          sCategory.setAdapter(adapter);
          sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    category = parent.getItemAtPosition(i).toString();
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
          day = dd;month = mm+1;year=yy;
          String d,m;
          d = day+"";m = month+"";
          if(day<10)
               d="0"+day;
          if(month<10)
               m = "0"+month;

          txtDeadLine.setText(d+"/"+m+"/"+year);

     }


     //post new job result
     OnResponseTask onResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
               });
          }
     };

}
