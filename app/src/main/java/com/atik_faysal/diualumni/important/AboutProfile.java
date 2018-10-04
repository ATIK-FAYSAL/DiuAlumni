package com.atik_faysal.diualumni.important;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.ExperienceAdapter;
import com.atik_faysal.diualumni.adapter.UrlsAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.JobPortal;
import com.atik_faysal.diualumni.models.ExperienceModel;
import com.atik_faysal.diualumni.models.UrlsModel;
import com.atik_faysal.diualumni.others.AdditionalInfo;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import android.content.Context;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AboutProfile extends Fragment implements Methods,View.OnClickListener
{

     private View view;
     private TextView txtId;
     private TextView txtStatus;
     private TextView txtGender;
     private TextView txtBatch;
     private TextView txtDate;
     private TextView txtDept;
     private TextView txtAddUrl;
     private TextView txtChoose,txtWork;
     private EditText txtEmail,txtAddress,txtPhone,txtName;
     private Button bChanges;
     private ImageView imgEdit;
     private CircleImageView imgUser;
     private RadioButton rStudent,rAlumni;
     private RecyclerView recyclerView,expList;
     private LinearLayoutManager layoutManager;
     private LinearLayoutManager manager;

     protected CheckInternetConnection internetConnection;
     private DisplayMessage displayMessage;
     protected PostInfoBackgroundTask backgroundTask;
     protected SharedPreferencesData sharedPreferencesData;
     private RequireMethods methods;
     protected AlertDialog.Builder builder;
     protected AlertDialog alertDialog;
     AdditionalInfo additionalInfo;
     protected Context context;

     private String name,stdId,email,phone,type,gender,batch,address,date,status,department,facebook,linkedin;
     private static String USER;
     private static final int IMAGE_SIZE=2048,PICK_IMAGE_REQUEST=1;
     private Bitmap bitmap;
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
          imgUser = view.findViewById(R.id.imgUser);
          bChanges = view.findViewById(R.id.bDone);
          imgEdit = view.findViewById(R.id.imgEdit);
          rStudent = view.findViewById(R.id.rStudent);
          rAlumni = view.findViewById(R.id.rAlumni);
          txtDept = view.findViewById(R.id.txtDept);
          txtAddUrl = view.findViewById(R.id.txtAddUrl);
          recyclerView = view.findViewById(R.id.urlList);
          expList = view.findViewById(R.id.expList);
          layoutManager = new LinearLayoutManager(getContext());
          manager = new LinearLayoutManager(getContext());
          bChanges.setBackgroundDrawable(getResources().getDrawable(R.drawable.disable_button));
          txtChoose = view.findViewById(R.id.txtChoose);
          context = getContext();

          bChanges.setOnClickListener(AboutProfile.this);
          imgUser.setOnClickListener(AboutProfile.this);
          imgEdit.setOnClickListener(AboutProfile.this);
          txtAddUrl.setOnClickListener(AboutProfile.this);
          txtChoose.setOnClickListener(AboutProfile.this);
          txtWork.setOnClickListener(AboutProfile.this);

          backgroundTask = new PostInfoBackgroundTask(getContext(),onResponseTask);
          internetConnection = new CheckInternetConnection(getContext());
          displayMessage = new DisplayMessage(getContext());
          methods = new RequireMethods(getContext());
          sharedPreferencesData = new SharedPreferencesData(getContext());
          additionalInfo = new AdditionalInfo(getContext());
          USER = Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).getString("user");

          //calling method
          enableDisableField(false);//disable all editText
          retrieveUserInfo();//retrieve value from server
          retrieveUserUrls();//retrieve user urls
          retrieveUserExperience();//retrieve user experience
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
                    txtChoose.setVisibility(View.VISIBLE);
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
               case R.id.txtChoose:
                    openGallery();//open phone gallery and select image
                    break;
               case R.id.txtAddUrl:
                    additionalInfo.onResultSuccess(task);
                    additionalInfo.userUrls();
                    break;
               case R.id.txtWork:
                    additionalInfo.onResultSuccess(task);
                    additionalInfo.workExperience();
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
          txtChoose.setEnabled(flag);
     }

     //get user info from server
     private void retrieveUserInfo()
     {
          Map<String,String>maps = new HashMap<>();
          maps.put("option","userInfo");
          maps.put("stdId",USER);
          if(internetConnection.isOnline())
               backgroundTask.InsertData(Objects.requireNonNull(getActivity()).getResources().getString(R.string.readInfo),maps);
     }

     //get user's all urls from server
     public void retrieveUserUrls()
     {
          backgroundTask = new PostInfoBackgroundTask(context,getRespTask);
          Map<String,String>maps = new HashMap<>();
          maps.put("option","userUrls");
          maps.put("stdId",USER);
          if(internetConnection.isOnline())
               backgroundTask.InsertData(context.getResources().getString(R.string.readInfo),maps);
     }

     //retrieve user experience
     public void retrieveUserExperience()
     {
          backgroundTask = new PostInfoBackgroundTask(context,getTask);
          Map<String,String>maps = new HashMap<>();
          maps.put("option","experience");
          maps.put("stdId",USER);
          if(internetConnection.isOnline())
               backgroundTask.InsertData(context.getResources().getString(R.string.readInfo),maps);
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

     //process user info json data to view format
     @SuppressLint("SetTextI18n")
     @Override
     public void processJsonData(String jsonData)
     {
          try {
               JSONObject rootObject = new JSONObject(jsonData);
               JSONArray rootArray = rootObject.optJSONArray("info");
               int count=0;
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
               String imageName = root.getString("imageName");
               if(!imageName.equals("none"))
                    Glide.with(this).
                         load(getResources().getString(R.string.address)+imageName+".png").
                         into(imgUser);

               viewUserInfo();//show user info
          } catch (JSONException e) {
               displayMessage.errorMessage(e.toString());
          }

     }

     //getting current user url and show in recylerview
     public void processUserUrlJsonData(String jsonData)
     {
          List<UrlsModel>urlsModels = new ArrayList<>();
          try {
               JSONObject rootObj = new JSONObject(jsonData);
               JSONArray rootArray = rootObj.optJSONArray("links");
               int count=0;
               while (count<rootArray.length())
               {
                    JSONObject object = rootArray.getJSONObject(count);
                    String stdId = object.getString("stdId");
                    String url = object.getString("url");
                    String id = object.getString("id");
                    urlsModels.add(new UrlsModel(stdId,id,url));
                    count++;
               }
               UrlsAdapter adapter = new UrlsAdapter(getContext(), urlsModels);//create adapter
               recyclerView.setAdapter(adapter);//set adapter in recyler view
               layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
               recyclerView.setLayoutManager(layoutManager);
               recyclerView.setItemAnimator(new DefaultItemAnimator());
          } catch (JSONException e) {
               e.printStackTrace();
          }
     }

     private void processJsonUserExperience(String jsonData)
     {
          List<ExperienceModel>modelList = new ArrayList<>();

          try {
               JSONObject jsonObject = new JSONObject(jsonData);
               JSONArray jsonArray = jsonObject.getJSONArray("info");
               int count=0;
               while(count<jsonArray.length())
               {
                    JSONObject object = jsonArray.getJSONObject(count);
                    String id = object.getString("id");
                    String stdId = object.getString("stdId");
                    String company = object.getString("company");
                    String position = object.getString("position");
                    String description = object.getString("description");
                    String from = object.getString("from");
                    String to = object.getString("to");
                    String city = object.getString("city");

                    modelList.add(new ExperienceModel(id,stdId,company,position,description,from,to,city));
                    count++;
               }

               ExperienceAdapter adapter = new ExperienceAdapter(getContext(), modelList);//create adapter
               expList.setAdapter(adapter);//set adapter in recyler view
               layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
               expList.setLayoutManager(manager);
               expList.setItemAnimator(new DefaultItemAnimator());
          } catch (JSONException e) {
               displayMessage.errorMessage(getResources().getString(R.string.jsonErr));
          }catch (NullPointerException e)
          {
               displayMessage.errorMessage(getResources().getString(R.string.nullPointer));
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

          if(!stdId.equals(sharedPreferencesData.getCurrentUserId()))//check is that current user or not
          {
               imgEdit.setImageDrawable(null);
               imgEdit.setEnabled(false);
               bChanges.setVisibility(View.GONE);
               txtAddUrl.setEnabled(false);
               txtAddUrl.setText("URL");
               txtWork.setText("Work Experience");
          }
     }

     //open galley and select image
     private void openGallery()
     {
          if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
               ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

          else
          {
               Intent intent = new Intent();
               intent.setType("image/*");
               intent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(intent,1);
          }
     }

     //image upload to server
     private void uploadImage(final Bitmap imageBit)
     {
          builder = new AlertDialog.Builder(getContext());
          View view = LayoutInflater.from(getContext()).inflate(R.layout.image_viewer,null);
          builder.setView(view);
          builder.setCancelable(false);
          ImageView imageView = view.findViewById(R.id.uImage);
          Button bCancel,bUpload;

          bCancel = view.findViewById(R.id.cancel);
          bUpload = view.findViewById(R.id.bUpload);


          if(bitmap!=null)
               imageView.setImageBitmap(imageBit);

          alertDialog = builder.create();
          alertDialog.show();

          bCancel.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    alertDialog.dismiss();
               }
          });

          bUpload.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(getContext(),respTask);
                    Map<String,String>maps = new HashMap<>();
                    maps.put("stdId",sharedPreferencesData.getCurrentUserId());
                    maps.put("image",convertImageToString(imageBit));
                    maps.put("date",methods.getDate());
                    maps.put("imageName",sharedPreferencesData.getImageName());
                    if(internetConnection.isOnline())
                         backgroundTask.InsertData(getResources().getString(R.string.uploadImage),maps);
                    else displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
                    alertDialog.dismiss();
               }
          });

     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
          if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
               Uri imageUri = data.getData();
               try {
                    File file = new File(getRealPathFromURI(imageUri));
                    long length = file.length()/1024;//in kb

                    if(length>IMAGE_SIZE)
                         displayMessage.errorMessage("Large image size,please select image less than 2.0 MB");
                    else
                    {
                         bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                         uploadImage(bitmap);
                    }
               } catch (IOException e) {
                    e.printStackTrace();
               }
          }
     }

     //get image real path from image uri
     private String getRealPathFromURI(Uri contentUri) {
          String[] imageData = { MediaStore.Images.Media.DATA };
          CursorLoader loader = new CursorLoader(getContext(), contentUri, imageData, null, null, null);
          Cursor cursor = loader.loadInBackground();
          int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          cursor.moveToFirst();
          String result = cursor.getString(column_index);
          cursor.close();
          return result;
     }

     //convert bitmap to string
     private String convertImageToString(Bitmap bmp){
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
          byte[] imageBytes = stream.toByteArray();
          return Base64.encodeToString(imageBytes, Base64.DEFAULT);
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
                                   maps.put("stdId",sharedPreferencesData.getCurrentUserId());
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

     //getting imageName
     private OnResponseTask respTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
               Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         try {
                              JSONObject jsonObject = new JSONObject(value);
                              JSONArray jsonArray = jsonObject.optJSONArray("response");
                              JSONObject object = jsonArray.getJSONObject(0);
                              if(object.getString("result").equals("success"))
                              {
                                   sharedPreferencesData.userImageName(object.getString("imgName"));
                                   imgUser.setImageBitmap(bitmap);
                              }else
                                   displayMessage.errorMessage(getResources().getString(R.string.executionFailed));

                         } catch (JSONException e) {
                              displayMessage.errorMessage(e.toString());
                         }
                    }
               });
          }
     };

     //getting url server response
     private OnResponseTask getRespTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               if(value!=null&&!value.equals("notArray"))
                   processUserUrlJsonData(value);//getting user url
          }
     };

     //if new url added then again call retrieveUserUrls to show new url in recylerView
     private OnResponseTask task = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               if(value.equals("success"))
               {
                    retrieveUserUrls();
                    retrieveUserExperience();
               }
          }
     };

     private OnResponseTask getTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               if(value!=null&&!value.equals("notArray"))
                    processJsonUserExperience(value);
          }
     };
}
