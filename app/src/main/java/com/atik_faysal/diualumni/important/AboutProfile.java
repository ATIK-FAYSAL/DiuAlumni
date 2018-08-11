package com.atik_faysal.diualumni.important;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.ViewPagerAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.JobPortal;
import com.atik_faysal.diualumni.main.SignIn;
import com.atik_faysal.diualumni.main.UserRegistration;
import com.atik_faysal.diualumni.others.SetTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutProfile extends Fragment implements Methods,View.OnClickListener
{

     private View view;
     private TextView txtId,txtStatus,txtWork,txtGender,txtBatch,txtDate,txtDept,txtAddUrl;
     private EditText txtEmail,txtAddress,txtPhone,txtName;
     private Button bChanges;
     private ImageView imgEdit;
     private RadioGroup radioGroup;
     private RadioButton rStudent,rAlumni;
     private RecyclerView recyclerView;
     private LinearLayoutManager layoutManager;

     private CheckInternetConnection internetConnection;
     private DisplayMessage displayMessage;
     private PostInfoBackgroundTask backgroundTask;
     private SharedPreferencesData sharedPreferencesData;
     private RequireMethods methods;

     private AlertDialog.Builder builder;
     private AlertDialog alertDialog;

     private String name,stdId,email,phone,type,gender,batch,address,date,status,department,facebook,linkedin;
     private static String USER;

     @Nullable
     @Override
     public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          view = inflater.inflate(R.layout.profile_about, container, false);
          initComponent();
          return view;
     }

     //initialize component
     @Override
     public void initComponent() {
          txtName = view.findViewById(R.id.txtName);
          txtId = view.findViewById(R.id.txtStdId);
          txtWork = view.findViewById(R.id.txtWork);
          txtGender = view.findViewById(R.id.txtGender);
          txtBatch = view.findViewById(R.id.txtBatch);
          txtDate = view.findViewById(R.id.txtDate);
          txtPhone = view.findViewById(R.id.txtPhone);
          txtEmail = view.findViewById(R.id.txtEmail);
          txtAddress = view.findViewById(R.id.txtAddress);
          txtStatus = view.findViewById(R.id.txtStatus);
          CircleImageView imageView = view.findViewById(R.id.imgUser);
          bChanges = view.findViewById(R.id.bDone);
          imgEdit = view.findViewById(R.id.imgEdit);
          rStudent = view.findViewById(R.id.rStudent);
          rAlumni = view.findViewById(R.id.rAlumni);
          txtDept = view.findViewById(R.id.txtDept);
          txtAddUrl = view.findViewById(R.id.txtAddUrl);
          recyclerView = view.findViewById(R.id.urlList);
          layoutManager = new LinearLayoutManager(getContext());
          bChanges.setBackgroundDrawable(getResources().getDrawable(R.drawable.disable_button));

          bChanges.setOnClickListener(AboutProfile.this);
          imageView.setOnClickListener(AboutProfile.this);
          imgEdit.setOnClickListener(AboutProfile.this);
          txtAddUrl.setOnClickListener(AboutProfile.this);

          backgroundTask = new PostInfoBackgroundTask(getContext(),onResponseTask);
          internetConnection = new CheckInternetConnection(getContext());
          displayMessage = new DisplayMessage(getContext());
          methods = new RequireMethods(getContext());
          sharedPreferencesData = new SharedPreferencesData(getContext());

          //calling method
          enableDisableField(false);//disable all editText
          retrieveUserInfo();//retrieve value from server
     }

     //on button clicklistener
     @Override
     public void onClick(View view) {
          switch (view.getId())
          {
               case R.id.imgEdit:
                    enableDisableField(true);
                    rStudent.setEnabled(true);
                    rAlumni.setEnabled(true);
                    bChanges.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_done));

                    break;
               case R.id.bDone:

                    if(infoValidator())
                    {
                         backgroundTask = new PostInfoBackgroundTask(getContext(),responseTask);
                         Map<String,String>maps = new HashMap<>();
                         maps.put("option","userUpdate");
                         maps.put("stdId",USER);
                         maps.put("name",name);
                         maps.put("phone",phone);
                         maps.put("email",email);
                         maps.put("address",address);

                         if(internetConnection.isOnline())
                              backgroundTask.InsertData(Objects.requireNonNull(getActivity()).getResources().getString(R.string.updateOperation),maps);
                         else displayMessage.errorMessage(Objects.requireNonNull(getActivity()).getResources().getString(R.string.noInternet));
                    }
                    break;
               case R.id.imgUser:
                    break;
               case R.id.txtAddUrl:
                    addNewUrl();//add new url
                    break;
          }
     }

     //disable & enable text field
     private void enableDisableField(boolean flag)
     {
          txtName.setEnabled(flag);
          txtPhone.setEnabled(flag);
          txtEmail.setEnabled(flag);
          txtAddress.setEnabled(flag);
          bChanges.setEnabled(flag);
     }

     //get user info from server
     private void retrieveUserInfo()
     {
          USER = Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).getString("user");
          Map<String,String>maps = new HashMap<>();
          maps.put("option","userInfo");
          maps.put("stdId",USER);
          if(internetConnection.isOnline())
               backgroundTask.InsertData(getActivity().getResources().getString(R.string.readInfo),maps);
          else displayMessage.errorMessage(getActivity().getResources().getString(R.string.noInternet));
     }

     @Override
     public void setToolbar() {}

     //update info validator
     private boolean infoValidator()
     {
          name = txtName.getText().toString();
          phone = txtPhone.getText().toString();
          email = txtEmail.getText().toString();
          address = txtAddress.getText().toString();


          if (TextUtils.isEmpty(name)||name.length() < 8 || name.length() > 30)
          {
               txtName.setFocusable(true);
               displayMessage.errorMessage("Invalid user name,please insert a valid name");
               return false;
          }
          else {
               for (int i = 0; i < name.length(); i++) {
                    if (name.charAt(i) >= '0' && name.charAt(i) <= '9' ||
                         name.charAt(i) == ':' ||
                         name.charAt(i) == '*' ||
                         name.charAt(i) == '@' ||
                         name.charAt(i) == '!' ||
                         name.charAt(i) == '#' ||
                         name.charAt(i) == '&')
                    {
                         txtEmail.setFocusable(true);
                         displayMessage.errorMessage("Invalid user email,please insert a valid email");
                         return false;
                    }
               }
          }

          if(TextUtils.isEmpty(email))
          {
               txtEmail.setFocusable(true);
               displayMessage.errorMessage("Invalid email,please input a valid email");
               return false;
          }
          if(email.length()<13||(!email.contains(".")||!email.contains("@")))
          {
               txtEmail.setFocusable(true);
               displayMessage.errorMessage("Invalid email,please input a valid email");
               return false;
          }

          if(TextUtils.isEmpty(phone))
          {

               txtPhone.setFocusable(true);
               return false;
          }
          if(phone.length()>6) {
               if (phone.substring(0, 3).equals("+88")) {
                    if (phone.length() != 14) {
                         txtPhone.setFocusable(true);
                         displayMessage.errorMessage("Invalid phone,please input a valid phone");
                         return false;
                    } else {
                         switch (phone.substring(3, 6)) {
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
                              default: {
                                   txtPhone.setFocusable(true);
                                   displayMessage.errorMessage("Invalid phone,please input a valid phone");
                                   return false;
                              }
                         }
                    }
               } else if (!phone.substring(0, 3).equals("+88")) {
                    if (phone.length() != 11) {
                         txtPhone.setFocusable(true);
                         displayMessage.errorMessage("Invalid phone,please input a valid phone");
                         return false;
                    } else {
                         switch (phone.substring(0, 3)) {
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
                              default: {
                                   txtPhone.setFocusable(true);
                                   displayMessage.errorMessage("Invalid phone,please input a valid phone");
                                   return false;
                              }
                         }
                    }
               } else {
                    txtPhone.setFocusable(true);
                    displayMessage.errorMessage("Invalid phone,please input a valid phone");
                    return false;
               }

               if (TextUtils.isEmpty(address) || (address.length() < 10 || address.length() > 50)) {
                    txtAddress.setFocusable(true);
                    displayMessage.errorMessage("Invalid address,please input a valid address");
                    return false;
               }
          }else
          {
               txtPhone.setFocusable(true);
               displayMessage.errorMessage("Invalid phone,please input a valid phone");
               return false;
          }
               return true;
     }

     //process json data to view format
     @SuppressLint("SetTextI18n")
     @Override
     public void processJsonData(String jsonData)
     {
          try {
               JSONObject rootObject = new JSONObject(jsonData);
               JSONArray rootArray = rootObject.optJSONArray("info");
               int count=0;
               while(count<rootArray.length())
               {
                    JSONObject root = rootArray.getJSONObject(count);

                    name = root.getString("name");
                    stdId = root.getString("stdId");
                    email = root.getString("email");
                    phone = root.getString("phone");
                    gender = root.getString("gender");
                    batch = root.getString("batch");
                    type = root.getString("type");
                    address = root.getString("address");
                    date = root.getString("date");
                    status = root.getString("status");
                    department = root.getString("department");

                    viewUserInfo();
                    count++;
               }
          } catch (JSONException e) {
               displayMessage.errorMessage(e.toString());
          }

     }

     //set user info in UI
     @SuppressLint("SetTextI18n")
     private void viewUserInfo()
     {
          txtName.setText(name);
          txtId.setText(stdId);
          txtEmail.setText(email);
          txtPhone.setText(phone);
          txtGender.setText(gender);
          txtBatch.setText(batch);
          txtAddress.setText(address);
          txtDate.setText("Join at "+date);
          txtDept.setText(department);

          if(status.equals("pending"))
               txtStatus.setVisibility(View.VISIBLE);

          if(type.equals("student"))
          {
               //if user is student,then it's select
               rStudent.setChecked(true);
               rAlumni.setChecked(false);
               rAlumni.setEnabled(false);
          }else if(type.equals("alumni"))
          {
               //if user is alumni,then it's select
               rStudent.setChecked(false);
               rAlumni.setChecked(true);
               rStudent.setEnabled(false);
          }

          if(!stdId.equals(sharedPreferencesData.getStudentId()))//check is that current user or not
          {
               imgEdit.setImageDrawable(null);
               imgEdit.setEnabled(false);
               bChanges.setVisibility(View.INVISIBLE);
          }
     }

     //add new URL alertBox.add url success,still incomplete show url,update url,and visit url.also incomplete add work
     public void addNewUrl()
     {
          Button bAdd;
          final EditText txtUrl;ImageView imgClear;
          builder = new AlertDialog.Builder(getContext());
          @SuppressLint("InflateParams")
          View view = LayoutInflater.from(getContext()).inflate(R.layout.add_url,null);
          bAdd = view.findViewById(R.id.bAdd);
          txtUrl = view.findViewById(R.id.txtUrl);
          imgClear = view.findViewById(R.id.imgClear);

          builder.setView(view);
          builder.setCancelable(false);
          alertDialog = builder.create();
          alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          alertDialog.show();

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
                         Toast.makeText(getContext(),"Invalid Url",Toast.LENGTH_LONG).show();
                    else
                    {
                         backgroundTask = new PostInfoBackgroundTask(getContext(),responseTask);
                         Map<String,String> maps = new HashMap<>();
                         maps.put("option","putUrl");
                         maps.put("stdId",sharedPreferencesData.getStudentId());
                         maps.put("url",url);
                         if(internetConnection.isOnline())
                              backgroundTask.InsertData(getActivity().getResources().getString(R.string.insertOperation),maps);
                         else Toast.makeText(getContext(),getActivity().getResources().getString(R.string.noInternet),Toast.LENGTH_LONG).show();

                         alertDialog.dismiss();
                    }
               }
          });
     }

     //get user info server response
     OnResponseTask onResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
               Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         processJsonData(value);
                    }
               });
          }
     };

     //update information server response
     OnResponseTask responseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
               Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         switch (value)
                         {
                              case "success":
                                   Map<String,String>maps = new HashMap<>();
                                   maps.put("name",name);
                                   maps.put("email",email);
                                   maps.put("phone",phone);
                                   maps.put("type",type);
                                   sharedPreferencesData = new SharedPreferencesData(getContext(),maps);//context and user info map
                                   sharedPreferencesData.currentUserInfo();//store current user information

                                   displayMessage.progressDialog("Please wait.....","Updating your information.",JobPortal.class);
                                   break;
                                   default:
                                        displayMessage.errorMessage(getActivity().getResources().getString(R.string.updateFailed));
                                        break;
                         }
                    }
               });
          }
     };

     //insert url server resoponse
     private OnResponseTask getOnResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
               getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         if (value.equals("success"))
                         {
                              Toast.makeText(getContext(),"Url inserted",Toast.LENGTH_LONG).show();
                         }

                    }
               });
          }
     };

}
