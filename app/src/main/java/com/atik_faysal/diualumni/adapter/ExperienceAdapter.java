package com.atik_faysal.diualumni.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.PostInfoBackgroundTask;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.CheckInternetConnection;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.important.RequireMethods;
import com.atik_faysal.diualumni.interfaces.BooleanResponse;
import com.atik_faysal.diualumni.interfaces.OnResponseTask;
import com.atik_faysal.diualumni.models.ExperienceModel;
import com.atik_faysal.diualumni.others.AdditionalInfo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ViewHolder>
{
     private Context context;
     private List<ExperienceModel>modelList;
     private LayoutInflater inflater;
     private CheckInternetConnection internetConnection;
     private RequireMethods requireMethods;
     private DisplayMessage displayMessage;
     private SharedPreferencesData sharedPreferencesData;

     public ExperienceAdapter(Context context, List<ExperienceModel>models)
     {
          this.context = context;
          this.modelList = models;
          inflater = LayoutInflater.from(context);
          displayMessage = new DisplayMessage(context);
          internetConnection = new CheckInternetConnection(context);
          requireMethods = new RequireMethods(context);
          sharedPreferencesData = new SharedPreferencesData(context);
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
          ExperienceModel model = modelList.get(position);
          holder.setDate(model,position);
          holder.setClickListener();
     }

     @Override
     public int getItemCount() {
          return modelList.size();
     }

     class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,DatePickerDialog.OnDateSetListener{

          private EditText txtCompany,txtPosition,txtDescription;
          private Spinner spinner;
          private TextView txtFrom,txtTo,txtTitle;
          private CheckBox checkBox;
          private ImageView imgCancel;
          private Button bSave;
          private ExperienceModel currentModel;
          private int position;
          private ImageView imgRmv;
          private int day,month,year;
          private boolean flag;
          private String city;
          private PostInfoBackgroundTask backgroundTask;
          private View view;
          private RelativeLayout relativeLayout;
          private String company,txtPos,from,to,description;

          @SuppressLint({"SetTextI18n", "InflateParams"})
          private ViewHolder(View itemView) {
               super(itemView);
               imgRmv = itemView.findViewById(R.id.imgRmv);
               txtTitle = itemView.findViewById(R.id.txtUrls);
               txtTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
               relativeLayout = itemView.findViewById(R.id.modelLayout);
               Calendar calendar = Calendar.getInstance();
               day = calendar.get(Calendar.DAY_OF_MONTH);
               month = calendar.get(Calendar.MONTH);
               year = calendar.get(Calendar.YEAR);
          }

          @SuppressLint("SetTextI18n")
          private void setDate(ExperienceModel model, int pos)
          {
               currentModel = model;
               position = pos;
               txtTitle.setText(model.getPosition()+" at "+model.getCompany());
               if(!model.getStdId().equals(sharedPreferencesData.getCurrentUserId()))
                    imgRmv.setVisibility(View.GONE);
          }

          private void setClickListener()
          {
               imgRmv.setOnClickListener(ExperienceAdapter.ViewHolder.this);
               txtTitle.setOnClickListener(ExperienceAdapter.ViewHolder.this);
          }

          @Override
          public void onClick(View view) {
               switch (view.getId())
               {
                    case R.id.imgRmv:
                         displayMessage.onResultSuccess(booleanResponse);
                         displayMessage.warning("Do you want to remove this experience??");
                         break;
                    case R.id.txtUrls:
                         workExperience();
                         spinnerLocation();
                         break;
               }
          }

          //work experience
          @SuppressLint({"InflateParams", "SetTextI18n"})
          private void workExperience()
          {
               view = LayoutInflater.from(context).inflate(R.layout.work_experience,null);
               AlertDialog.Builder builder;
               final AlertDialog alertDialog;
               builder = new AlertDialog.Builder(context);
               builder.setView(view);
               builder.setCancelable(false);
               alertDialog = builder.create();
               Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
               alertDialog.show();

               txtFrom = view.findViewById(R.id.txtForm);
               txtTo = view.findViewById(R.id.txtTo);
               txtCompany = view.findViewById(R.id.txtCompany);
               txtPosition = view.findViewById(R.id.txtPos);
               txtDescription = view.findViewById(R.id.txtDes);
               checkBox = view.findViewById(R.id.checkBox);
               bSave = view.findViewById(R.id.bSave);
               spinner = view.findViewById(R.id.sCity);
               imgCancel = view.findViewById(R.id.imgCancel);
               bSave.setText("Change");

               if(!currentModel.getStdId().equals(sharedPreferencesData.getCurrentUserId()))
               {
                    txtCompany.setEnabled(false);
                    txtPosition.setEnabled(false);
                    txtDescription.setEnabled(false);
                    checkBox.setEnabled(false);
                    txtFrom.setEnabled(false);
                    txtTo.setEnabled(false);
                    bSave.setVisibility(View.GONE);
               }

               txtCompany.setText(currentModel.getCompany());
               txtPosition.setText(currentModel.getPosition());
               if(!currentModel.getDescription().equals("none"))
                    txtDescription.setText(currentModel.getDescription());
               if(currentModel.getFromDate().equals("present"))
               {
                    checkBox.setChecked(true);
                    txtFrom.setVisibility(View.GONE);
                    txtTo.setVisibility(View.GONE);
               }else
               {
                    txtFrom.setText(currentModel.getFromDate());
                    txtTo.setText(currentModel.getToDate());
                    txtFrom.setVisibility(View.VISIBLE);
                    txtTo.setVisibility(View.VISIBLE);
               }

               imgCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         alertDialog.dismiss();
                    }
               });

               bSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         backgroundTask = new PostInfoBackgroundTask(context,responseTask);
                         Map<String,String> maps = new HashMap<>();
                         company = txtCompany.getText().toString();
                         txtPos = txtPosition.getText().toString();
                         from = txtFrom.getText().toString();
                         to = txtTo.getText().toString();
                         description = txtDescription.getText().toString();
                         if(!company.isEmpty()&&!txtPos.isEmpty())
                         {
                              maps.put("option","updateExp");
                              maps.put("id",currentModel.getId());
                              maps.put("company",company);
                              maps.put("position",txtPos);
                              maps.put("city",city);
                              if(!description.isEmpty())
                                   maps.put("description",description);
                              else maps.put("description","none");
                              if(checkBox.isChecked())
                              {
                                   maps.put("from","present");
                                   maps.put("to","present");
                              }else
                              {
                                   if(!from.isEmpty()&&!to.isEmpty()&&(requireMethods.calculateDate(from)<requireMethods.calculateDate(to)))
                                   {
                                        maps.put("from",from);
                                        maps.put("to",to);
                                   }else {
                                        Toast.makeText(context,"Please insert valid dates.",Toast.LENGTH_LONG).show();
                                        return;
                                   }
                              }

                              if(internetConnection.isOnline())
                                   backgroundTask.InsertData(context.getResources().getString(R.string.updateOperation),maps);
                              else
                                   Toast.makeText(context,context.getResources().getString(R.string.noInternet),Toast.LENGTH_LONG).show();
                              alertDialog.dismiss();
                         }else
                              Toast.makeText(context,"Please input valid data",Toast.LENGTH_LONG).show();

                    }
               });

               checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         if(checkBox.isChecked())
                         {
                              txtFrom.setEnabled(false);
                              txtTo.setEnabled(false);
                              txtFrom.setVisibility(View.GONE);
                              txtTo.setVisibility(View.GONE);
                              txtFrom.setText("");txtTo.setText("");
                         }else
                         {
                              txtFrom.setEnabled(true);
                              txtTo.setEnabled(true);
                              txtFrom.setVisibility(View.VISIBLE);
                              txtTo.setVisibility(View.VISIBLE);
                         }
                    }
               });

               txtFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         DatePickerDialog datePickerDialog = new DatePickerDialog(context, ExperienceAdapter.ViewHolder.this, year, month, day);
                         datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                         datePickerDialog.show();
                         flag = true;
                    }
               });

               txtTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         DatePickerDialog datePickerDialog = new DatePickerDialog(context, ExperienceAdapter.ViewHolder.this, year, month, day);
                         datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                         datePickerDialog.show();
                         flag = false;
                    }
               });
          }

          //set job location in spinner
          private void spinnerLocation()
          {
               ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.division,R.layout.support_simple_spinner_dropdown_item);
               adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
               spinner.setAdapter(adapter);
               int pos = adapter.getPosition(currentModel.getCity());
               spinner.setSelection(pos);
               spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                         city = parent.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
               });
          }

          //remove one experience
          private void removeExperience()
          {
               backgroundTask = new PostInfoBackgroundTask(context,onResponseTask);
               Map<String,String>maps = new HashMap<>();
               maps.put("option","expRmv");
               maps.put("id",currentModel.getId());
               if(internetConnection.isOnline())
                    backgroundTask.InsertData(context.getResources().getString(R.string.deleteOperation),maps);
               else Toast.makeText(context,context.getResources().getString(R.string.noInternet),Toast.LENGTH_LONG).show();
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
               int month1 = mm+1;
               String d,m;
               d = dd +"";m = month1+"";
               if(dd <10)
                    d="0"+ dd;
               if(month1<10)
                    m = "0"+month1;

               if(flag)
                    txtFrom.setText(d+"-"+m+"-"+ yy);
               else
                    txtTo.setText(d+"-"+m+"-"+ yy);
          }

          private OnResponseTask responseTask = new OnResponseTask() {
               @Override
               public void onResultSuccess(String value) {
                    if(value.equals("success"))
                    {
                         modelList.get(position).setDescription(description);
                         modelList.get(position).setCompany(company);
                         modelList.get(position).setPosition(txtPos);
                         notifyItemChanged(position);
                         String msg = "This experience is updated successfully";
                         Snackbar snackbar = Snackbar.make(relativeLayout, Html.fromHtml("<font color=\"#00C8F4\">"+msg+"</font>"),
                              Snackbar.LENGTH_SHORT);
                         snackbar.show();
                    }else
                    {
                         notifyItemChanged(position);
                         String msg = "Sorry!Failed to update this experience,please try again.";
                         Snackbar snackbar = Snackbar.make(relativeLayout, Html.fromHtml("<font color=\"#00C8F4\">"+msg+"</font>"),
                              Snackbar.LENGTH_SHORT);
                         snackbar.show();
                    }
               }
          };

          private OnResponseTask onResponseTask = new OnResponseTask() {
               @Override
               public void onResultSuccess(String value) {
                    if(value.equals("success"))
                    {
                         modelList.remove(position);
                         notifyItemRemoved(position);
                         notifyItemRangeChanged(position, modelList.size());
                         String msg = "This experience is deleted successfully";
                         Snackbar snackbar = Snackbar.make(relativeLayout, Html.fromHtml("<font color=\"#00C8F4\">"+msg+"</font>"),
                              Snackbar.LENGTH_SHORT);
                         snackbar.show();
                    }
                    else {
                         String msg = "Sorry!Failed to delete experience.please try again";
                         Snackbar snackbar = Snackbar.make(relativeLayout, Html.fromHtml("<font color=\"#00C8F4\">" + msg + "</font>"),
                              Snackbar.LENGTH_SHORT);
                         snackbar.show();
                    }
               }
          };

          private BooleanResponse booleanResponse = new BooleanResponse() {
               @Override
               public void onCompleteResult(boolean flag) {
                    if(flag)
                         removeExperience();//remove experience
               }
          };
     }
}
