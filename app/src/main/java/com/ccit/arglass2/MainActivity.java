package com.ccit.arglass2;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import GlobalSetting.MasterController;

public class MainActivity extends AppCompatActivity {
    private ImageButton ARButton;//AR模式按鈕
    private static SensorManager SensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //取得系統定位服務
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //偵測GPS
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }
        if (mNetworkInfo != null &&  mNetworkInfo.isConnected()) {
            //偵測網路
        } else {

            Toast.makeText(this, "請開啟網路服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));	//開啟設定頁面
        }
        ARButton=(ImageButton)findViewById(R.id.ARButton);
        ARButton.setOnTouchListener(new View.OnTouchListener()//偵測
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // TODO Auto-generated method stub
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    ARButton.setBackgroundResource(R.drawable.main);
                }
                else
                {
                    ARButton.setBackgroundResource(R.drawable.main_select);
                }
                return false;
            }
        });
        ARButton.setOnClickListener(new View.OnClickListener()//按鈕
        {
            @Override
            public void onClick(View v)
            {
                //TODO Auto-generated method stub

                Intent intent = new Intent(MainActivity.this,ARActivity.class);
                startActivity(intent);
            }
        });
        SensorManager = (SensorManager) getSystemService(MainActivity.SENSOR_SERVICE);
        MasterController.SENSORService.SensorServiceStart(SensorManager);
    }
    @Override
    public void onPause()
    {
        super.onPause();

    }
    @Override
    public void onStop()
    {
        super.onStop();
    }
}
