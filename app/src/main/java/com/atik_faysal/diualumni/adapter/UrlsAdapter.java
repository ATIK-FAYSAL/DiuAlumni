package com.atik_faysal.diualumni.adapter;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.interfaces.BooleanResponse;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.models.UrlsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UrlsAdapter extends RecyclerView.Adapter<UrlsAdapter.ViewHolder>
{
    private Context context;
    private List<UrlsModel>urlsModels;
    private LayoutInflater inflater;

    public UrlsAdapter(Context context, List<UrlsModel> modelList)
    {
        this.context = context;
        this.urlsModels = modelList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.url_model,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UrlsModel model = urlsModels.get(position);
        holder.setData(model,position);
        holder.setListener();
    }

    @Override
    public int getItemCount() {
        return urlsModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private UrlsModel currentModel;
        private TextView txtUrl;
        private int position;
        private AlertDialog.Builder builder;
        private PostInfoBackgroundTask backgroundTask;
        private CheckInternetConnection internetConnection;
        private ImageView imgRmv;
        private RelativeLayout relativeLayout;
        private DisplayMessage displayMessage;
        private SharedPreferencesData sharedPreferencesData;

        private String updateUrl;

        private ViewHolder(View itemView) {
            super(itemView);
            txtUrl = itemView.findViewById(R.id.txtUrls);
            builder = new AlertDialog.Builder(context);
            imgRmv = itemView.findViewById(R.id.imgRmv);
            internetConnection = new CheckInternetConnection(context);
            relativeLayout = itemView.findViewById(R.id.modelLayout);
            displayMessage = new DisplayMessage(context);
            sharedPreferencesData = new SharedPreferencesData(context);
        }

        private void setData(UrlsModel model,int pos)
        {
            currentModel = model;
            txtUrl.setText(model.getUrl());
            position = pos;

            if(!sharedPreferencesData.getCurrentUserId().equals(currentModel.getStdId()))
            {
                imgRmv.setEnabled(false);
                imgRmv.setImageDrawable(null);
            }
        }

        private void setListener()
        {
            txtUrl.setOnClickListener(UrlsAdapter.ViewHolder.this);
            imgRmv.setOnClickListener(UrlsAdapter.ViewHolder.this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.txtUrls:
                    if(!sharedPreferencesData.getCurrentUserId().equals(currentModel.getStdId()))
                    {
                        try {
                            Uri uri = Uri.parse(currentModel.getUrl());
                            Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
                            context.startActivity(intent);
                        }catch (Exception e)
                        {
                            displayMessage.errorMessage("Something is wrong.Please try again");
                        }
                    }else
                        userUrls();//update url
                    break;
                case R.id.imgRmv:
                    //removeUrl();//remove one url
                    displayMessage.onResultSuccess(booleanResponse);
                    displayMessage.warning("Do you want to remove this url ???");
                    break;
            }
        }

        //user url update and insert new url
        @SuppressLint("SetTextI18n")
        private void userUrls()
        {
            Button bAdd;
            final EditText txtUrl;ImageView imgClear;
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_url,null);
            bAdd = view.findViewById(R.id.bAdd);
            txtUrl = view.findViewById(R.id.txtUrl);
            txtUrl.setText("http://");
            Selection.setSelection(txtUrl.getText(),txtUrl.getText().length());
            txtUrl.setText(currentModel.getUrl());
            imgClear = view.findViewById(R.id.imgClear);
            bAdd.setText("Change");
            builder = new AlertDialog.Builder(context);
            builder.setView(view);
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();

            txtUrl.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!editable.toString().startsWith("http://"))//set default text http
                    {
                        txtUrl.setText("http://");
                        Selection.setSelection(txtUrl.getText(),txtUrl.getText().length());
                    }
                }
            });

            imgClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            bAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateUrl = txtUrl.getText().toString();
                    if(TextUtils.isEmpty(updateUrl)||updateUrl.length()<15)
                        Toast.makeText(context,"Invalid Url",Toast.LENGTH_LONG).show();
                    else
                    {
                        backgroundTask = new PostInfoBackgroundTask(context,onResponseTask);
                        updateUrl(updateUrl);
                        alertDialog.dismiss();
                    }
                }
            });
        }

        //update current url using id
        private void updateUrl(String url)
        {
            Map<String,String> maps = new HashMap<>();
            maps.put("option","updateUrl");
            maps.put("id",currentModel.getuId());
            maps.put("url",url);
            if(internetConnection.isOnline())
                backgroundTask.insertData(context.getResources().getString(R.string.updateOperation),maps);
            else Toast.makeText(context, context.getResources().getString(R.string.noInternet),Toast.LENGTH_LONG).show();
        }

        //delete current url using id
        private void removeUrl()
        {
            backgroundTask = new PostInfoBackgroundTask(context,responseTask);
            Map<String,String> maps = new HashMap<>();
            maps.put("option","deleteUrl");
            maps.put("id",currentModel.getuId());
            if(internetConnection.isOnline())
                backgroundTask.insertData(context.getResources().getString(R.string.deleteOperation),maps);
            else Toast.makeText(context, context.getResources().getString(R.string.noInternet),Toast.LENGTH_LONG).show();
        }

        //getting update url server response
        private OnResponseTask onResponseTask = new OnResponseTask() {
            @Override
            public void onResultSuccess(String value) {
                if(value.equals("success"))
                {
                    urlsModels.get(position).setUrl(updateUrl);
                    notifyItemChanged(position);
                    String msg = "This url is updated successfully";
                    Snackbar snackbar = Snackbar.make(relativeLayout, Html.fromHtml("<font color=\"#00C8F4\">"+msg+"</font>"),
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else
                {
                    String msg = "Sorry url update failed.please try again";
                    Snackbar snackbar = Snackbar.make(relativeLayout, Html.fromHtml("<font color=\"#00C8F4\">"+msg+"</font>"),
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        };

        //getting user confirmation
        private BooleanResponse booleanResponse = new BooleanResponse() {
            @Override
            public void onCompleteResult(boolean flag) {
                if(flag)
                    removeUrl();
            }
        };

        //remove url server response
        private OnResponseTask responseTask = new OnResponseTask() {
            @Override
            public void onResultSuccess(String value) {
                if(value.equals("success"))
                {
                    urlsModels.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, urlsModels.size());
                    String msg = "This url is deleted successfully";
                    Snackbar snackbar = Snackbar.make(relativeLayout, Html.fromHtml("<font color=\"#00C8F4\">"+msg+"</font>"),
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else {
                    String msg = "Sorry url delete failed.please try again";
                    Snackbar snackbar = Snackbar.make(relativeLayout, Html.fromHtml("<font color=\"#00C8F4\">" + msg + "</font>"),
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        };
    }
}
