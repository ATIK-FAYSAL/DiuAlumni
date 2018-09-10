package com.atik_faysal.diualumni.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DataValidator;
import com.atik_faysal.diualumni.important.DesEncryptionAlgo;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


import java.util.HashMap;
import java.util.Map;

public class UserRegistration extends AppCompatActivity implements View.OnClickListener
{


     private EditText txtName,txtEmail,txtAddress,txtStdId,txtPass;
     private Spinner spinner,sDepartment;private ProgressBar progressBar;

     private DataValidator validator;
     private CheckInternetConnection internetConnection;
     private DisplayMessage dialogClass;
     private PostInfoBackgroundTask backgroundTask;
     private RequireMethods requireMethods;
     private DesEncryptionAlgo encryptionAlgo;

     private String memberType=null,phone;
     private String gender = null;
     private String name,email,stdId,address,password,batch,department;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.register_layout);
          initComponent();
     }

     //initialize information
     private void initComponent()
     {
          //initialize component variable
          txtName = findViewById(R.id.txtName);
          txtEmail = findViewById(R.id.txtEmail);
          txtAddress = findViewById(R.id.txtAddress);
          txtStdId = findViewById(R.id.txtStdId);
          txtPass = findViewById(R.id.txtPassword);
          spinner = findViewById(R.id.sBatch);
          sDepartment = findViewById(R.id.sDepartment);
          TextView txtSignIn = findViewById(R.id.txtSignIn);
          Button bProceed = findViewById(R.id.bProceed);
          progressBar = findViewById(R.id.progress);

          //set click listener
          txtSignIn.setOnClickListener(this);
          bProceed.setOnClickListener(this);

          validator = new DataValidator(this);
          internetConnection = new CheckInternetConnection(this);
          dialogClass = new DisplayMessage(this);
          backgroundTask = new PostInfoBackgroundTask(this,responseTask);
          requireMethods = new RequireMethods(this);
          encryptionAlgo = new DesEncryptionAlgo(this);

          setToolbar();
          userDataValidator();
          userBatch();//select batch from spinner
          selectDepartment();//select department from spinner
     }

     //button click listener
     @Override
     public void onClick(View view) {
          switch (view.getId())
          {
               case R.id.bProceed:
                    name = txtName.getText().toString();
                    email = txtEmail.getText().toString();
                    stdId = txtStdId.getText().toString();
                    address = txtAddress.getText().toString();
                    password = txtPass.getText().toString();

                    if(batch.equals("Select your batch"))//if don't choose batch
                    {
                         Toast.makeText(UserRegistration.this,"Please select your batch",Toast.LENGTH_LONG).show();
                         return;
                    }

                    if(validator.userDataValidator(name,stdId,email,password,address,memberType,batch,gender))
                    {
                         if(internetConnection.isOnline())
                              onLogin(LoginType.PHONE);
                         else dialogClass.errorMessage(getString(R.string.noInternet));
                    }else dialogClass.errorMessage("Your information is not valid.please check and insert valid information");
                    break;
               case R.id.txtSignIn:
                    finish();
                    break;
          }
     }

     //check user information is valid or not
     private void userDataValidator()
     {
          final Drawable iconValid = getResources().getDrawable(R.drawable.icon_check);//valid icon
          iconValid.setBounds(0,0,iconValid.getIntrinsicWidth(),iconValid.getIntrinsicHeight());
          final Drawable iconInvalid = getResources().getDrawable(R.drawable.icon_wrong);//invalid icon
          iconInvalid.setBounds(0,0,iconInvalid.getIntrinsicWidth(),iconInvalid.getIntrinsicHeight());

          /*if(txtName.getText().toString().isEmpty())
               txtName.setError("Invalid name",iconInvalid);
          if(txtStdId.getText().toString().isEmpty())
               txtStdId.setError("Invalid student id",iconInvalid);
          if(txtEmail.getText().toString().isEmpty())
               txtEmail.setError("Invalid email",iconInvalid);
          if(txtAddress.getText().toString().isEmpty())
               txtAddress.setError("Invalid address",iconInvalid);
          if(txtPass.getText().toString().isEmpty())
               txtPass.setError("Invalid password",iconInvalid);*/

          txtName.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    boolean flag = true;
                    String name = txtName.getText().toString();
                    if(name.length()<8||name.length()>30)
                         flag = false;
                    else
                    {
                         for(int i=0;i<name.length();i++)
                         {
                              if(name.charAt(i)>='0'&&name.charAt(i)<='9'||
                                   name.charAt(i)==':'||
                                   name.charAt(i)=='*'||
                                   name.charAt(i)=='@'||
                                   name.charAt(i)=='!'||
                                   name.charAt(i)=='#'||
                                   name.charAt(i)=='&')
                              {
                                   flag = false;
                                   break;
                              }
                         }
                    }

                    if(flag)
                         txtName.setError("Valid",iconValid);
                    else
                    {
                         txtName.setError("Invalid name",iconInvalid);
                    }
               }
          });//user name validator must be in 8-30 characters and does not contain number and some special character

          txtStdId.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String stdId = txtStdId.getText().toString();
                    boolean flag = true;
                    if(stdId.length()<8||stdId.length()>15)
                         flag = false;
                    for(int i=0;i<stdId.length();i++)
                    {
                         if((stdId.charAt(i) >='0'&&stdId.charAt(i)<='9')||stdId.charAt(i)=='-')
                         {}
                         else
                         {
                              flag = false;
                              break;
                         }
                    }

                    if(flag)txtStdId.setError("Valid",iconValid);
                    else txtStdId.setError("Invalid student id",iconInvalid);
               }
          });//student id validator must in 8-15 characters and only contain '-'

          txtAddress.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String address = txtAddress.getText().toString();
                    if(address.length()<15||address.length()>50)
                         txtAddress.setError("Invalid address",iconInvalid);
                    else txtAddress.setError("Valid",iconValid);
               }
          });//address validator,must be in 15-50 characters

          txtPass.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

               @Override
               public void afterTextChanged(Editable editable) {
                    String pass = txtPass.getText().toString();
                    if(pass.length()<8||pass.length()>20)
                         txtPass.setError("Invalid password",iconInvalid);
                    else
                    {
                         int alphabet=0,number=0,sChar=0;
                         if(txtPass.length() <= 10)
                              txtPass.setError("Short",iconValid);
                         else
                         {
                              for(int i=0;i<pass.length();i++)
                              {
                                   if((pass.charAt(i)>='a'&&pass.charAt(i)<='z')||(pass.charAt(i)>='A'&&pass.charAt(i)<='Z'))
                                        alphabet++;
                                   else if(pass.charAt(i)>='0'&&pass.charAt(i)<='9')
                                        number++;
                                   else sChar++;
                              }

                              if(alphabet>1&&number>1&&sChar>1)
                                   txtPass.setError("Medium",iconValid);
                              else if(alphabet>2||number>2||sChar>2)
                                   txtPass.setError("Strong",iconValid);
                         }
                    }
               }
          });//password validator,must be in 8-20 characters,

          txtEmail.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    String email = txtEmail.getText().toString();
                    if(email.length()<15||email.length()>50||!email.contains("@")||!email.contains("."))
                         txtEmail.setError("Invalid email",iconInvalid);
                    else txtEmail.setError("Valid",iconValid);
               }
          });//email validator,must be in 15-50,must contain "." and "@"
     }

     //set a toolbar,above the page
     public void setToolbar()
     {
          Toolbar toolbar = findViewById(R.id.toolbar);
          setSupportActionBar(toolbar);
          toolbar.setTitleTextColor(getResources().getColor(R.color.white));
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

     //define member type
     public void chooseMemType(View view)
     {
          boolean checked = ((RadioButton)view).isChecked();

          switch (view.getId())
          {
               case R.id.rAlumni:
                    if(checked)memberType="alumni";
                    break;

               case R.id.rStd:
                    if(checked)memberType="student";
                    break;

          }
     }

     //define member type
     public void chooseGender(View view)
     {
          boolean checked = ((RadioButton)view).isChecked();

          switch (view.getId())
          {
               case R.id.rMale:
                    if(checked)gender="male";
                    break;

               case R.id.rFemale:
                    if(checked)gender="female";
                    break;

          }
     }

     //select your batch from spinner
     private void userBatch()
     {
          ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.batch,R.layout.support_simple_spinner_dropdown_item);
          adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
          spinner.setAdapter(adapter);
          spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    batch = parent.getItemAtPosition(i).toString();
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {}
          });
     }

     //select your department from spinner
     private void selectDepartment()
     {
          ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.department,R.layout.support_simple_spinner_dropdown_item);
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

     //new user data inserting into server
     private void registerNewUser(String phone)
     {
          Map<String,String>map = new HashMap<>();
          map.put("option","insert");
          map.put("name",name);
          map.put("email",email);
          map.put("stdId",stdId);
          map.put("phone",phone);
          map.put("address",address);
          map.put("department",department);
          map.put("gender",gender);
          map.put("pass",encryptionAlgo.encryptPass(password));
          map.put("batch",batch);
          map.put("type",memberType);
          map.put("date",requireMethods.getDate());
          if(memberType.equals("alumni"))
               map.put("status","pending");
          else map.put("status","done");

          this.phone = phone;

          if(internetConnection.isOnline())
               backgroundTask.InsertData(getString(R.string.insertOperation),map);
          else dialogClass.errorMessage(getString(R.string.noInternet));
     }


     //insert information background task
     OnResponseTask responseTask = new OnResponseTask() {
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
                                             Thread.sleep(2500);
                                             Map<String,String>maps = new HashMap<>();
                                             maps.put("name",name);
                                             maps.put("email",email);
                                             maps.put("phone",phone);
                                             maps.put("type",memberType);
                                             SharedPreferencesData sharedPreferencesData = new SharedPreferencesData(UserRegistration.this,maps);
                                             sharedPreferencesData.currentUserInfo();//store current user information
                                             requireMethods.closeActivity(UserRegistration.this,JobPortal.class);
                                        }catch (InterruptedException e)
                                        {
                                             e.printStackTrace();
                                        }
                                   }
                              });
                              thread.start();
                         }
                         else dialogClass.errorMessage("Execution failed,please try again latter");
                    }
               });
          }
     };



     //phone number validation with facebook account kit
     private static final int FRAMEWORK_REQUEST_CODE = 1;
     private int nextPermissionsRequestCode = 4000;
     @SuppressLint("UseSparseArrays")
     private final Map<Integer, OnCompleteListener> permissionsListeners = new HashMap<>();

     private interface OnCompleteListener {
          void onComplete();
     }

     @Override
     protected void onActivityResult(
          final int requestCode,
          final int resultCode,
          final Intent data) {
          super.onActivityResult(requestCode, resultCode, data);

          if (requestCode != FRAMEWORK_REQUEST_CODE) {
               return;
          }

          final String toastMessage;
          final AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
          if (loginResult == null || loginResult.wasCancelled())
          {
               toastMessage = "Cancelled";
               Toast.makeText(UserRegistration.this,toastMessage,Toast.LENGTH_SHORT).show();
          }
          else if (loginResult.getError() != null) {
               Toast.makeText(UserRegistration.this,"Error",Toast.LENGTH_SHORT).show();
          } else {
               final AccessToken accessToken = loginResult.getAccessToken();
               if (accessToken != null) {

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                         @Override
                         public void onSuccess(final Account account) {
                              String phoneNumber = account.getPhoneNumber().toString();

                              if(phoneNumber.length()==14)
                                   phoneNumber = phoneNumber.substring(3);
                              registerNewUser(phoneNumber);
                         }

                         @Override
                         public void onError(final AccountKitError error) {
                         }
                    });
               } else {
                    toastMessage = "Unknown response type";
                    Toast.makeText(UserRegistration.this, toastMessage, Toast.LENGTH_SHORT).show();
               }
          }
     }

     public void onLogin(final LoginType loginType)
     {
          final Intent intent = new Intent(this, AccountKitActivity.class);
          final AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType, AccountKitActivity.ResponseType.TOKEN);
          final AccountKitConfiguration configuration = configurationBuilder.build();
          intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
          UserRegistration.OnCompleteListener completeListener = new UserRegistration.OnCompleteListener() {
               @Override
               public void onComplete() {
                    startActivityForResult(intent, FRAMEWORK_REQUEST_CODE);
               }
          };
          if (configuration.isReceiveSMSEnabled() && !canReadSmsWithoutPermission()) {
               final UserRegistration.OnCompleteListener receiveSMSCompleteListener = completeListener;
               completeListener = new UserRegistration.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                         requestPermissions(
                              android.Manifest.permission.RECEIVE_SMS,
                              com.atik_faysal.diualumni.R.string.permissions_receive_sms_title,
                              com.atik_faysal.diualumni.R.string.permissions_receive_sms_message,
                              receiveSMSCompleteListener);
                    }
               };
          }
          if (configuration.isReadPhoneStateEnabled() && !isGooglePlayServicesAvailable()) {
               final UserRegistration.OnCompleteListener readPhoneStateCompleteListener = completeListener;
               completeListener = new UserRegistration.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                         requestPermissions(
                              Manifest.permission.READ_PHONE_STATE,
                              com.atik_faysal.diualumni.R.string.permissions_read_phone_state_title,
                              com.atik_faysal.diualumni.R.string.permissions_read_phone_state_message,
                              readPhoneStateCompleteListener);
                    }
               };
          }
          completeListener.onComplete();
     }

     private boolean isGooglePlayServicesAvailable() {
          final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
          int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(UserRegistration.this);
          return googlePlayServicesAvailable == ConnectionResult.SUCCESS;
     }

     private boolean canReadSmsWithoutPermission() {
          final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
          int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(UserRegistration.this);
          if (googlePlayServicesAvailable == ConnectionResult.SUCCESS) {
               return true;
          }
          return false;
     }

     private void requestPermissions(
          final String permission,
          final int rationaleTitleResourceId,
          final int rationaleMessageResourceId,
          final UserRegistration.OnCompleteListener listener) {
          if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
               if (listener != null) {
                    listener.onComplete();
               }
               return;
          }

          checkRequestPermissions(permission, rationaleTitleResourceId, rationaleMessageResourceId, listener);
     }

     @TargetApi(23)
     private void checkRequestPermissions(final String permission, final int rationaleTitleResourceId, final int rationaleMessageResourceId, final UserRegistration.OnCompleteListener listener) {
          if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
               if (listener != null) {
                    listener.onComplete();
               }
               return;
          }

          final int requestCode = nextPermissionsRequestCode++;
          permissionsListeners.put(requestCode, listener);

          if (shouldShowRequestPermissionRationale(permission)) {
               new AlertDialog.Builder(this).setTitle(rationaleTitleResourceId).setMessage(rationaleMessageResourceId).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                         requestPermissions(new String[] { permission }, requestCode);
                    }
               }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                         permissionsListeners.remove(requestCode);
                    }
               }).setIcon(android.R.drawable.ic_dialog_alert).show();
          } else requestPermissions(new String[]{ permission }, requestCode);

     }

     @TargetApi(23)
     @SuppressWarnings("unused")
     @Override
     public void onRequestPermissionsResult(final int requestCode, final @NonNull String permissions[], final @NonNull int[] grantResults) {
          final UserRegistration.OnCompleteListener permissionsListener = permissionsListeners.remove(requestCode);
          if (permissionsListener != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               permissionsListener.onComplete();
          }
     }
}
