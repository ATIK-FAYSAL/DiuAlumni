package com.atik_faysal.diualumni.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.models.JobsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.ViewHolder>
{

     private Context context;
     private List<JobsModel>jobsModels;
     private LayoutInflater inflater;
     private Activity activity;

     private CheckInternetConnection internetConnection;
     private PostInfoBackgroundTask backgroundTask;
     private DisplayMessage displayMessage;
     private SharedPreferencesData sharedPreferencesData;
     public JobsAdapter(Context context,List<JobsModel>models)
     {
          this.jobsModels = models;
          this.context = context;
          this.inflater = LayoutInflater.from(context);
          this.activity = (Activity)context;
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
     {
          View view = inflater.inflate(R.layout.job_models,parent,false);
          return new ViewHolder(view);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          JobsModel model = jobsModels.get(position);
          holder.setData(model,position);
          holder.setListener();
     }

     @Override
     public int getItemCount() {
          return jobsModels.size();
     }


     public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
     {

          private TextView txtTitle,txtDate,txtDeadline,txtEdu,txtCompany,txtName,txtExp,txtSalary;
          private ImageView imgSelect;
          private int position;
          private JobsModel currentModel;
          private RelativeLayout relativeLayout;
          private CardView cardView;

          private ViewHolder(View view) {
               super(view);
               txtName = view.findViewById(R.id.txtUserName);
               txtExp = view.findViewById(R.id.txtExp);
               txtTitle = view.findViewById(R.id.txtTitle);
               txtEdu = view.findViewById(R.id.txtEdu);
               txtCompany = view.findViewById(R.id.txtCompany);
               txtDate = view.findViewById(R.id.txtDate);
               txtDeadline = view.findViewById(R.id.txtDeadLine);
               imgSelect = view.findViewById(R.id.imgSelect);
               relativeLayout = view.findViewById(R.id.rLayout);
               txtSalary = view.findViewById(R.id.txtSalary);
               cardView = view.findViewById(R.id.cardView);

               internetConnection = new CheckInternetConnection(context);
               backgroundTask = new PostInfoBackgroundTask(context,onResponseTask);
               displayMessage = new DisplayMessage(context);
               sharedPreferencesData = new SharedPreferencesData(context);
          }

          private void setData(JobsModel model,int pos)
          {
               position = pos;
               currentModel = model;
               txtTitle.setText(model.getJobTitle());
               txtName.setText(model.getUserName());
               txtDeadline.setText(model.getDeadLine());
               txtCompany.setText(model.getCompany());
               txtDate.setText(model.getDate());
               txtEdu.setText(model.getEducation());
               txtExp.setText(model.getExperience());
               txtSalary.setText(model.getSalary());
               if(model.isFlag())
                    imgSelect.setImageResource(R.drawable.icon_select2);
          }

          private void setListener()
          {
               imgSelect.setOnClickListener(this);
               relativeLayout.setOnClickListener(this);
          }

          @Override
          public void onClick(View view) {
               switch (view.getId())
               {
                    case R.id.imgSelect:
                         storeFavJob(currentModel.getJobId());
                         break;
                    case R.id.rLayout:
                         Toast.makeText(context,"job details",Toast.LENGTH_SHORT).show();
                         break;
               }
          }

          //favourite job insertion
          private void storeFavJob(String jobId)
          {
               Map<String,String>maps = new HashMap<>();
               maps.put("option","fabJob");
               maps.put("stdId",sharedPreferencesData.getStudentId());
               maps.put("jobId",jobId);

               if(internetConnection.isOnline())
                    backgroundTask.InsertData(activity.getString(R.string.insertUserInfo),maps);
               else displayMessage.errorMessage(activity.getString(R.string.noInternet));
          }

          //favourite job insertion result
          OnResponseTask onResponseTask = new OnResponseTask() {
               @Override
               public void onResultSuccess(final String value) {
                    activity.runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                              Log.d("error",value);
                              if(value.equals("success"))
                              {
                                   Snackbar snackbar = Snackbar.make(cardView,"Job is added to favourite list",Snackbar.LENGTH_SHORT);
                                   snackbar.show();
                              }else
                                   displayMessage.errorMessage(activity.getString(R.string.executionFailed));
                         }
                    });
               }
          };
     }

}
