package com.atik_faysal.diualumni.others;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.adapter.ViewPagerAdapter;

import java.util.Objects;

/**
 * Created by USER on 2/23/2018.
 */

public class SetTabLayout extends AppCompatActivity
{
        protected Toolbar toolbar;
        protected TabLayout tabLayout;
        protected ViewPager pager;
        protected ViewPagerAdapter adapter;
        private String user;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.tab_layout);
                user = Objects.requireNonNull(getIntent().getExtras()).getString("user");
                initComponent();
                setToolbar();
                setTabLayout();
        }

        protected void initComponent()
        {
                tabLayout = findViewById(R.id.tabLayout);
                pager = findViewById(R.id.pager);
                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
        }

        //set a toolbar,above the page
        protected void setToolbar()
        {
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(R.drawable.icon_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                finish();
                        }
                });
        }

        //initialize tablayout
        private void setTabLayout()
        {
                tabLayout.addTab(tabLayout.newTab().setText("Home"));
                tabLayout.addTab(tabLayout.newTab().setText("About"));
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                adapter = new ViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
                pager.setAdapter(adapter);
                pager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                                pager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {}

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {}
                });
        }


}
