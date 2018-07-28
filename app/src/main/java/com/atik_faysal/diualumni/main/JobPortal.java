package com.atik_faysal.diualumni.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.background.SharedPreferencesData;
import com.atik_faysal.diualumni.important.DisplayMessage;
import com.atik_faysal.diualumni.interfaces.Methods;


public class JobPortal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,Methods
{

     private DrawerLayout drawerLayout;
     private ActionBarDrawerToggle mToggle;
     private SharedPreferencesData sharedPreferencesData;
     private DisplayMessage displayMessage;
     private NavigationView navigationView;

     private TextView txtName,txtPhone;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.job_portal);
          drawerLayout = findViewById(R.id.drawer);
          mToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
          drawerLayout.setDrawerListener(mToggle);
          mToggle.syncState();
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          initComponent();
     }


     @Override
     protected void onStart() {
          super.onStart();
          if(sharedPreferencesData.getIsUserLogin())
          {
               Menu menu = navigationView.getMenu();
               menu.findItem(R.id.navSignInOut).setTitle("Sign out");
          }
          if(sharedPreferencesData.checkBoxStatus())
          {
               txtName.setText(sharedPreferencesData.getUserName());
               txtPhone.setText(sharedPreferencesData.getUserPhone());
          }else
          {
               txtPhone.setVisibility(View.INVISIBLE);
               txtName.setVisibility(View.INVISIBLE);
          }
     }


     @Override
     public void initComponent()
     {
          navigationView = findViewById(R.id.nav_view);
          navigationView.setNavigationItemSelectedListener(this);
          View view = navigationView.inflateHeaderView(R.layout.nav_header);
          txtName = view.findViewById(R.id.txtName);
          txtPhone = view.findViewById(R.id.txtPhone);

          sharedPreferencesData = new SharedPreferencesData(this);
          displayMessage = new DisplayMessage(this);

     }

     @Override
     public void setToolbar() {

     }

     @Override
     public void processJsonData(String jsonData) {

     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
          return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
     }


     public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          // Handle navigation view item clicks here.

          switch (item.getItemId())
          {
               case R.id.navAlumni:
                    Toast.makeText(JobPortal.this,"option 1",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navPost:
                    startActivity(new Intent(JobPortal.this,PostNewJob.class));
                    break;
               case R.id.navUpload:
                    Toast.makeText(JobPortal.this,"option 3",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navFabJob:
                    Toast.makeText(JobPortal.this,"option 4",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navSetting:
                    Toast.makeText(JobPortal.this,"option 5",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navFeedback:
                    Toast.makeText(JobPortal.this,"option 6",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navAbout:
                    Toast.makeText(JobPortal.this,"option 7",Toast.LENGTH_LONG).show();
                    break;
               case R.id.navSignInOut:
                    startActivity(new Intent(JobPortal.this,SignIn.class));
                    break;
          }
          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
     }
}
