package com.atik_faysal.diualumni.main;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostNewJob extends AppCompatActivity implements Methods,View.OnClickListener,DatePickerDialog.OnDateSetListener
{

     private EditText txtTitle,txtPhone,txtEmail,txtDes,txtEdu,txtReq,txtSalary,txtCompany;
     private Spinner sLocation,sCategory;
     private TextView txtDeadLine;
     private ProgressBar progressBar;

     private Drawable iconValid;
     private Drawable iconInvalid;

     private int day,month,year;
     private String title,description,education,requirement,division,jobType,category,deadLine,salary,company,email,phone;
     private static final String INVALID_MSG = "Invalid input";
     private static final String VALID_MSG = "Valid";

     private CheckInternetConnection internetConnection;
     private DisplayMessage displayMessage;
     private PostInfoBackgroundTask backgroundTask;
     private SharedPreferencesData sharedPreferencesData;
     private RequireMethods methods;


     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.layout_job);
          initComponent();
     }

     //initialize all component
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
          progressBar = findViewById(R.id.progress);

          //set click listener
          bDone.setOnClickListener(this);
          txtLink.setOnClickListener(this);
          txtDeadLine.setOnClickListener(this);

          displayMessage = new DisplayMessage(this);
          internetConnection = new CheckInternetConnection(this);
          backgroundTask = new PostInfoBackgroundTask(this,onResponseTask);
          sharedPreferencesData = new SharedPreferencesData(this);
          methods = new RequireMethods(this);
          Calendar calendar = Calendar.getInstance();

          day = calendar.get(Calendar.DAY_OF_MONTH);
          month = calendar.get(Calendar.MONTH);
          year = calendar.get(Calendar.YEAR);

          //call method
          setToolbar();
          textChangeListener();
          spinnerCategory();
          spinnerLocation();
     }

     //set toolbar in layout
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

     //button click
     @Override
     public void onClick(View view)
     {
          switch (view.getId())
          {
               case R.id.bDone:
                    if(dataValidator())
                    {
                         Map<String,String> infoMap = new HashMap<>();
                         infoMap.put("stdId",sharedPreferencesData.getStudentId());
                         infoMap.put("email",email);
                         infoMap.put("phone",phone);
                         infoMap.put("type",jobType);
                         infoMap.put("title",title);
                         infoMap.put("des",description);
                         infoMap.put("edu",education);
                         infoMap.put("req",requirement);
                         infoMap.put("category",category);
                         infoMap.put("loc",division);
                         infoMap.put("company",company);
                         infoMap.put("deadLine",deadLine);
                         infoMap.put("date",methods.getDate());
                         infoMap.put("salary",salary);

                         if(internetConnection.isOnline())
                              backgroundTask.InsertData(getString(R.string.postJob),infoMap);
                         else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
                    }else displayMessage.errorMessage(getString(R.string.infoErr));

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


     //if input is not valid ,it's shows an error
     private void textChangeListener()
     {
          iconValid = getResources().getDrawable(R.drawable.icon_check);//valid icon
          iconValid.setBounds(0,0,iconValid.getIntrinsicWidth(),iconValid.getIntrinsicHeight());
          iconInvalid = getResources().getDrawable(R.drawable.icon_wrong);//invalid icon
          iconInvalid.setBounds(0,0,iconInvalid.getIntrinsicWidth(),iconInvalid.getIntrinsicHeight());

          txtTitle.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String title = txtTitle.getText().toString();
                    if(title.length()<15)
                         txtTitle.setError(INVALID_MSG,iconInvalid);
                    else txtTitle.setError(VALID_MSG,iconValid);
               }
          });//check title,must be more than 15 characters

          txtDes.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtDes.getText().toString();
                    if(text.length()<25)
                         txtDes.setError(INVALID_MSG,iconInvalid);
                    else txtDes.setError(VALID_MSG,iconValid);
               }
          });//check description,must be more than 25 characters

          txtEdu.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtEdu.getText().toString();
                    if(text.length()<25)
                         txtEdu.setError(INVALID_MSG,iconInvalid);
                    else txtEdu.setError(VALID_MSG,iconValid);
               }
          });//check education,must be more than 25 characters

          txtReq.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtReq.getText().toString();
                    if(text.length()<25)
                         txtReq.setError(INVALID_MSG,iconInvalid);
                    else txtReq.setError(VALID_MSG,iconValid);
               }
          });//check requirement,must be more than 25 characters

          txtEmail.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtEmail.getText().toString();
                    if(text.length()<13||(!text.contains(".")||!text.contains("@")))
                         txtEmail.setError(INVALID_MSG,iconInvalid);
                    else txtEmail.setError(VALID_MSG,iconValid);
               }
          });//check email,must be more than 13 characters and must be contain @ an "."

          txtPhone.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtPhone.getText().toString();

                    if(text.length()>6)
                    {
                         if(text.substring(0,3).equals("+88"))
                         {
                              if(text.length()!=14)
                                   txtPhone.setError(INVALID_MSG,iconInvalid);
                              else
                              {
                                   switch (text.substring(3,6))
                                   {
                                        case "019":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        case "018":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        case "017":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        case "016":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        case "015":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        default:
                                             txtPhone.setError(INVALID_MSG,iconInvalid);
                                   }
                              }
                         }else if(!text.substring(0,3).equals("+88"))
                         {
                              if(text.length()!=11)
                                   txtPhone.setError(INVALID_MSG,iconInvalid);
                              else
                              {
                                   switch (text.substring(0,3))
                                   {
                                        case "019":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        case "018":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        case "017":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        case "016":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        case "015":
                                             txtPhone.setError(VALID_MSG,iconValid);
                                             break;
                                        default:
                                             txtPhone.setError(INVALID_MSG,iconInvalid);
                                   }
                              }
                         }else txtPhone.setError(VALID_MSG,iconValid);
                    }
               }
          });//check phone,must be in 11 or 14 characters,

          txtSalary.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtSalary.getText().toString();
                    if(text.length()<4)
                         txtSalary.setError(INVALID_MSG,iconInvalid);
                    else txtSalary.setError(VALID_MSG,iconValid);
               }
          });//check sallary

          txtCompany.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtCompany.getText().toString();
                    if(text.length()<5)
                         txtCompany.setError(INVALID_MSG,iconInvalid);
                    else
                         txtCompany.setError(VALID_MSG,iconValid);
               }
          });//check text company
     }

     //job info validator
     private boolean dataValidator()
     {
          title = txtTitle.getText().toString();
          description = txtDes.getText().toString();
          education = txtEdu.getText().toString();
          requirement = txtReq.getText().toString();
          deadLine = txtDeadLine.getText().toString();
          email = txtEmail.getText().toString();
          phone = txtPhone.getText().toString();
          salary = txtSalary.getText().toString();
          company = txtCompany.getText().toString();

          if(TextUtils.isEmpty(title)||(title.length()<15))
          {
               txtTitle.setError(INVALID_MSG,iconInvalid);
               txtTitle.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(description)||(description.length()<25))
          {
               txtDes.setError(INVALID_MSG,iconInvalid);
               txtDes.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(education)||(education.length()<25))
          {
               txtEdu.setError(INVALID_MSG,iconInvalid);
               txtEdu.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(requirement)||(requirement.length()<25))
          {
               txtReq.setError(INVALID_MSG,iconInvalid);
               txtReq.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(jobType))
          {
               Toast.makeText(PostNewJob.this,"Please choose job type",Toast.LENGTH_LONG).show();
               return false;
          }
          if(TextUtils.isEmpty(category))
          {
               Toast.makeText(PostNewJob.this,"Please select job category",Toast.LENGTH_LONG).show();
               return false;
          }
          if(TextUtils.isEmpty(deadLine))
          {
               txtDeadLine.setError("Choose a date",iconInvalid);
               return false;
          }
          if(TextUtils.isEmpty(salary)||salary.length()<4)
          {
               txtSalary.setError(INVALID_MSG,iconInvalid);
               txtSalary.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(company)||company.length()<5)
          {
               txtCompany.setError(INVALID_MSG,iconInvalid);
               txtCompany.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(email))
          {
               txtEmail.setError(INVALID_MSG,iconInvalid);
               txtEmail.setFocusable(true);
               return false;
          }
          if(email.length()<13||(!email.contains(".")||!email.contains("@")))
          {
               txtEmail.setError(INVALID_MSG,iconInvalid);
               txtEmail.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(phone))
          {
               txtPhone.setError(INVALID_MSG,iconInvalid);
               txtPhone.setFocusable(true);
               return false;
          }
          if(phone.length()>6) {
               if (phone.substring(0, 3).equals("+88")) {
                    if (phone.length() != 14)
                    {
                         txtPhone.setError(INVALID_MSG, iconInvalid);
                         txtPhone.setFocusable(true);
                         return false;
                    }
                    else {
                         switch (phone.substring(3,6)) {
                              case "019":
                                   break;
                              case "018":
                                   break;
                              case "017":
                                   break;
                              case "016":
                                   break;
                              case "015":
                                   break;
                              default:
                              {
                                   txtPhone.setError(INVALID_MSG, iconInvalid);
                                   txtPhone.setFocusable(true);
                                   return false;
                              }
                         }
                    }
               } else if (!phone.substring(0,3).equals("+88")) {
                    if (phone.length() != 11)
                    {
                         txtPhone.setError(INVALID_MSG, iconInvalid);
                         txtPhone.setFocusable(true);
                         return false;
                    }
                    else {
                         switch (phone.substring(0,3)) {
                              case "019":
                                   break;
                              case "018":
                                   break;
                              case "017":
                                   break;
                              case "016":
                                   break;
                              case "015":
                                   break;
                              default:
                              {
                                   txtPhone.setError(INVALID_MSG, iconInvalid);
                                   txtPhone.setFocusable(true);
                                   return false;
                              }
                         }
                    }
               } else
               {
                    txtPhone.setError(VALID_MSG, iconValid);
                    txtPhone.setFocusable(true);
                    return false;
               }
          }else return false;
          return true;
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

               case R.id.rContract:
                    if(checked)jobType="contract";
                    break;
          }
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
          txtDeadLine.setError("Valid",iconValid);
     }

     //post new job result
     OnResponseTask onResponseTask = new OnResponseTask() {
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
                                             Thread.sleep(2500);
                                             methods.closeActivity(PostNewJob.this,JobPortal.class);
                                        }catch (InterruptedException e)
                                        {
                                             e.printStackTrace();
                                        }
                                   }
                              });
                              thread.start();
                         }
                         else displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
                    }
               });
          }
     };

}
