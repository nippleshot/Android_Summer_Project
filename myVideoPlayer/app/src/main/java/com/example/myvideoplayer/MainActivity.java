package com.example.myvideoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.myvideoplayer.models.MediaObject;
import com.example.myvideoplayer.util.MyJsonParser;
import com.example.myvideoplayer.util.Resources;
import com.example.myvideoplayer.util.VerticalSpacingItemDecorator;
import com.google.android.exoplayer2.C;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements FragmentAll.OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    public FragmentArchived fragmentArchived;
    public FragmentManager fragmentManager;

    // --------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        fragmentArchived = new FragmentArchived();

        tabLayout = (TabLayout) findViewById(R.id.table_layout_id);
        viewPager = (ViewPager) findViewById(R.id.view_pager_id);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentAll(), "最热视频");
        viewPagerAdapter.addFragment(fragmentArchived, "我的视频");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_fireplace_24);
        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#f54242"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_star_24);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_IN);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            FragmentArchived f1 = new FragmentArchived();
            getSupportFragmentManager().beginTransaction().replace(R.id.view_pager_id, f1).commit();
            fragmentArchived.setArguments(bundle);
            tabLayout.getTabAt(1).select();
        }

    }

    @Override
    public void onBackPressed() {
        onPause();
    }

    @Override
    public void onFragmentInteraction(String text) {
        fragmentArchived.renewArchivedList(text);
    }
}