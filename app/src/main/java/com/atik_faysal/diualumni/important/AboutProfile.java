
package com.atik_faysal.diualumni.important;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Switch;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.ExperienceAdapter;
import com.atik_faysal.diualumni.adapter.UrlsAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.JobPortal;
import com.atik_faysal.diualumni.messages.PersonMessage;
import com.atik_faysal.diualumni.models.ExperienceModel;
import com.atik_faysal.diualumni.models.UrlsModel;
import com.atik_faysal.diualumni.others.AdditionalInfo;
import com.atik_faysal.diualumni.others.UserPermission;
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
    private TextView txtId,txtStatus,txtGender,txtBatch,txtDate,txtDept,txtAddUrl,txtChoose,txtWork,txtMsgStatus;
    private EditText txtEmail,txtAddress,txtPhone,txtName;
    private Button bChanges;
    private TextView imgEdit,imgMessage;
    private CircleImageView imgUser;
    private RadioButton rStudent,rAlumni;
    private RecyclerView recyclerView,expList;
    private LinearLayoutManager layoutManager;
    private LinearLayoutManager manager;
    private Switch msgSwitch;

    protected CheckInternetConnection internetConnection;
    private DisplayMessage displayMessage;
    protected PostInfoBackgroundTask backgroundTask;
    protected SharedPreferencesData sharedPreferencesData;
    private RequireMethods methods;
    protected AlertDialog.Builder builder;
    private ProgressDialog progressDialog;
    protected AlertDialog alertDialog;
    protected AdditionalInfo additionalInfo;
    protected Context context;
    private UserPermission permission;

    private String name,stdId,email,phone,type,gender,batch,address,date,status,department,msgStatus,msgSetting;
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
        msgSwitch = view.findViewById(R.id.messOption);
        txtMsgStatus = view.findViewById(R.id.msgStatus);
        imgMessage = view.findViewById(R.id.imgMessage);
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
        imgMessage.setOnClickListener(AboutProfile.this);

        backgroundTask = new PostInfoBackgroundTask(getContext(),onResponseTask);
        internetConnection = new CheckInternetConnection(getContext());
        displayMessage = new DisplayMessage(getContext());
        methods = new RequireMethods(getContext());
        progressDialog = new ProgressDialog(getContext());
        sharedPreferencesData = new SharedPreferencesData(getContext());
        additionalInfo = new AdditionalInfo(getContext());
        USER = Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).getString("user");
        msgSetting = sharedPreferencesData.getMessageSettings();//get current message setting status

        //calling method
        retrieveUserInfo();//retrieve value from server
        retrieveUserUrls();//retrieve user urls
        retrieveUserExperience();//retrieve user experience
        enableDisableField(false);//disable all editText
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
                msgSwitch.setVisibility(View.VISIBLE);
                txtMsgStatus.setVisibility(View.INVISIBLE);
                bChanges.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_done));

                msgSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(msgSwitch.isChecked())
                            msgSetting = "enable";
                        else msgSetting = "disable";
                    }
                });
                break;
            case R.id.bDone:

                if(infoValidator())
                {
                    Map<String,String>maps = new HashMap<>();

                    maps.put("option","userUpdate");
                    maps.put("stdId",USER);
                    maps.put("name",name);
                    maps.put("phone",phone);
                    maps.put("email",email);
                    maps.put("address",address);
                    maps.put("msgStatus",msgSetting);

                    if(internetConnection.isOnline())
                    {
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("Please wait....");
                        progressDialog.setMessage("Updating your information");
                        progressDialog.show();
                        PostInfoBackgroundTask infoBackgroundTask = new PostInfoBackgroundTask(getContext(),responseTask);
                        infoBackgroundTask.insertData(Objects.requireNonNull(getActivity()).getResources().getString(R.string.updateOperation),maps);
                    }
                    else displayMessage.errorMessage(Objects.requireNonNull(getActivity()).getResources().getString(R.string.noInternet));
                }
                break;
            case R.id.txtChoose:
                //if storage read permission is not granted
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
                    ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
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
            case R.id.imgMessage:
                Intent intent = new Intent(getActivity(),PersonMessage.class);
                intent.putExtra("receiverId",USER);
                intent.putExtra("receiverName",txtName.getText().toString());
                startActivity(intent);
                break;
        }
    }

    //disable & enable text field
    @SuppressLint("ResourceAsColor")
    private void enableDisableField(boolean flag)
    {
        txtName.setEnabled(flag);
        txtPhone.setEnabled(flag);
        txtEmail.setEnabled(flag);
        txtAddress.setEnabled(flag);
        bChanges.setEnabled(flag);
        txtChoose.setEnabled(flag);
        msgSwitch.setVisibility(View.GONE);
        txtMsgStatus.setText(sharedPreferencesData.getMessageSettings());
    }

    //get user info from server
    private void retrieveUserInfo()
    {
        Map<String,String>maps = new HashMap<>();
        maps.put("option","userInfo");
        maps.put("stdId",USER);
        if(internetConnection.isOnline())
            backgroundTask.insertData(Objects.requireNonNull(getActivity()).getResources().getString(R.string.readInfo),maps);
    }

    //get user's all urls from server
    public void retrieveUserUrls()
    {
        backgroundTask = new PostInfoBackgroundTask(context,getRespTask);
        Map<String,String>maps = new HashMap<>();
        maps.put("option","userUrls");
        maps.put("stdId",USER);
        if(internetConnection.isOnline())
            backgroundTask.insertData(context.getResources().getString(R.string.readInfo),maps);
    }

    //retrieve user experience
    public void retrieveUserExperience()
    {
        backgroundTask = new PostInfoBackgroundTask(context,getTask);
        Map<String,String>maps = new HashMap<>();
        maps.put("option","experience");
        maps.put("stdId",USER);
        if(internetConnection.isOnline())
            backgroundTask.insertData(context.getResources().getString(R.string.readInfo),maps);
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
            msgStatus = root.getString("msgStatus");
            department = root.getString("department");
            String imageName = root.getString("imageName");

            if(msgStatus.equals("enable"))//if user message option is enable then message icon show
                imgMessage.setVisibility(View.VISIBLE);
            if(!imageName.equals("none"))//if user has image,then it will show
                Glide.with(this).
                        load(getResources().getString(R.string.address)+imageName+".png").
                        into(imgUser);

            viewUserInfo();//user info show in UI .
        } catch (JSONException e) {//if JSON exception occur
            displayMessage.errorMessage(e.toString());
        }

    }

    //getting current user url and show in recyclerView
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

    //user experience retrieve and process json data
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

        if(status.equals("pending"))//if Alumni information is not verify by admin,then this warning is show
            txtStatus.setVisibility(View.VISIBLE);

        if(type.equals("student")) //if user is student,then it's select
        {
            rStudent.setChecked(true);//student radio button is selected
            rAlumni.setChecked(false);//alumni radio button is not selected
            rAlumni.setEnabled(false);//alumni radio button is disable
        }else if(type.equals("alumni"))//if user is alumni,then it's select
        {
            rStudent.setChecked(false);//student radio button is not selected
            rAlumni.setChecked(true);//alumni radio button is selected
            rStudent.setEnabled(false);//student radio button is disable
        }

        /*
         * If this user is not current user
         * Edit button is invisible and disable
         * Save Changes button is invisib   le and disable
         * url and experience text change
         * url and experience remove and update button disable and invisible
         */
        if(sharedPreferencesData.getCurrentUserId().equals(stdId))
        {
            imgMessage.setVisibility(View.GONE);
            sharedPreferencesData.setMessageSetting(msgStatus);//store current user message setting
            if(sharedPreferencesData.getMessageSettings().equals("enable"))//if user message option is enable then the switch is on
                msgSwitch.setChecked(true);
            else msgSwitch.setChecked(false);//otherwise switch is disable
        }else{
            //imgEdit.setImageDrawable(null);
            //imgEdit.setEnabled(false);
            imgEdit.setVisibility(View.GONE);
            bChanges.setVisibility(View.GONE);
            txtAddUrl.setEnabled(false);
            txtAddUrl.setText("URL");
            txtWork.setText("Work Experience");
            msgSwitch.setVisibility(View.GONE);
            view.findViewById(R.id.settingText).setVisibility(View.GONE);
            txtMsgStatus.setVisibility(View.GONE);
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
                    backgroundTask.insertData(getResources().getString(R.string.uploadImage),maps);
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
        CursorLoader loader = new CursorLoader(getActivity(), contentUri, imageData, null, null, null);
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
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Map<String,String>maps = new HashMap<>();
                                        maps.put("stdId",sharedPreferencesData.getCurrentUserId());
                                        maps.put("name",name);
                                        maps.put("email",email);
                                        maps.put("phone",phone);
                                        maps.put("type",type);
                                        SharedPreferencesData preferencesData = new SharedPreferencesData(getContext(),maps);//context and user info map
                                        preferencesData.currentUserInfo();//store current user updated information
                                        sharedPreferencesData.setMessageSetting(msgSetting);//update message setting
                                        Thread.sleep(Objects.requireNonNull(getContext()).getResources().getInteger(R.integer.progTime));
                                    } catch (Exception e) {
                                        displayMessage.errorMessage(Objects.requireNonNull(getContext()).getResources().getString(R.string.executionFailed));
                                    }
                                    progressDialog.dismiss();
                                }
                            }).start();
                            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    Toast.makeText(context,"Information's update successfully.",Toast.LENGTH_LONG).show();
                                    methods.closeActivity(getActivity(),JobPortal.class);
                                }
                            });

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