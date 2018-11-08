package com.atik_faysal.diualumni.main;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import com.atik_faysal.diualumni.others.NoInternetConnection;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostNewJob extends AppCompatActivity implements Methods,View.OnClickListener,DatePickerDialog.OnDateSetListener
{

     protected EditText txtTitle,txtPhone,txtEmail,txtDes,txtEdu,txtReq,txtSalary,txtCompany,txtExp,txtComUrl,txtComAddress;
     protected Spinner sLocation,sCategory;
     protected TextView txtDeadLine,txtVacancy;
     protected ProgressBar progressBar;
     protected ImageView imgAdd,imgRmv;

     protected Drawable iconValid;
     protected Drawable iconInvalid;

     protected int day,month,year;
     protected String title,description,education,requirement,division,jobType,
          category,deadLine,salary,company,email,phone,experience,comUrl,comAddress,vacancy;
     protected static final String INVALID_MSG = "Invalid input";
     protected static final String VALID_MSG = "Valid";
     protected int numOfVacancy;

     protected CheckInternetConnection internetConnection;
     protected DisplayMessage displayMessage;
     protected PostInfoBackgroundTask backgroundTask;
     protected SharedPreferencesData sharedPreferencesData;
     protected RequireMethods methods;
     protected Button bDone;


     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.layout_job);
          initComponent();
     }

     @Override
     protected void onStart() {
          super.onStart();
          if(!internetConnection.isOnline())//if internet is not connect go to no internet page ,
          {
               String className = PostNewJob.class.getName();
               Intent intent = new Intent(PostNewJob.this,NoInternetConnection.class);
               intent.putExtra("class",className);//send current class name to NoInternetConnection class
               startActivity(intent);
               finish();
          }
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
          imgAdd = findViewById(R.id.imgAdd);
          imgRmv = findViewById(R.id.imgRmv);
          txtComAddress = findViewById(R.id.txtComAddress);
          txtComUrl = findViewById(R.id.txtComUrl);
          txtComUrl.setText("http://");
          Selection.setSelection(txtComUrl.getText(),txtComUrl.getText().length());
          txtVacancy = findViewById(R.id.txtVacancy);
          imgRmv.setVisibility(View.GONE);

          //set click listener
          bDone.setOnClickListener(this);
          txtLink.setOnClickListener(this);
          imgAdd.setOnClickListener(this);
          imgRmv.setOnClickListener(this);
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

     //button click
     @Override
     public void onClick(View view)
     {
          switch (view.getId())
          {
               case R.id.bDone:
                    progressBar.setVisibility(View.VISIBLE);
                    bDone.setEnabled(false);
                    bDone.setBackgroundDrawable(getResources().getDrawable(R.drawable.disable_button));
                    postNewJob();
                    break;
               case R.id.txtLink:
                    break;
               case R.id.txtDeadLine:
                    DatePickerDialog datePickerDialog = new DatePickerDialog(PostNewJob.this, PostNewJob.this, day, month, year);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                    break;
               case R.id.imgAdd:

                    if(!txtVacancy.getText().toString().equals(""))
                    {
                         numOfVacancy = Integer.parseInt(txtVacancy.getText().toString());
                         numOfVacancy++;
                         txtVacancy.setText(String.valueOf(numOfVacancy));
                         imgAdd.setVisibility(View.VISIBLE);
                         imgRmv.setVisibility(View.VISIBLE);
                         imgRmv.setEnabled(true);
                         if(txtVacancy.getText().toString().equals("10"))
                         {
                              imgAdd.setVisibility(View.INVISIBLE);
                              imgAdd.setEnabled(false);
                         }
                    }
                    else{
                         txtVacancy.setText("1");
                         imgRmv.setVisibility(View.VISIBLE);
                         imgRmv.setEnabled(true);
                    }
                    break;
               case R.id.imgRmv:
                    if(txtVacancy.getText().toString().equals(""))
                         txtVacancy.setText("0");
                    else
                    {
                         numOfVacancy = Integer.parseInt(txtVacancy.getText().toString());
                         numOfVacancy--;
                         txtVacancy.setText(String.valueOf(numOfVacancy));
                         imgRmv.setVisibility(View.VISIBLE);
                         imgAdd.setVisibility(View.VISIBLE);
                         imgAdd.setEnabled(true);
                         if(txtVacancy.getText().toString().equals("0"))
                         {
                              imgRmv.setVisibility(View.INVISIBLE);
                              imgRmv.setEnabled(false);
                         }
                    }
                    break;
          }
     }

     //if all information are correct than insert into table
     private void postNewJob()
     {
          if(dataValidator())
          {
               Map<String,String> infoMap = new HashMap<>();
               infoMap.put("option","insert");
               infoMap.put("stdId",sharedPreferencesData.getCurrentUserId());
               infoMap.put("email",email);
               infoMap.put("phone",phone);
               infoMap.put("type",jobType);
               infoMap.put("title",title);
               infoMap.put("des",description);
               infoMap.put("edu",education);
               infoMap.put("req",requirement);
               infoMap.put("expe",experience);
               infoMap.put("category",category);
               infoMap.put("loc",division);
               infoMap.put("company",company);
               infoMap.put("deadLine",deadLine);
               infoMap.put("date",methods.getDateWithTime());
               infoMap.put("salary",salary);
               infoMap.put("comUrl",comUrl);
               infoMap.put("comAddress",comAddress);
               infoMap.put("vacancy",vacancy);

               if(internetConnection.isOnline())
                    backgroundTask.insertData(getString(R.string.postJob),infoMap);
               else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
          }else displayMessage.errorMessage(getString(R.string.infoErr));
     }

     //if input is not valid ,it's shows an error
     protected void textChangeListener()
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
                    if(title.length()<10)
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
                    if(text.length()<15)
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
                    if(text.length()<15)
                         txtEdu.setError(INVALID_MSG,iconInvalid);
                    else txtEdu.setError(VALID_MSG,iconValid);
               }
          });//check education,must be more than 15 characters

          txtReq.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtReq.getText().toString();
                    if(text.length()<15)
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
          });//check salary

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

          txtExp.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtExp.getText().toString();
                    if(text.length()<15)
                         txtExp.setError(INVALID_MSG,iconInvalid);
                    else txtExp.setError(VALID_MSG,iconValid);
               }
          });//check requirement,must be more than 15 characters

          txtComUrl.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @SuppressLint("SetTextI18n")
               @Override
               public void afterTextChanged(Editable editable) {

                   if(!editable.toString().startsWith("http://"))//set default text http
                   {
                       txtComUrl.setText("http://");
                       Selection.setSelection(txtComUrl.getText(),txtComUrl.getText().length());
                   }

                    String text = txtComUrl.getText().toString();
                    if(text.length()<15)
                         txtComUrl.setError(INVALID_MSG,iconInvalid);
                    else txtComUrl.setError(VALID_MSG,iconValid);
               }
          });//check company url,must be more than 10 characters

          txtComAddress.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String text = txtComAddress.getText().toString();
                    if(text.length()<15)
                         txtComAddress.setError(INVALID_MSG,iconInvalid);
                    else txtComAddress.setError(VALID_MSG,iconValid);
               }
          });//check company address,must be more than 15 characters
     }

     //job info validator
     protected boolean dataValidator()
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
          experience = txtExp.getText().toString();
          comAddress = txtComAddress.getText().toString();
          comUrl = txtComUrl.getText().toString();
          vacancy = txtVacancy.getText().toString();

          if(TextUtils.isEmpty(title)||(title.length()<10))
          {
               txtTitle.setError(INVALID_MSG,iconInvalid);
               txtTitle.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(description)||(description.length()<15))
          {
               txtDes.setError(INVALID_MSG,iconInvalid);
               txtDes.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(education)||(education.length()<15))
          {
               txtEdu.setError(INVALID_MSG,iconInvalid);
               txtEdu.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(requirement)||(requirement.length()<15))
          {
               txtReq.setError(INVALID_MSG,iconInvalid);
               txtReq.setFocusable(true);
               return false;
          }

          if(TextUtils.isEmpty(vacancy)||vacancy.equals("0"))
          {
               Toast.makeText(this,"Please input number of vacancy",Toast.LENGTH_LONG).show();
               return false;
          }
          if(TextUtils.isEmpty(experience)||(experience.length()<15))
          {
               txtExp.setError(INVALID_MSG,iconInvalid);
               txtExp.setFocusable(true);
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
          if(TextUtils.isEmpty(comAddress)||comAddress.length()<15)
          {
               txtComAddress.setError(INVALID_MSG,iconInvalid);
               txtComAddress.setFocusable(true);
               return false;
          }
          if(TextUtils.isEmpty(comUrl)||comUrl.length()<10)
          {
               txtComUrl.setError(INVALID_MSG,iconInvalid);
               txtComUrl.setFocusable(true);
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
     public void processJsonData(String jsonData) {}

     //set job location in spinner
     protected void spinnerLocation()
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
     protected void spinnerCategory()
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

               case R.id.rIntern:
                    if(checked)jobType="internship";
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

          txtDeadLine.setText(year+"-"+m+"-"+d);
          txtDeadLine.setError("Valid",iconValid);
     }

     //post new job result
     OnResponseTask onResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         if(value.equals("success"))
                         {
                              progressBar.setVisibility(View.VISIBLE);
                              Thread thread = new Thread(new Runnable() {
                                   @Override
                                   public void run() {
                                        try
                                        {
                                             Thread.sleep(getResources().getInteger(R.integer.progTime));
                                             runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                       progressBar.setVisibility(View.INVISIBLE);
                                                       displayMessage.congratesMessage("Your post has been posted successfully");
                                                  }
                                             });
                                        }catch (InterruptedException e)
                                        {
                                             e.printStackTrace();
                                        }
                                   }
                              });
                              thread.start();
                         }
                         else{
                              bDone.setEnabled(true);
                              bDone.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_done));
                              progressBar.setVisibility(View.INVISIBLE);
                              displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
                         }
                    }
               });
          }
     };

}
