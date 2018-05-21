package com.ctyon.mp3test;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by zx
 * On 2018/5/19
 */
public class MediaService extends Service implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "MediaService";
    private MyBinder mBinder = new MyBinder();
    //标记当前歌曲的序号
    private int i = 0;
    //初始化MediaPlayer
    public MediaPlayer mMediaPlayer = new MediaPlayer();
    private List<MusicBean> musics;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer.setOnCompletionListener(this);
        Log.e(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand");
        musics = intent.getParcelableArrayListExtra("MUSICS");
        for (MusicBean music : musics) {
            Log.e(TAG,music.getPath());
        }
        if(musics.size()>0){
            Toast.makeText(this,"找到"+musics.size()+"首歌曲",Toast.LENGTH_SHORT).show();
            iniMediaPlayerFile(i);
        }else {
            Toast.makeText(this,"没找到歌曲",Toast.LENGTH_SHORT).show();
            Log.e(TAG,"****musics.size()=0*****");
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mBinder.nextMusic();
    }

    public class MyBinder extends Binder {

        //        /**
        //         *  获取MediaService.this（方便在ServiceConnection中）
        //         *
        //         * *//*
        //        public MediaService getInstance() {
        //            return MediaService.this;
        //        }*/
        /**
         * 播放音乐
         */
        public void playMusic() {
            if (!mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.start();
            }
        }

        /**
         * 暂停播放
         */
        public void pauseMusic() {
            if (mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.pause();
            }
        }

        /**
         * reset
         */
        public void resetMusic() {
            if (!mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.reset();
                iniMediaPlayerFile(i);
            }
        }

        /**
         * 关闭播放器
         */
        public void closeMedia() {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }

        /**
         * 下一首
         */
        public void nextMusic() {
            if (mMediaPlayer != null){
                if ( i == musics.size()-1) {
                    //切换歌曲reset()很重要很重要很重要，没有会报IllegalStateException
                    i = 0;
                }else {
                    i = i+1;
                }

                mMediaPlayer.reset();
                iniMediaPlayerFile(i);
                playMusic();

            }
        }

        /**
         * 上一首
         *//*
        public void preciousMusic() {
            if (mMediaPlayer != null && i < 4 && i > 0) {
                mMediaPlayer.reset();
                iniMediaPlayerFile(i - 1);
                if (i == 1) {

                } else {

                    i = i - 1;
                }
                playMusic();
            }
        }*/

        /**
         * 获取歌曲长度
         **/
        public int getProgress() {

            return mMediaPlayer.getDuration();
        }

        /**
         * 获取播放位置
         */
        public int getPlayPosition() {

            return mMediaPlayer.getCurrentPosition();
        }
        /**
         * 播放指定位置
         */
        public void seekToPositon(int msec) {
            mMediaPlayer.seekTo(msec);
        }

    }


    /**
     * 添加file文件到MediaPlayer对象并且准备播放音频
     */
    private void iniMediaPlayerFile(int dex) {
        //获取文件路径
        try {
            //此处的两个方法需要捕获IO异常
            //设置音频文件到MediaPlayer对象中
            mMediaPlayer.setDataSource(musics.get(dex).getPath());
            //让MediaPlayer对象准备
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.d(TAG, "设置资源，准备阶段出错");
            e.printStackTrace();
        }
    }
}
