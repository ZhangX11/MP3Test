package com.ctyon.mp3test;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private MediaService.MyBinder mMyBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (Build.VERSION.SDK_INT>22){
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            }
        }
        initData();

    }

    private void initView() {
        Button button = findViewById(R.id.btn_play);
        button.setOnClickListener(this);
        Button btnPause = findViewById(R.id.btn_pause);
        btnPause.setOnClickListener(this);
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
    }

    private void initData() {
        List<MusicBean> musics = FileManager.getInstance(this).getMusics();
        for (MusicBean music : musics) {
            //Log.e(TAG,music.getPath());
        }
        Intent mediaServiceIntent = new Intent(this, MediaService.class);
        //mediaServiceIntent.putExtra("MUSICS", (Parcelable) musics);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("MUSICS", (ArrayList<? extends Parcelable>) musics);
        mediaServiceIntent.putExtras(bundle);
        startService(mediaServiceIntent);
        //够了就设置路径等，准备播放
        bindService(mediaServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyBinder = (MediaService.MyBinder) service;
            Log.e(TAG, "Service与Activity已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                mMyBinder.playMusic();
                break;
            case R.id.btn_pause:
                mMyBinder.pauseMusic();
                break;
            case R.id.btn_next:
                mMyBinder.nextMusic();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyBinder.closeMedia();
        unbindService(mServiceConnection);
    }

    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this,"测试中，再按一次退出", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
