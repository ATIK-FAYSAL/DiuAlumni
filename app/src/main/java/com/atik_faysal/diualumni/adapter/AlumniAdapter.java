package com.atik_faysal.diualumni.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.models.AlumniModel;
import com.atik_faysal.diualumni.others.SetTabLayout;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlumniAdapter extends RecyclerView.Adapter<AlumniAdapter.ViewHolder>
{
     private List<AlumniModel>modelList;
     private Context context;
     private LayoutInflater inflater;
     private Activity activity;

     public AlumniAdapter(Context context, List<AlumniModel>models)
     {
          this.context = context;
          this.modelList = models;
          inflater = LayoutInflater.from(context);
          activity = (Activity)context;
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view = inflater.inflate(R.layout.alumni_model,parent,false);
          return new ViewHolder(view);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          AlumniModel model = modelList.get(position);
          holder.setData(model,position);
          holder.setListener();
     }

     @Override
     public int getItemCount() {
          return modelList.size();
     }

     class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
     {
          private TextView txtName,txtId,txtPhone,txtEmail,txtBatch,txtGender,txtWork,txtDept;
          private DisplayMessage displayMessage;
          private AlumniModel currentModel;
          private int position;
          private CircleImageView imageView;

          private ViewHolder(View view) {
               super(view);
               txtPhone = view.findViewById(R.id.txtPhone);
               txtName = view.findViewById(R.id.txtName);
               txtEmail = view.findViewById(R.id.txtEmail);
               txtId = view.findViewById(R.id.txtId);
               txtBatch = view.findViewById(R.id.txtBatch);
               txtGender = view.findViewById(R.id.txtGender);
               imageView = view.findViewById(R.id.userImage);
               txtDept = view.findViewById(R.id.txtDepartment);
               txtWork = view.findViewById(R.id.txtWork);
               displayMessage = new DisplayMessage(context);
          }

          @SuppressLint("SetTextI18n")
          private void setData(AlumniModel model, int pos)
          {
               currentModel = model;position = pos;
               txtName.setText(model.getName());
               txtId.setText(model.getStdId());
               txtPhone.setText(model.getPhone());
               txtEmail.setText(model.getEmail());
               txtGender.setText(model.getGender());
               txtBatch.setText(model.getBatch());
               if(model.getCompany().equals("null")||model.getPosition().equals("null"))
                    txtWork.setVisibility(View.GONE);
               else txtWork.setText(model.getPosition()+" at "+model.getCompany());
               txtDept.setText(model.getDepartment());
               if(!model.getImageName().equals("none"))
                    Glide.with(context).
                         load(context.getResources().getString(R.string.address)+model.getImageName()+".png").
                         into(imageView);
          }

          private void setListener()
          {
               txtName.setOnClickListener(AlumniAdapter.ViewHolder.this);
          }

          @Override
          public void onClick(View view) {
               switch (view.getId())
               {
                    case R.id.txtName:
                         Intent intent = new Intent(activity, SetTabLayout.class);
                         intent.putExtra("user",currentModel.getStdId());
                         activity.startActivity(intent);
                         break;
               }
          }
     }

}
