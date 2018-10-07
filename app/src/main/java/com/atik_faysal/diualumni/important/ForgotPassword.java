package com.atik_faysal.diualumni.important;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.interfaces.Methods;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ForgotPassword extends AppCompatActivity implements Methods
{
     private EditText txtStdId,txtEmail;
     private String stdId,email;
     private EditText[] editTexts = new EditText[6];
     private int[] editTextId = new int[]{R.id.txtCode1,R.id.txtCode2,R.id.txtCode3,R.id.txtCode4,R.id.txtCode5,R.id.txtCode6};
     private TextView txtTime;

     private DisplayMessage displayMessage;
     private CheckInternetConnection internetConnection;
     private ProgressDialog progressDialog;
     private DesEncryptionAlgo desEncryptionAlgo;
     private AlertDialog alertDialog;

     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.forgot_password);
          initComponent();
          setErrorSign();//if user input is not valid it will show error
          setToolbar();
     }

     //initialize component
     @SuppressLint("InflateParams")
     @Override
     public void initComponent() {
          txtStdId = findViewById(R.id.txtStdId);
          txtEmail = findViewById(R.id.txtEmail);
          Button bFind = findViewById(R.id.bFind);

          internetConnection = new CheckInternetConnection(this);
          displayMessage = new DisplayMessage(this);
          progressDialog = new ProgressDialog(this);
          desEncryptionAlgo = new DesEncryptionAlgo(this);

          bFind.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   connectToServer();

               }
          });
     }

     //pass value to server
     private void connectToServer()
     {
          Map<String,String>map = new HashMap<>();
          if(dataValidator())
          {
               map.put("option","find");
               map.put("stdId",stdId);
               map.put("email",email);
               if(internetConnection.isOnline())
               {
                    PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(ForgotPassword.this,responseTask);
                    backgroundTask.InsertData(getResources().getString(R.string.forgotPass),map);
                    progressDialog.setTitle("Please wait");
                    progressDialog.setMessage("Finding your account");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
               }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
          }
     }

     //if user input invalid data it will show error
     private void setErrorSign()
     {
          final Drawable iconValid = getResources().getDrawable(R.drawable.icon_check);//valid icon
          iconValid.setBounds(0,0,iconValid.getIntrinsicWidth(),iconValid.getIntrinsicHeight());
          final Drawable iconInvalid = getResources().getDrawable(R.drawable.icon_wrong);//invalid icon
          iconInvalid.setBounds(0,0,iconInvalid.getIntrinsicWidth(),iconInvalid.getIntrinsicHeight());

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

     //set toolbar in top
     @Override
     public void setToolbar() {
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
     public void processJsonData(String jsonData) {

     }
//compile 'com.firebase:firebase-client-android:2.5.0'
     private void onTextChange()
     {
          editTexts[0].addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    editTexts[1].requestFocus();
               }
          });

          editTexts[1].addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    editTexts[2].requestFocus();
               }
          });

          editTexts[2].addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    editTexts[3].requestFocus();
               }
          });

          editTexts[3].addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    editTexts[4].requestFocus();
               }
          });

          editTexts[4].addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                    editTexts[5].requestFocus();
               }
          });
     }

     //input verification code
     @SuppressLint("InflateParams")
     private void verifyCode()
     {
          AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
          View view = LayoutInflater.from(this).inflate(R.layout.verify_code, null);
          builder.setView(view);//set view in builder
          builder.setCancelable(true);//cancelable true,
          for(int i=0;i<6;i++)//initialize editText
               editTexts[i] = view.findViewById(editTextId[i]);//create object all editText

          txtTime = view.findViewById(R.id.txtTime);//initialize time textview
          alertDialog = builder.create();//create alertdialog
          Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          //set countDownTimer
          final CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
               @SuppressLint({"DefaultLocale", "SetTextI18n"})
               @Override
               public void onTick(long millisUntilFinished) {
                    txtTime.setText("" + String.format("0%d:%d ",
                         TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                         TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                              TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
               }
               @Override
               public void onFinish() {
                    alertDialog.dismiss();
               }
          };

          countDownTimer.start();//count down start
          onTextChange();//set textChangeListener
          alertDialog.show();//showing dialog

          Button bVerify;
          TextView txtResend = view.findViewById(R.id.txtResend);
          bVerify = view.findViewById(R.id.bVerify);

          alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {//when alert dialog is destroy timer is cancel
               @Override
               public void onDismiss(DialogInterface dialogInterface) {
                    countDownTimer.cancel();//cancel timer
               }
          });

          final Map<String,String>codes = new HashMap<>();
          bVerify.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    String code1 = editTexts[0].getText().toString();
                    String code2 = editTexts[1].getText().toString();
                    String code3 = editTexts[2].getText().toString();
                    String code4 = editTexts[3].getText().toString();
                    String code5 = editTexts[4].getText().toString();
                    String code6 = editTexts[5].getText().toString();
                    if(!code1.isEmpty()&&!code2.isEmpty()&&!code3.isEmpty()&&!code4.isEmpty()&&!code5.isEmpty()&&!code6.isEmpty())
                    {
                         codes.put("option","matchCode");
                         codes.put("stdId",stdId);
                         codes.put("code",code1+code2+code3+code4+code5+code6);
                         if(internetConnection.isOnline())
                         {
                              PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(ForgotPassword.this,onResponseTask);
                              backgroundTask.InsertData(getResources().getString(R.string.forgotPass),codes);
                         }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
                    }else {
                         editTexts[0].setText("");
                         editTexts[1].setText("");
                         editTexts[2].setText("");
                         editTexts[3].setText("");
                         editTexts[4].setText("");
                         editTexts[5].setText("");
                         editTexts[0].requestFocus();
                         Toast.makeText(ForgotPassword.this,"Invalid code1",Toast.LENGTH_LONG).show();
                    }
               }
          });

          txtResend.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    alertDialog.dismiss();
                    connectToServer();
               }
          });
     }

     //change password
     public void changePassword()
     {
          final EditText txtOldPass,txtNewPass,txtConPass;
          Button bChange;
          final CheckBox cRemember;
          final AlertDialog alertDialog;
          AlertDialog.Builder builder;
          @SuppressLint("InflateParams")
          View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_pass,null);
          builder = new AlertDialog.Builder(this);
          txtOldPass = view.findViewById(R.id.txtOldPass);
          txtNewPass = view.findViewById(R.id.txtNewPass);
          txtConPass = view.findViewById(R.id.txtConPass);
          bChange = view.findViewById(R.id.bChange);
          cRemember = view.findViewById(R.id.cRememberPass);

          txtOldPass.setVisibility(View.GONE);//invisible txtOld editTextView
          cRemember.setVisibility(View.GONE);//invisible checkbox
          builder.setView(view);//set View in builder
          builder.setCancelable(true);//cancelable true
          alertDialog = builder.create();//create alertdialog
          Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//use transparent model
          alertDialog.show();//showing alertdialog

          bChange.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {//change password
                    Map<String,String> map = new HashMap<>();
                    String newPass = desEncryptionAlgo.encryptPass(txtNewPass.getText().toString());//decrypt new password by des method
                    String conPass = desEncryptionAlgo.encryptPass(txtConPass.getText().toString());//decrypt confirm password by des method

                    if((newPass.length()<8||newPass.length()>20))
                    {
                         Toast.makeText(ForgotPassword.this,"Invalid password.Password must be in 8-20 characters",Toast.LENGTH_LONG).show();
                         return;
                    }

                    if(newPass.equals(conPass))
                    {
                         map.put("option","changePass");
                         map.put("stdId",stdId);
                         map.put("newPass",newPass);
                         if(internetConnection.isOnline())
                         {
                              PostInfoBackgroundTask backgroundTask = new PostInfoBackgroundTask(ForgotPassword.this,getResponseTask);
                              backgroundTask.InsertData(getResources().getString(R.string.forgotPass),map);
                              alertDialog.dismiss();
                         }else displayMessage.errorMessage(getResources().getString(R.string.noInternet));
                    }else Toast.makeText(ForgotPassword.this,"Password does not matched",Toast.LENGTH_LONG).show();
               }
          });
     }

     //check student id and email
     private boolean dataValidator()
     {
          stdId = txtStdId.getText().toString();
          email = txtEmail.getText().toString();
          if(stdId.equals("")||email.equals(""))
          {
               displayMessage.errorMessage(getResources().getString(R.string.infoErr));
               return false;
          }

          if (stdId.length() < 8 || stdId.length() > 15)
               return false;
          for (int i = 0; i < stdId.length(); i++) {
               if ((stdId.charAt(i) >= '0' && stdId.charAt(i) <= '9') || stdId.charAt(i) == '-') {

               } else
                    return false;
          }
          return email.length() >= 15 &&
               email.length() <= 50 &&
               email.contains("@") &&
               email.contains(".");
     }

     //user information is valid or not server response.check if this user is exist
     private OnResponseTask responseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               if(value.equals("not exist"))
                    displayMessage.errorMessage("We can not recognized you.Please try again.");
               else
               {
                    if(progressDialog.isShowing())
                         progressDialog.dismiss();
                    verifyCode();
               }

          }
     };

     //server response for matching verification code
     private OnResponseTask onResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               if(value.equals("success"))
               {
                    alertDialog.dismiss();
                    changePassword();
               }
               else Toast.makeText(ForgotPassword.this,"Invalid code",Toast.LENGTH_LONG).show();
          }
     };

     private OnResponseTask getResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(String value) {
               if(value.equals("success"))
               {
                    Toast.makeText(ForgotPassword.this,"Your password has been updated",Toast.LENGTH_LONG).show();
                    finish();
               }else
                    displayMessage.errorMessage(getResources().getString(R.string.executionFailed));
          }
     };
}
