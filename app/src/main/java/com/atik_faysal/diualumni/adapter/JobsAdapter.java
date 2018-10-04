package com.atik_faysal.diualumni.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.interfaces.BooleanResponse;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.ApplyForJob;
import com.atik_faysal.diualumni.models.JobsModel;
import com.atik_faysal.diualumni.others.EditJobPost;
import com.atik_faysal.diualumni.others.SetTabLayout;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.ViewHolder>
{

     private Context context;
     private List<JobsModel>jobsModels;
     private LayoutInflater inflater;
     private Activity activity;
     private String callFrom;

     private CheckInternetConnection internetConnection;
     private PostInfoBackgroundTask backgroundTask;
     private SharedPreferencesData sharedPreferencesData;

     private boolean temp;

     public JobsAdapter(Context context,List<JobsModel>models,String callFrom)
     {
          this.jobsModels = models;
          this.context = context;
          this.inflater = LayoutInflater.from(context);
          this.activity = (Activity)context;
          this.callFrom = callFrom;
          internetConnection = new CheckInternetConnection(context);
          sharedPreferencesData = new SharedPreferencesData(context);
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
          private ImageView imgSelect,imgValid,imgOpt;
          private int position;
          private JobsModel currentModel;
          private RelativeLayout relativeLayout;
          private CardView cardView;
          private PopupMenu popupMenu;
          private DisplayMessage displayMessage;
          private Map<String,String>infoMap;

          //initialize component
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
               imgValid = view.findViewById(R.id.imgValid);
               cardView = view.findViewById(R.id.cardView);
               imgOpt = view.findViewById(R.id.imgOpt);
               infoMap = new HashMap<>();
               displayMessage = new DisplayMessage(context);
          }

          //set data for each object
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

               infoMap.put("id",currentModel.getJobId());
               infoMap.put("email",currentModel.getEmail());
               infoMap.put("phone",currentModel.getPhone());
               infoMap.put("type",currentModel.getType());
               infoMap.put("title",currentModel.getJobTitle());
               infoMap.put("des",currentModel.getJobDes());
               infoMap.put("edu",currentModel.getEducation());
               infoMap.put("req",currentModel.getRequirement());
               infoMap.put("expe",currentModel.getExperience());
               infoMap.put("category",currentModel.getCategory());
               infoMap.put("company",currentModel.getCompany());
               infoMap.put("deadLine",currentModel.getDeadLine());
               infoMap.put("salary",currentModel.getSalary());
               infoMap.put("city",currentModel.getCity());
               infoMap.put("vacancy",currentModel.getVacancy());
               infoMap.put("comUrl",currentModel.getComUrl());
               infoMap.put("comAddress",currentModel.getComAddress());
               infoMap.put("date",currentModel.getDate());
               infoMap.put("name",currentModel.getUserName());

               if(callFrom.equals("homeProfile"))
               {
                    imgSelect.setEnabled(false);
                    imgSelect.setImageDrawable(null);
               }
               else
               {
                    if(model.isFlag())
                         imgSelect.setImageResource(R.drawable.icon_select2);
               }

               if(!model.getmType().equals("alumni"))
                    imgValid.setVisibility(View.GONE);
               else
                    imgValid.setVisibility(View.VISIBLE);

               if(!model.getStdId().equals(sharedPreferencesData.getCurrentUserId()))
                    imgOpt.setVisibility(View.GONE);
               else imgOpt.setVisibility(View.VISIBLE);
          }

          //on click listener
          private void setListener()
          {
               imgSelect.setOnClickListener(JobsAdapter.ViewHolder.this);
               relativeLayout.setOnClickListener(JobsAdapter.ViewHolder.this);
               imgOpt.setOnClickListener(JobsAdapter.ViewHolder.this);
               txtName.setOnClickListener(JobsAdapter.ViewHolder.this);
          }

          //on button click
          @Override
          public void onClick(View view) {
               switch (view.getId())
               {
                    case R.id.imgSelect:
                         if(sharedPreferencesData.getIsUserLogin())
                         {
                              if(currentModel.isFlag())
                              {
                                   backgroundTask = new PostInfoBackgroundTask(context,onResponseTask);
                                   removeFavJob(currentModel.getJobId());//remove favourite job
                                   currentModel.setFlag(false);
                                   imgSelect.setImageResource(R.drawable.icon_select1);
                                   temp = false;//use for if failed to execution
                              }else
                              {
                                   backgroundTask = new PostInfoBackgroundTask(context,onResponseTask);
                                   storeFavJob(currentModel.getJobId());//store favourite job
                                   currentModel.setFlag(true);
                                   imgSelect.setImageResource(R.drawable.icon_select2);
                                   temp = true;//use for if failed to execution
                              }
                         }else Toast.makeText(context,"Please sign in first and retry",Toast.LENGTH_LONG).show();
                         break;
                    case R.id.rLayout:
                         Intent intent = new Intent(context,ApplyForJob.class);
                         intent.putExtra("maps", (Serializable) infoMap);
                         context.startActivity(intent);
                         break;
                    case R.id.imgOpt:
                         showPopup();
                         break;
                    case R.id.txtUserName:
                         Toast.makeText(context,"click",Toast.LENGTH_LONG).show();
                         Intent page = new Intent(context, SetTabLayout.class);
                         page.putExtra("user",currentModel.getStdId());
                         activity.startActivity(page);
                         break;
               }
          }

          //favourite job insertion
          private void storeFavJob(String jobId)
          {
               Map<String,String>maps = new HashMap<>();
               maps.put("option","fabJob");
               maps.put("stdId",sharedPreferencesData.getCurrentUserId());
               maps.put("jobId",jobId);

               if(internetConnection.isOnline())
                    backgroundTask.InsertData(activity.getString(R.string.insertOperation),maps);
               else displayMessage.errorMessage(activity.getString(R.string.noInternet));
          }

          //delete favourite job
          private void removeFavJob(String jobId)
          {
               Map<String,String>maps = new HashMap<>();
               maps.put("option","removeFavJob");
               maps.put("stdId",sharedPreferencesData.getCurrentUserId());
               maps.put("jobId",jobId);

               if(internetConnection.isOnline())
                    backgroundTask.InsertData(activity.getString(R.string.deleteOperation),maps);
               else displayMessage.errorMessage(activity.getString(R.string.noInternet));
          }

          //show a popup menu for delete and edit
          private void showPopup()
          {
               popupMenu = new PopupMenu(context,imgOpt);
               popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                         switch (menuItem.getItemId())
                         {
                              case R.id.mEdit:
                                   Intent intent = new Intent(context,EditJobPost.class);
                                   intent.putExtra("maps", (Serializable) infoMap);
                                   context.startActivity(intent);
                                   break;
                              case R.id.mDelete:
                                   displayMessage.onResultSuccess(booleanResponse);
                                   displayMessage.warning("Do you want to remove this job?");
                                   break;

                         }
                         return true;
                    }
               });
               popupMenu.show();
          }

          //remove one job from server
          private void removeJob()
          {
               backgroundTask = new PostInfoBackgroundTask(context,responseTask);
               Map<String,String>maps = new HashMap<>();
               maps.put("option","jobRmv");
               maps.put("jobId",currentModel.getJobId());
               if(internetConnection.isOnline())
                    backgroundTask.InsertData(context.getResources().getString(R.string.deleteOperation),maps);
               else Toast.makeText(context,context.getResources().getString(R.string.noInternet),Toast.LENGTH_LONG).show();
          }

          //favourite job insertion result
          OnResponseTask onResponseTask = new OnResponseTask() {
               @Override
               public void onResultSuccess(final String value) {
                    activity.runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                              String msg;
                              switch (value) {
                                   case "success"://if execution success
                                        if(temp)
                                             msg = "Job is added to favourite list";
                                        else
                                             msg = "Job is removed to favourite list";

                                        Snackbar snackbar = Snackbar.make(cardView, Html.fromHtml("<font color=\"#00C8F4\">"+msg+"</font>"),
                                             Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        break;
                                   case "limit exceed"://if favourite job more than 10 jobs
                                             imgSelect.setImageResource(R.drawable.icon_select1);
                                             currentModel.setFlag(false);//set save job false
                                             displayMessage.errorMessage(activity.getString(R.string.limitExceed));
                                        break;
                                   default:
                                        if(temp)
                                        {
                                             imgSelect.setImageResource(R.drawable.icon_select1);
                                             currentModel.setFlag(false);//set save job false
                                        }
                                        else
                                        {
                                             imgSelect.setImageResource(R.drawable.icon_select2);
                                             currentModel.setFlag(true);//set save job true
                                        }
                                        displayMessage.errorMessage(activity.getString(R.string.executionFailed));
                                        break;
                              }
                         }
                    });
               }
          };

          //remove job id server response
          private OnResponseTask responseTask = new OnResponseTask() {
               @Override
               public void onResultSuccess(String value) {
                    if(value.equals("success"))
                    {
                         jobsModels.remove(position);//remove from list
                         notifyItemRemoved(position);
                         notifyItemRangeChanged(position, jobsModels.size());
                    }else displayMessage.errorMessage(context.getResources().getString(R.string.executionFailed));
               }
          };

          //get confirmation for remove this job true or false
          private BooleanResponse booleanResponse = new BooleanResponse() {
               @Override
               public void onCompleteResult(boolean flag) {
                    if(flag)
                         removeJob();
               }
          };
     }

}
