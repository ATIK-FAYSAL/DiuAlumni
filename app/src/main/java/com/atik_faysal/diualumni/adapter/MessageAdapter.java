package com.atik_faysal.diualumni.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.models.MessageModel;

import java.util.List;

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.ViewHolder>
{

    private Context context;
    private List<MessageModel> messageModels;
    private LayoutInflater inflater;


    public MessageAdapter(Context context,List<MessageModel>models)
    {
        this.context = context;
        this.messageModels = models;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.message_model,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel model = messageModels.get(position);
        holder.setData(model,position);
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtSendMsg,txtRcvMsg,txtSendTime,txtRcvTime;
        private MessageModel currentModel;
        private int position;
        private SharedPreferencesData sharedPreferencesData;

        private ViewHolder(View view) {
            super(view);
            txtSendMsg = view.findViewById(R.id.msgSend);
            txtRcvMsg = view.findViewById(R.id.msgReceive);
            txtSendTime = view.findViewById(R.id.sendMsgTime);
            txtRcvTime = view.findViewById(R.id.rcvMsgTime);

            sharedPreferencesData = new SharedPreferencesData(context);
        }


        private void setData(MessageModel model,int pos)
        {
            currentModel = model;
            position = pos;


            if(sharedPreferencesData.getCurrentUserId().equals(currentModel.getSender()))
            {
                txtSendMsg.setText(currentModel.getText());
                txtSendTime.setText(currentModel.getTime());
                txtRcvMsg.setVisibility(View.GONE);
                txtRcvTime.setVisibility(View.GONE);
            }else
            {
                txtRcvMsg.setText(currentModel.getText());
                txtRcvTime.setText(currentModel.getTime());
                txtSendMsg.setVisibility(View.GONE);
                txtSendTime.setVisibility(View.GONE);
            }

        }

        private void setOnClickListener()
        {

        }

        @Override
        public void onClick(View view) {

        }
    }

}
