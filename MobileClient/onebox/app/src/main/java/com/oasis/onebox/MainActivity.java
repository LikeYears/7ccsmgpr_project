package com.oasis.onebox;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FileFragment.OnFragmentInteractionListener {


    private FileFragment fileFragment;
    private ShareFragment shareFragment;
    private TransportFragment transportFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setNavigationIcon(R.drawable.drawer_open);
        myToolbar.setTitle("OneBox");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, myToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        ListView listView = (ListView) findViewById(R.id.left_drawer);
        String[] mMenuTitles = getResources().getStringArray(R.array.drawer_list);
        try {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mMenuTitles);
            listView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        listView.setItemChecked(1,true);
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        if(fileFragment==null){
            fileFragment=new FileFragment();
        }
        transaction.replace(R.id.frame,fileFragment);
        transaction.commit();
    }

    private void selectItem(int position){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        switch (position){
            case 0:
                if(fileFragment==null){
                    fileFragment=new FileFragment();
                }
                transaction.replace(R.id.frame,fileFragment);
                transaction.commit();
                break;
            case 1:
                if(transportFragment==null){
                    transportFragment=new TransportFragment();
                }
                transaction.replace(R.id.frame,transportFragment);
                transaction.commit();
                break;
            case 2:
                if(shareFragment==null){
                    shareFragment=new ShareFragment();
                }
                transaction.replace(R.id.frame,shareFragment);
                transaction.commit();
                break;
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
