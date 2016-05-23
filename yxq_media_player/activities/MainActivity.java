package com.test.yxq.yxq_media_player.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.test.yxq.yxq_media_player.R;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openMedia(View view) {
        Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
        intent.putExtra("url", Environment.getExternalStorageDirectory().getPath() + "/testfile.mp4");
        //intent.putExtra("url", "http://27.28.171.38/pl3.live.panda.tv/live_panda/1189f2645fe5c94beb2bcfee5cbd9745.flv?wshc_tag=0&wsts_tag=5742a40b&wsid_tag=1b114197&wsiphost=ipdbm");
        startActivity(intent);
    }

    public void test(View view) {
        Intent intent = new Intent(MainActivity.this,TestActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
