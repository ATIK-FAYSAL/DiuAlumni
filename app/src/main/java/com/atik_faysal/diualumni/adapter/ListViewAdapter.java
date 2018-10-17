package com.atik_faysal.diualumni.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import android.content.Context;
import android.widget.TextView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.models.MessageModel;

/**
 * Created by USER on 3/1/2018.
 */

public class ListViewAdapter extends BaseAdapter
{
    private List<MessageModel>messageModels;
    private Context context;
    private Activity activity;


    private SharedPreferencesData sharedPreferenceData;

    public ListViewAdapter(Context context,List<MessageModel>models)
    {
        this.context = context;
        this.activity = (Activity)context;
        this.messageModels = models;
        sharedPreferenceData = new SharedPreferencesData(context);
    }

    @Override
    public int getCount() {
        return messageModels.size();
    }

    @Override
    public Object getItem(int position) {
        return messageModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("ViewHolder")
        View view = inflater.inflate(R.layout.message_model, parent, false);

        TextView txtSendMsg,txtRcvMsg,txtSendTime,txtRcvTime;
        txtSendMsg = view.findViewById(R.id.msgSend);
        txtRcvMsg = view.findViewById(R.id.msgReceive);
        txtSendTime = view.findViewById(R.id.sendMsgTime);
        txtRcvTime = view.findViewById(R.id.rcvMsgTime);

        if(sharedPreferenceData.getCurrentUserId().equals(messageModels.get(position).getSender()))
        {
            txtSendMsg.setText(messageModels.get(position).getText());
            txtSendTime.setText(messageModels.get(position).getTime());
            txtRcvMsg.setVisibility(View.GONE);
            txtRcvTime.setVisibility(View.GONE);
        }else
        {
            txtRcvMsg.setText(messageModels.get(position).getText());
            txtRcvTime.setText(messageModels.get(position).getTime());
            txtSendMsg.setVisibility(View.GONE);
            txtSendTime.setVisibility(View.GONE);
        }

        return view;
    }

}