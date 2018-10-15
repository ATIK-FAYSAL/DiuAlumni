package com.atik_faysal.diualumni.others;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressLint("Registered")
public class FilterResult extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

     private OnResponseTask onResponseTask;
     private Context context;
     private TextView txtDeadLine;
     private EditText txtStdId;
     private Spinner sCategory, sLocation,sDepartment,sBatch;
     private AlertDialog alertDialog;
     private String location,category,batch,department;
     protected int day, month, year;
     private String type;

     private CheckInternetConnection internetConnection;
     private PostInfoBackgroundTask backgroundTask;
     private SharedPreferencesData sharedPreferencesData;

     public FilterResult(Context context) {
          this.context = context;
          internetConnection = new CheckInternetConnection(context);
          sharedPreferencesData = new SharedPreferencesData(context);
     }

     public void onSuccessListener(OnResponseTask responseTask) {
          this.onResponseTask = responseTask;
     }


     //job filter by deadline,category,location,this method is call from JobPortal class
     @SuppressLint("InflateParams")
     public void filterJob() {
          AlertDialog.Builder builder = new AlertDialog.Builder(context);
          final View view = LayoutInflater.from(context).inflate(R.layout.dialog_filter_job, null);
          builder.setView(view);
          alertDialog = builder.create();
          alertDialog.show();
          Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

          txtDeadLine = view.findViewById(R.id.txtDeadLine);
          sCategory = view.findViewById(R.id.sCategory);
          sLocation = view.findViewById(R.id.sLocation);
          RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
          Button bSearch = view.findViewById(R.id.bSearch);

          txtDeadLine.setVisibility(View.GONE);//Invisible deadLine TextView
          sCategory.setVisibility(View.GONE);//Invisible category spinner
          sLocation.setVisibility(View.GONE);//Invisible location spinner

          final Map<String, String> map = new HashMap<>();

          radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(RadioGroup radioGroup, int id) {
                    jobSearchOption(id);
               }
          });

          bSearch.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    if(type==null)return;
                    String result;
                    switch (type) {
                         case "deadline":
                              result = txtDeadLine.getText().toString();
                              if(result.isEmpty())
                              {
                                   Toast.makeText(context,"Please choose a deadline",Toast.LENGTH_SHORT).show();
                                   return;
                              }
                              break;
                         case "category":
                              result = category;
                              if(result.isEmpty())
                              {
                                   Toast.makeText(context,"Please select a category",Toast.LENGTH_SHORT).show();
                                   return;
                              }
                              break;
                         default:
                              result = location;
                              if(result.isEmpty())
                              {
                                   Toast.makeText(context,"Please select a location",Toast.LENGTH_SHORT).show();
                                   return;
                              }
                              break;
                    }
                    map.put("option","searchJob");
                    map.put("stdId",sharedPreferencesData.getCurrentUserId());
                    map.put("type",type);
                    map.put("value",result);
                    if(internetConnection.isOnline())
                    {
                         backgroundTask = new PostInfoBackgroundTask(context,responseTask);
                         backgroundTask.insertData(context.getResources().getString(R.string.readInfo),map);
                    }

               }
          });

          txtDeadLine.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, FilterResult.this, day, month, year);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();//show dialog picker
               }
          });

          spinnerCategory();//select category from spinner
          spinnerLocation();//select location from spinner
     }

     private void jobSearchOption(int id)
     {
          switch (id) {
               case R.id.rDeadline:
                    txtDeadLine.setVisibility(View.VISIBLE);
                    sCategory.setVisibility(View.GONE);
                    sLocation.setVisibility(View.GONE);
                    type = "deadline";
                    break;

               case R.id.rCategory:
                    txtDeadLine.setVisibility(View.GONE);
                    sCategory.setVisibility(View.VISIBLE);
                    sLocation.setVisibility(View.GONE);
                    type = "category";
                    break;
               case R.id.rLocation:
                    txtDeadLine.setVisibility(View.GONE);
                    sCategory.setVisibility(View.GONE);
                    sLocation.setVisibility(View.VISIBLE);
                    type = "city";
                    break;
          }
     }

     private void alumniSearchOption(int id)
     {
          switch (id) {
               case R.id.rStudent:
                    txtStdId.setVisibility(View.VISIBLE);
                    sDepartment.setVisibility(View.GONE);
                    sBatch.setVisibility(View.GONE);
                    type = "std_id";
                    break;

               case R.id.rDepartment:
                    txtStdId.setVisibility(View.GONE);
                    sDepartment.setVisibility(View.VISIBLE);
                    sBatch.setVisibility(View.GONE);
                    type = "department";
                    break;
               case R.id.rBatch:
                    txtStdId.setVisibility(View.GONE);
                    sDepartment.setVisibility(View.GONE);
                    sBatch.setVisibility(View.VISIBLE);
                    type = "batch";
                    break;
          }
     }

     //filter alumni member by student id,department,batch ,this method call from AlumniMembers class
     public void filterAlumni()
     {
          AlertDialog.Builder builder = new AlertDialog.Builder(context);
          @SuppressLint("InflateParams")
          final View view = LayoutInflater.from(context).inflate(R.layout.dialog_filter_alumni, null);
          builder.setView(view);
          alertDialog = builder.create();
          alertDialog.show();
          Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          txtStdId = view.findViewById(R.id.txtStdId);
          sDepartment = view.findViewById(R.id.sDepartment);
          sBatch = view.findViewById(R.id.sBatch);

          RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
          Button bSearch = view.findViewById(R.id.bSearch);

          txtStdId.setVisibility(View.GONE);//Invisible deadLine TextView
          sDepartment.setVisibility(View.GONE);//Invisible category spinner
          sBatch.setVisibility(View.GONE);//Invisible location spinner

          final Map<String, String> map = new HashMap<>();

          radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(RadioGroup radioGroup, int id) {
                    alumniSearchOption(id);
               }
          });

          bSearch.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    if(type==null){
                         Toast.makeText(context,"Please select a search option",Toast.LENGTH_SHORT).show();
                         return;
                    }
                    String result;
                    switch (type) {
                         case "std_id":
                              result = txtStdId.getText().toString();
                              if(result.isEmpty())
                              {
                                   Toast.makeText(context,"Please enter a student id",Toast.LENGTH_SHORT).show();
                                   return;
                              }
                              break;
                         case "department":
                              result = department;
                              if(result.isEmpty())
                              {
                                   Toast.makeText(context,"Please select a department",Toast.LENGTH_SHORT).show();
                                   return;
                              }
                              break;
                         default:
                              result = batch;
                              if(result.isEmpty())
                              {
                                   Toast.makeText(context,"Please select a batch",Toast.LENGTH_SHORT).show();
                                   return;
                              }
                              break;
                    }
                    map.put("option","searchAlumni");
                    map.put("stdId",sharedPreferencesData.getCurrentUserId());
                    map.put("type",type);
                    map.put("value",result);
                    if(internetConnection.isOnline())
                    {
                         backgroundTask = new PostInfoBackgroundTask(context,responseTask);
                         backgroundTask.insertData(context.getResources().getString(R.string.readInfo),map);
                    }

               }
          });

          spinnerBatch();//select batch from spinner
          spinnerDepartment();//select department from spinner
     }

     //set job location in spinner
     protected void spinnerLocation() {
          ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.division, R.layout.support_simple_spinner_dropdown_item);
          adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
          sLocation.setAdapter(adapter);
          sLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    location = parent.getItemAtPosition(i).toString();
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {
               }
          });
     }

     //set job category in spinner
     protected void spinnerCategory() {
          ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.category, R.layout.support_simple_spinner_dropdown_item);
          adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
          sCategory.setAdapter(adapter);
          sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    String item = parent.getItemAtPosition(i).toString();
                    if (!item.equals("Select a category"))
                         category = parent.getItemAtPosition(i).toString();
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {
               }
          });
     }

     //set department in spinner
     private void spinnerDepartment()
     {
          ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.department,R.layout.support_simple_spinner_dropdown_item);
          adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
          sDepartment.setAdapter(adapter);
          sDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    department = parent.getItemAtPosition(i).toString();
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {}
          });
     }

     //set  batch in spinner
     private void spinnerBatch()
     {
          ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.batch,R.layout.support_simple_spinner_dropdown_item);
          adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
          sBatch.setAdapter(adapter);
          sBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    batch = parent.getItemAtPosition(i).toString();
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {}
          });
     }

     //select date from date picker
     @SuppressLint("SetTextI18n")
     @Override
     public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
          day = dd;
          month = mm + 1;
          year = yy;
          String d, m;
          d = day + "";
          m = month + "";
          if (day < 10)
               d = "0" + day;
          if (month < 10)
               m = "0" + month;

          txtDeadLine.setText(d + "/" + m + "/" + year);
     }

     private OnResponseTask responseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               onResponseTask.onResultSuccess(value);
               alertDialog.dismiss();
          }
     };
}