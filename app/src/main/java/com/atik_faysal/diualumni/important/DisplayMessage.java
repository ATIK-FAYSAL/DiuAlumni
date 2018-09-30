package com.atik_faysal.diualumni.important;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.interfaces.BooleanResponse;
import com.atik_faysal.diualumni.main.JobPortal;
import com.atik_faysal.diualumni.main.UserRegistration;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

import java.util.Objects;

public class DisplayMessage extends AlertDialog
{
     private Context context;
     private RequireMethods methods;
     private BooleanResponse booleanResponse;

     private Activity activity;


     //constructor
     public DisplayMessage(Context context)
     {
          super(context);
          this.context = context;
          activity = (Activity) context;
          methods = new RequireMethods(context);
     }


     //execution failed
     public void errorMessage(String message)
     {
          Objects.requireNonNull(getWindow()).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
          iOSDialogBuilder builder = new iOSDialogBuilder(context);

          builder.setTitle("Attention")
               .setSubtitle(message)
               .setBoldPositiveLabel(true)
               .setCancelable(false)
               .setPositiveListener("ok",new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                         dialog.dismiss();
                    }
               }).build().show();
     }

     public void progressDialog(String message1, String message2, final Class<?>className)
     {
          final ProgressDialog ringProgressDialog = ProgressDialog.show(getContext(), message1,message2, true);
          ringProgressDialog.setCancelable(true);
          new Thread(new Runnable() {
               @Override
               public void run() {
                    try {
                         Thread.sleep(2500);
                    } catch (Exception e) {
                    }
                    ringProgressDialog.dismiss();
               }
          }).start();
          ringProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
               @Override
               public void onDismiss(DialogInterface dialog) {
                    Toast.makeText(context,"Information's update successfully.",Toast.LENGTH_LONG).show();
                    methods.closeActivity(activity,className);
               }
          });
     }

     public void onResultSuccess(BooleanResponse response)
     {
          this.booleanResponse = response;
     }

     //show alert box with confirmation
     public void warning(String text)
     {
          Objects.requireNonNull(getWindow()).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
          iOSDialogBuilder builder = new iOSDialogBuilder(context);

          builder.setTitle("Warning!!!")
               .setSubtitle(text)
               .setBoldPositiveLabel(true)
               .setCancelable(false)
               .setPositiveListener("Yes",new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                         booleanResponse.onCompleteResult(true);
                         dialog.dismiss();

                    }
               })
               .setNegativeListener("No", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                         booleanResponse.onCompleteResult(false);
                         dialog.dismiss();
                    }
               })
               .build().show();
     }

     public void congratesMessage(String text)
     {
          AlertDialog.Builder builder;
          final AlertDialog alertDialog;
          @SuppressLint("InflateParams")
          View view = LayoutInflater.from(context).inflate(R.layout.congrates_layout,null);
          Button bGo = view.findViewById(R.id.bGo);
          TextView textView = view.findViewById(R.id.text1);
          textView.setText(text);
          builder = new AlertDialog.Builder(context);
          builder.setView(view);
          builder.setCancelable(false);
          alertDialog = builder.create();
          Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          alertDialog.show();

          bGo.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    methods.closeActivity((Activity) context,JobPortal.class);
                    alertDialog.dismiss();
               }
          });
     }

}
