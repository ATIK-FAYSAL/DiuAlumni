package com.atik_faysal.diualumni.important;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.JobsAdapter;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.main.JobPortal;
import com.atik_faysal.diualumni.models.JobsModel;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeProfile extends Fragment
{
     private View view;
     private TextView txtName,txtNoResult,txtStdId;
     private CircleImageView imgUser;
     private RecyclerView jobLists;
     private LinearLayoutManager layoutManager;
     private ProgressBar progressBar;
     private RelativeLayout emptyView;


     private CheckInternetConnection internetConnection;
     private DisplayMessage displayMessage;
     private PostInfoBackgroundTask backgroundTask;
     private JobPortal jobPortal;

     private static String USER;

     @Nullable
     @Override
     public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          view = inflater.inflate(R.layout.profile_home, container, false);
          initComponent();
          return view;
     }

     //initialize component
     private void initComponent()
     {
          txtName = view.findViewById(R.id.txtName);
          imgUser = view.findViewById(R.id.imgUser);
          jobLists = view.findViewById(R.id.jobList);
          layoutManager = new LinearLayoutManager(getContext());
          txtNoResult = view.findViewById(R.id.txtNoResult);
          emptyView = view.findViewById(R.id.emptyView);
          progressBar = view.findViewById(R.id.progressBar);
          txtNoResult.setVisibility(View.INVISIBLE);
          txtStdId = view.findViewById(R.id.txtStdId);
          internetConnection = new CheckInternetConnection(getContext());
          displayMessage = new DisplayMessage(getContext());
          backgroundTask = new PostInfoBackgroundTask(getContext(),onResponseTask);
          jobPortal = new JobPortal();

          USER = Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getExtras()).getString("user");

          //calling method
          getJobInformation();
     }

     //get user posted job information
     private void getJobInformation()
     {
          Map<String,String>maps = new HashMap<>();
          maps.put("option","myPost");
          maps.put("stdId",USER);
          if(internetConnection.isOnline())
               backgroundTask.insertData(getString(R.string.readInfo),maps);

          PostInfoBackgroundTask backgroundTask1 = new PostInfoBackgroundTask(getContext(),responseTask);
          Map<String,String>imgMap = new HashMap<>();
          imgMap.put("option","imgName");
          imgMap.put("stdId",USER);
          backgroundTask1.insertData(getResources().getString(R.string.readInfo),imgMap);
     }

     //view all job information in UI
     private void viewJobInfo(String json)
     {
          final List<JobsModel> jobsModels = jobPortal.processJson(json);

          //add progress bar ...
          final Timer timer = new Timer();
          final Handler handler = new Handler();
          final  Runnable runnable = new Runnable() {
               @Override
               public void run() {
                    try {
                         if(jobsModels.isEmpty()||jobsModels.get(0).getJobTitle().equals("null"))//if no jobs found
                         {
                              emptyView.setVisibility(View.VISIBLE);//empty view visible
                              txtNoResult.setVisibility(View.VISIBLE);//no result text visible
                              jobLists.setVisibility(View.INVISIBLE);//list invisible
                         }else//if jobs found
                         {
                              emptyView.setVisibility(View.INVISIBLE);//empty view invisible
                              jobLists.setVisibility(View.VISIBLE);//no result text invisible
                              JobsAdapter adapter = new JobsAdapter(getContext(), jobsModels,"homeProfile");//create adapter
                              jobLists.setAdapter(adapter);//set adapter in recycler view
                              layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                              jobLists.setLayoutManager(layoutManager);
                              jobLists.setItemAnimator(new DefaultItemAnimator());
                         }
                         txtName.setText(jobsModels.get(0).getUserName());//set user name,get from list
                         txtStdId.setText(jobsModels.get(0).getStdId());//set user id,get from list
                         progressBar.setVisibility(View.GONE);
                         timer.cancel();
                    }catch (NullPointerException ex)
                    {
                         txtNoResult.setVisibility(View.VISIBLE);
                         txtNoResult.setText(getResources().getString(R.string.noResult));
                         progressBar.setVisibility(View.GONE);
                    }
               }
          };
          timer.schedule(new TimerTask() {
               @Override
               public void run() {
                    handler.post(runnable);
               }
          },getResources().getInteger(R.integer.progTime));
     }

     //server response result
     OnResponseTask onResponseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
              Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                        viewJobInfo(value);
                   }
              });
          }
     };


     //get user image name response
     OnResponseTask responseTask = new OnResponseTask() {
          @Override
          public void onResultSuccess(final String value) {
               Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         if(!value.equals("none"))
                              Glide.with(HomeProfile.this).
                                   load(getResources().getString(R.string.address)+value+".png").
                                   into(imgUser);

                    }
               });
          }
     };

}
