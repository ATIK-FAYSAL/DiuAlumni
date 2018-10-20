package com.atik_faysal.diualumni.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.messages.PersonMessage;
import com.atik_faysal.diualumni.models.ChatModel;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.ViewHolder>
{

    private Context context;
    private List<ChatModel> messageModels;
    private LayoutInflater inflater;

    public MessageAdapter(Context context,List<ChatModel>models)
    {
        this.context = context;
        this.messageModels = models;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.chat_list_model,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatModel model = messageModels.get(position);
        holder.setData(model,position);
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView chatName,chatMsg,unseenMsg;
        private CircleImageView imageUrl;
        private ChatModel currentModel;
        private int position;
        private SharedPreferencesData sharedPreferencesData;

        private ViewHolder(View view) {
            super(view);
            chatName = view.findViewById(R.id.chatName);
            chatMsg = view.findViewById(R.id.chatMsg);
            unseenMsg = view.findViewById(R.id.unseenMsg);
            imageUrl = view.findViewById(R.id.imgUserImage);

            sharedPreferencesData = new SharedPreferencesData(context);
        }


        private void setData(ChatModel model,int pos)
        {
            currentModel = model;
            position = pos;

            chatName.setText(currentModel.getPersonName());
            chatMsg.setText(currentModel.getMessage());
            if(!currentModel.getCounter().equals("0"))
            {
                unseenMsg.setText(currentModel.getCounter());
                chatMsg.setTextColor(context.getResources().getColor(R.color.black));
            }
            else{
                unseenMsg.setVisibility(View.GONE);
                chatMsg.setTextColor(context.getResources().getColor(R.color.iconColor));
            }


            if(!currentModel.getImageUrl().equals("null"))
                Glide.with(context).
                        load(currentModel.getImageUrl()).
                        into(imageUrl);


        }

        private void setOnClickListener()
        {
            chatName.setOnClickListener(MessageAdapter.ViewHolder.this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.chatName:
                    Intent intent = new Intent(context,PersonMessage.class);
                    intent.putExtra("receiverId",currentModel.getPersonId());
                    intent.putExtra("receiverName",currentModel.getPersonName());
                    context.startActivity(intent);
                    break;
            }
        }
    }

}
