package com.oasis.onebox;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    private String IP_ADDRESS = "169.254.146.86";
    private String url = "http://"+IP_ADDRESS+":8080/api/mobile";

    private static final String TAG = "MainActivity";

    private String username;
    private String onebox;

    private TextView textView;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressBar mProgressBar;

//    private FileFragment fileFragment;
//    private ShareFragment shareFragment;
//    private TransportFragment transportFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
        Intent intent = getIntent();
        username = intent.getStringExtra("onebox");
        onebox = intent.getStringExtra("username");
//        toastString(onebox,Toast.LENGTH_SHORT);
        getFileList(url);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        myToolbar.setNavigationIcon(R.drawable.drawer_open);
//        myToolbar.setTitle("OneBox");
//        setSupportActionBar(myToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, myToolbar, R.string.drawer_open,
//                R.string.drawer_close);
//        mDrawerToggle.syncState();
//        mDrawerLayout.addDrawerListener(mDrawerToggle);
//
//        ListView listView = (ListView) findViewById(R.id.left_drawer);
//        String[] mMenuTitles = getResources().getStringArray(R.array.drawer_list);
//        try {
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mMenuTitles);
//            listView.setAdapter(arrayAdapter);
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//        }
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectItem(position);
//            }
//        });
//        listView.setItemChecked(1,true);
//
//        Bundle bundle = new Bundle();
//        bundle.putString("username",username);
//        bundle.putString("onebox",onebox);
//
//        FragmentManager manager=getSupportFragmentManager();
//        FragmentTransaction transaction=manager.beginTransaction();
//        if(fileFragment==null){
//            fileFragment=new FileFragment();
//        }
//        fileFragment.setArguments(bundle);
//        transaction.replace(R.id.frame,fileFragment);
//        transaction.commit();
    }

    public void getFileList(final String newurl)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils
                        .get()
                        .url(newurl)
                        .addParams("onebox",onebox)
                        .build()
                        .execute(new StringCallback()
                        {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                toastString("Can not get file list:"+e, Toast.LENGTH_LONG);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e(TAG,response.toString());
                                String list ="";
                                Gson gson = new Gson();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    list = jsonObject.getString("result");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                List<CloundFile> fileList = gson.fromJson(list, new TypeToken<List<CloundFile>>(){}.getType());
                                for(CloundFile file : fileList)
                                {
                                    file.setPlayOnline(false);
                                    file.setPlayOnline(false);
                                    String type = file.getFileType();
                                    file.setImage(getResource(type));
                                }

                                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                                recyclerView.setAdapter(new CloudFileAdapter(MainActivity.this, fileList));
                            }
                        });
            }
        }).start();
    }

    public void downloadFile(final String path, final String filename)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,url+"/"+path+"/download");
                OkHttpUtils//
                        .get()//
                        .url(url+"/"+path+"/download")//
                        .addParams("onebox",onebox)
                        .build()//
                        .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), filename)//
                        {
                            @Override
                            public void inProgress(float progress, long total, int id)
                            {
                                mProgressBar.setProgress((int) (100 * progress));
                                Log.e(TAG, "inProgress :" + (int) (100 * progress));
                            }

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e(TAG, "onError :" + e.getMessage());
                            }

                            @Override
                            public void onResponse(File file, int id) {
                                Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                            }
                        });
            }
        }).start();
    }

    public int  getResource(String imageName){
        Context ctx=getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable" , ctx.getPackageName());
        return resId;
    }

    private void toastString(final String toastContent, final int longOrShort)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,toastContent,longOrShort).show();
            }
        });
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);

    }

    private void setListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setTextView(final String content)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(content);
            }
        });
    }

    public void refreshView(String url)
    {
        getFileList(this.url+"/"+url);
    }

//    private void selectItem(int position){
//        FragmentManager manager=getSupportFragmentManager();
//        FragmentTransaction transaction=manager.beginTransaction();
//        switch (position){
//            case 0:
//                if(fileFragment==null){
//                    fileFragment=new FileFragment();
//                }
//                transaction.replace(R.id.frame,fileFragment);
//                transaction.commit();
//                break;
//            case 1:
//                if(transportFragment==null){
//                    transportFragment=new TransportFragment();
//                }
//                transaction.replace(R.id.frame,transportFragment);
//                transaction.commit();
//                break;
//            case 2:
//                if(shareFragment==null){
//                    shareFragment=new ShareFragment();
//                }
//                transaction.replace(R.id.frame,shareFragment);
//                transaction.commit();
//                break;
//        }
//    }


//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }
}
