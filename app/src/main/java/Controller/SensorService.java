package Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


import View.POIView;
import View.RadarView;
import GlobalSetting.MasterController;
import Model.SensorFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import static com.ccit.arglass2.ARActivity.angle;

public class SensorService implements SensorEventListener {



	private String directoryName = Environment.getExternalStorageDirectory().toString()+"/OmniGuider";//Using to storing the sensor log file

	private static final AtomicBoolean computing = new AtomicBoolean(false);
	private float mGravity[] = new float[3];
	private float mGeomagnetic[] = new float[3];
	private float smooth[] = new float[3];
	public static  float  orientation[] = new float[3];
	public static  float dorientation[] = new float[3];
	private float temporientation[] = new float[3];
	private float avgorientation[] = new float[3];

	public static float HoriOrientation;
	public static float VertOrientation;
	private Sensor sensor_accelerometer;
	private Sensor sensor_magnetometer;

	private float OldHorizontal;
	private float OldVertical;

	private int tempcount = 0; // 降低sensor update rate
	private int count_for_avg = 0;//用來平均水平的sensor data.
	private float tempavg = 0;//儲存平均sensor data


	//private File log = new File(directoryName +"/"+getTime()+"Orientation.txt");
	//private FileWriter fsensor;

	public void SensorServiceStart(SensorManager sensorManager)
	{
		sensor_accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensor_magnetometer  =sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


		sensorManager.registerListener(this,sensor_magnetometer,SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this,sensor_accelerometer,SensorManager.SENSOR_DELAY_GAME);


//		  try
//		  {
//			  if(log.createNewFile())
//			  {
//				  fsensor = new FileWriter(log);
//				  fsensor.write("Pitch,Yaw,Roll\n");
//			  }
//		  }
//		  catch (IOException e)
//		  {
//			  e.printStackTrace();
//		  }
	}
	public void SensorServiceremove(SensorManager sensorManager) {
		// TODO Auto-generated constructor stub
//		try
//		{
//			fsensor.flush();
//			fsensor.close();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
		sensorManager.unregisterListener(this);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (!computing.compareAndSet(false, true)) return;  //一樣之時不更新

		switch(event.sensor.getType())
		{

			//case Sensor.TYPE_MAGNETIC_FIELD:
			//	mGeomagnetic =event.values.clone();;
			//
			//	break;
			//case Sensor.TYPE_ACCELEROMETER:
			//	mGravity = event.values.clone();;
			case Sensor.TYPE_MAGNETIC_FIELD:
				smooth = SensorFilter.filter(2.0f, 4.0f, event.values.clone(),mGeomagnetic);
				mGeomagnetic[0] = smooth[0];
				mGeomagnetic[1] = smooth[1];
				mGeomagnetic[2] = smooth[2];

//	  	          mGeomagnetic[0]=(mGeomagnetic[0]*1+event.values[0])*0.5f;
//			        mGeomagnetic[1]=(mGeomagnetic[1]*1+event.values[1])*0.5f;
//			        mGeomagnetic[2]=(mGeomagnetic[2]*1+event.values[2])*0.5f;
				break;
			case Sensor.TYPE_ACCELEROMETER:
				smooth = SensorFilter.filter(0.5f, 1.0f, event.values.clone(), mGravity);
				mGravity[0] = smooth[0];
				mGravity[1] = smooth[1];
				mGravity[2] = smooth[2];
//		  			mGravity[0]=(mGravity[0]*2+event.values[0])*0.33334f;
//		  			mGravity[1]=(mGravity[1]*2+event.values[1])*0.33334f;
//		  			mGravity[2]=(mGravity[2]*2+event.values[2])*0.33334f;



				if (mGravity != null && mGeomagnetic != null)
				{
					float R[] = new float[9];
					float I[] = new float[9];
					float[] outR = new float[9];
					boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
					SensorManager.remapCoordinateSystem(R,
							SensorManager.AXIS_X, //for samsung USE Z
							SensorManager.AXIS_Z, //for samsung USE X
							outR);
					if(success)
					{
						SensorManager.getOrientation(outR, orientation);
						dorientation[0] = (float) Math.toDegrees(orientation[0]);
						dorientation[1] = (float) Math.toDegrees(orientation[1]);
						dorientation[2] = (float) Math.toDegrees(orientation[2]);
//		  				Log.i("Omniguider", "test 0 = " +dorientation[1] ); // pitch for alcatel
//		  				Log.i("Omniguider", "test 0 = " +dorientation[0] ); // yaw for alcatel

						//Log.i("Omniguider", "test 0 = " +dorientation[2] ); // roll
					}
				}
				POIView.setVerOrientation(dorientation[1]);

				//POIView.setVerOrientation(-dorientation[1]); // for samsung
				float sent = 0;
				if(dorientation[0]<0) //將數字都轉成鄭樹
				{
					sent = 360 + dorientation[0];
				}// -170 => 190 , -160 => 200 ... and so on
				else
				{
					sent = dorientation[0];
//					Log.i("Omniguider","test0= "+ dorientation[0]);
				}


				if(tempcount == 50)
				{
					tempcount = 0;
					avgorientation[0] = temporientation[0] / 50;
					avgorientation[1] = temporientation[1] / 50;
					avgorientation[2] = temporientation[2] / 50;
					temporientation[0] = 0;
					temporientation[1] = 0;
					temporientation[2] = 0;

				}
				else
				{
					tempcount++;
					temporientation[0] = temporientation[0] + sent;
					temporientation[1] = temporientation[1] + dorientation[1];
					temporientation[2] = temporientation[2] + dorientation[2];
				}

//				try
//				{
//					fsensor.write(String.valueOf(dorientation[1])+","+String.valueOf(sent)+","+String.valueOf(dorientation[2])+","+String.valueOf(avgorientation[1])+","+String.valueOf(avgorientation[0])+","+String.valueOf(avgorientation[2])+"\n");
//				}
//				catch (IOException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				POIView.setHoriOrientation(sent);
				RadarView.setOrientation(sent);  //通知Radarview要更新 根據這個角度
				angle=sent;
		}
		computing.set(false);
	}


	//	public float getHoriOrientation()
//	{
//		return HoriOrientation;
//	}
	public String getTime()
	{
		String time;
		Time t = new Time();
		t.setToNow();
		time= "M_"+t.month + " "+ "D_"+t.monthDay + " "+"h_"+ t.hour+" "+"m_" +t.minute+" "+"s_"+t.second;
		return time;
	}
	public float[] getOrientation()
	{
		return dorientation;
	}
}
