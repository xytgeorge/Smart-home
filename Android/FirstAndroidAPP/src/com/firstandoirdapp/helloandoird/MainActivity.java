package com.firstandoirdapp.helloandoird;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	MediaPlayer mMediaPlayer;
	MyView myview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       //MediaPlayer类
        mMediaPlayer = MediaPlayer.create(this,R.raw.midi); 
        mMediaPlayer.start();
       // mMediaPlayer.stop();
        //myview = new MyView(this);
        //this.setContentView(myview);
        
        Log.v("TAG_1","冗余信息");
        Log.d("TAG_1","调试信息");
        Log.i("TAG_1","普通信息");
        Log.w("TAG_1","警告信息");
        Log.e("TAG_1","错误信息");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
