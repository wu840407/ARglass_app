package com.ccit.arglass2;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import GlobalSetting.MasterController;
import View.POIView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import View.CustomCameraView;
import View.RadarView;


import static GlobalSetting.MasterController.POI;
import static View.RadarView.DB;

public class ARActivity extends AppCompatActivity implements LocationListener {
    public POIView mPOIView;
    private FrameLayout.LayoutParams framelayoutparams;
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;    //最佳資訊提供者
    private FrameLayout framelayout;
    public RadarView mRadar;
    private CustomCameraView customcameraview;
    private Location location;//GPS位置
    private Handler mHandler;
    private ImageButton ARHomeButton;//離開按鈕
    private ImageView image;
    private TextView  AD, Loc,Dis;
    private double Ls = 0, Cs = 0, Rs = 0;
    public static float angle = 0;
    private ProgressBar spinner;
    private boolean getService = false;
    private Long startTime;
    private Long l, counter = 0L;
    /*******************/
    private int iWidth;
    private int iHeight;
    int iButtonSize;
    /*******************/
    public static  int start=0;
    static public double longitude=0, latitude=0;
    private double longitude1=0,latitude1=0;
    String showUri = "http://192.168.2.101/arglasses/index.php";
    com.android.volley.RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest

            (Request.Method.POST, showUri, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response.toString());
                    try {
                        JSONArray data = response.getJSONArray("data");
                        //這邊要和上面json的名稱一樣
                        //下邊是把全部資料都印出來
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jasondata = data.getJSONObject(i);
                            String place = jasondata.getString("place");
                            String latitude1 = jasondata.getString("latitude");
                            String longitude1 = jasondata.getString("latitude");
                            AD.append(place + " " + latitude1 + " " + longitude1 + " " + " \n");
                            POI[i][0] = place;
                            POI[i][1] = latitude1;
                            POI[i][2] = longitude1;
                            //txt是textview
                        }
                        //AD.append("===\n");//把資料放到textview顯示出來
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.append(error.getMessage());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_armode);
        //取得系統定位服務
        testLocationProvider();        //檢查定位服務
        ocINILayout();
        startTime = System.currentTimeMillis();
        mHandler = new Handler();
        mHandler.post(runnable);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
        POIView.setPOInum(POI.length);
        POIView.setPOI(MasterController.POI);
        POIView.setPOInumofAggregation(POI.length);
        POIView.setAggregationPOI(POI);
        POIView.setPOInumofFOV(POI.length);
        POIView.setPOIofFOV(POI);
    }

    private void testLocationProvider() {
        //取得系統定位服務
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();
            getService = true;    //確認開啟定位服務
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //開啟設定頁面
        }
    }

    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);    //取得系統定位服務
        Criteria criteria = new Criteria();    //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = lms.getLastKnownLocation(bestProvider);
        getLocation(location);
    }

    private void getLocation(Location location) {    //將定位資訊顯示在畫面中
        if (location != null) {
            longitude = location.getLongitude();    //取得經度
            latitude = location.getLatitude();    //取得緯度
            POIView.setLat(latitude); // pass location data to POIView
            POIView.setLng(longitude );
            start=1;
        }

    }


    private void ocINILayout() {

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        iWidth = this.getResources().getDisplayMetrics().widthPixels;
        iHeight = this.getResources().getDisplayMetrics().heightPixels;
        iButtonSize = iWidth / 15;
        framelayout = new FrameLayout(this.getApplicationContext());//建立一個框架
        setContentView(framelayout);        //將頁面設為framelayout
        customcameraview = new CustomCameraView(this.getApplicationContext());        //開啟相機
        framelayout.addView(customcameraview);		//將相機畫面貼在頁面上
        framelayout.addView(getLayoutInflater().inflate(R.layout.activity_armode, null));
        framelayoutparams = new FrameLayout.LayoutParams(iWidth,iHeight, Gravity.BOTTOM);
        mPOIView = new POIView(this,iWidth,iHeight);
        framelayout.addView(mPOIView, framelayoutparams);

        framelayout.addView(getLayoutInflater().inflate(R.layout.activity_armode, null));

        mRadar = new RadarView(this);
        FrameLayout.LayoutParams p_radar = new FrameLayout.LayoutParams(iHeight / 2, iHeight / 2, Gravity.AXIS_X_SHIFT);
        p_radar.setMargins(iWidth - iHeight / 2, 0, 0, 0);

        framelayout.addView(mRadar, p_radar);
        spinner = (ProgressBar) findViewById(R.id.progressBar2);
        Loc = (TextView) findViewById(R.id.textView3);
        AD = (TextView) findViewById(R.id.textView4);
        Dis = (TextView) findViewById(R.id.textView5);
        image=(ImageView)findViewById(R.id.imageView2);
        image.setBackgroundResource(R.drawable.disappear);
        ARHomeButton = (ImageButton) findViewById(R.id.exitbutton);
        ARHomeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ARHomeButton.setBackgroundResource(R.drawable.exit);
                } else {
                    ARHomeButton.setBackgroundResource(R.drawable.exit_g);
                }
                return false;
            }
        });
        ARHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ARHomeButton.setBackgroundResource(R.drawable.exit);
                framelayout.removeAllViews();
                finish();
            }
        });
    }

    private void counter() {

        for (int i = 0; i < DB.length; i++) {
            if (angle >= 90 && angle <= 270) {
                if (angle >= DB[i][1]) {
                    if (angle - DB[i][1] <=5) {
                        DB[i][2] = 2;
                    } else if (angle - DB[i][1] >= 5&& angle - DB[i][1] <= 55) {
                        DB[i][2] = 1;
                    }
                } else if (angle < DB[i][1]) {
                    if (DB[i][1] - angle <= 5) {
                        DB[i][2] = 2;
                    } else if (DB[i][1] - angle >=5&& DB[i][1] - angle <=55) {
                        DB[i][2] = 3;
                    }
                }
            } else if (angle > 270 && angle <= 360) {
                if (angle >= DB[i][1] && DB[i][1] > 180) {
                    if (angle - DB[i][1] <= 5) {
                        DB[i][2] = 2;
                    } else if (angle - DB[i][1] >=5 && angle - DB[i][1] <= 55) {
                        DB[i][2] = 1;
                    }
                } else if (angle < DB[i][1]) {
                    if (DB[i][1] - angle <=5) {
                        DB[i][2] = 2;
                    } else if (DB[i][1] - angle >=5 && DB[i][1] - angle <= 55) {
                        DB[i][2] = 3;
                    }
                } else if (angle >= DB[i][1] && DB[i][1] < 90) {
                    if (DB[i][1] + 360 - angle <= 5) {
                        DB[i][2] = 2;
                    } else if (DB[i][1] + 360 - angle >=5 && DB[i][1] + 360 - angle <=55) {
                        DB[i][2] = 3;
                    }
                }
            } else if (angle >= 0 && angle < 90) {
                if (angle >= DB[i][1]) {
                    if (angle - DB[i][1] <= 5) {
                        DB[i][2] = 2;
                    } else if (angle - DB[i][1] >=5 && angle - DB[i][1] <= 55) {
                        DB[i][2] = 1;
                    }
                } else if (angle < DB[i][1] && DB[i][1] < 180) {
                    if (DB[i][1] - angle <=5) {
                        DB[i][2] = 2;
                    } else if (DB[i][1] - angle >= 5&& DB[i][1] - angle <=55) {
                        DB[i][2] = 3;
                    }
                } else if (angle < DB[i][1] && DB[i][1] > 270) {
                    if (angle + 360 - DB[i][1] <= 5) {
                        DB[i][2] = 2;
                    } else if (angle + 360 - DB[i][1] >=5 && angle + 360 - DB[i][1] <=55) {
                        DB[i][2] = 1;
                    }
                }
            }
            if (DB[i][2] == 1) {
                if (Ls == 0) {
                    Ls = DB[i][0];
                }
                if (DB[i][0] < Ls && Ls != 0) {
                    Ls = DB[i][0];
                }

            }
            if (DB[i][2] == 2) {

                if (Cs == 0) {
                    Cs = DB[i][0];
                    AD.setText(POI[i][0]);
                    Dis.setText("Distance: "+String.format("%.2f", Cs) + "m");
                    image.setBackgroundResource(R.drawable.movefocus);
                }
                if (DB[i][0] < Cs && Cs != 0) {
                    AD.setText(POI[i][0]);
                    Dis.setText("Distance: "+String.format("%.2f", Cs) + "m");
                    Cs = DB[i][0];
                    l = 0L;
                    image.setBackgroundResource(R.drawable.movefocus);
                }
                counter = l;
            }
            if (DB[i][2] == 3) {
                if (Rs == 0) {
                    Rs = DB[i][0];

                }
                if (DB[i][0] < Rs && Rs != 0) {
                    Rs = DB[i][0];

                }

            }

        }
    }
    private void openDialog() {
        new AlertDialog.Builder(this)
                .setTitle("C.C.I.T. Library")
                .setMessage("Both physical library and digital knowledge management center")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                //按下按鈕後執行的動作，沒寫則退出Dialog
                            }
                        }
                )
                .show();
    }
    private final Runnable runnable = new Runnable() {
        public void run() {
            mHandler.postDelayed(runnable, 500);
            locationServiceInitial();
            counter();
            longitude1=longitude;
            latitude1=latitude;
            Loc.setText("GPS:"+String.valueOf(longitude) + "," + String.valueOf(latitude));
            l = ((System.currentTimeMillis() - startTime) / 1000) % 60;
            // TODO Auto-generated method stub
            // 需要背景作的事
            if (Cs == 0) {

                if(latitude1-latitude >=0.1)
                {
                    image.setBackgroundResource(R.drawable.move);
                }
                else
                {
                    if (counter == 20L) {
                        image.setBackgroundResource(R.drawable.turnstop);
                    }
                    if (counter == 5L) {
                        image.setBackgroundResource(R.drawable.turn);
                    }

                }
            }
            if (counter == 50L || l == 50L) {

                counter = 1L;
                startTime = System.currentTimeMillis();
            }
            if (counter == 20L) {
                image.setBackgroundResource(R.drawable.turnstop);
                spinner.setVisibility(View.VISIBLE);
            }
            if (counter ==30L) {

                spinner.setVisibility(View.INVISIBLE);
                openDialog();
            }

        }
    };

    @Override
    protected void onRestart() {	//從其它頁面跳回時
        // TODO Auto-generated method stub
        super.onRestart();
        testLocationProvider();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getService) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lms.requestLocationUpdates(bestProvider, 1000, 1,  this);
            //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
        }
    }
    @Override
    public void onPause()
    {
        start=0;
        super.onPause();
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }
        if(getService) {
            lms.removeUpdates(this);	//離開頁面時停止更新
        }
    }


    @Override
    public void onLocationChanged(Location location) {	//當地點改變時
        // TODO Auto-generated method stub
        getLocation(location);
    }

    @Override
    public void onProviderDisabled(String arg0) {	//當GPS或網路定位功能關閉時
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String arg0) {	//當GPS或網路定位功能開啟
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {	//定位狀態改變
        // TODO Auto-generated method stub
    }


    public static void getTime()
    {
        String NowTime;
        Time t = new Time();
        t.setToNow();
        NowTime= "Time : " + t.hour+ " : " +t.minute + " : "+t.second;
        POIView.StartPerceiveTime = NowTime;

    }
}
