package Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.ccit.arglass2.ARActivity;
import GlobalSetting.MasterController;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;


public class BehaviorPerceive {

    private int INVALID_STATE=  100;  // Set the invalid value;
   	private int INVALID_XandV_STATE =103;
   	private int INVALID_Situation = 100;
	/**********************************************/
	//File Input properties
	static double alphaX = 1.0;
	static double alphaV = 0.308;
	static double alphaD = 15;
  	static double DX_CONSTRAINT=35;
  	static double SPEED_DEV=3;
//  	static double MAX_SPEED= 22.2;
  	static double MAX_DIFF = 3;
    public float[][][] OptTransMatrix;
  	static int TPMWeight;
	/**********************************************/
  	/***********************************************/	
  	
   	private double DX=0, DV=0, DD=0;// These three variables are used to pass data between FeatureExtract() and Classify()
  	//OnlinePerceive Value
	static int CS_Saved = 100; // Save the value of dropped current state
	static int CS_count = 0;
   	private int POSTURE_THRESHOLD=1;  // �����

  	/**********************************************/
   	/**********************************************/
   	//Make TPM
   	
   	/**********************************************/

   	private static Location Loc[] = new Location[2];
	private float preOrientation[] = new float[3];// prior orientation, 0 = ,1 = ,2 =
   	private float Orientation[] = new float[3];// now orientation, 0 = ,1 = ,2 = 
	private float[] _LatLog=new float [2];//Save previous value of longitude and latitude
	private float[] _LonLog=new float [2];	
	private float[] _preSpeed=new float [2];//Save previous value of speed
   	private float[] _Bearing=new float [2];//Save previous value of bearing
   	private float HeadingDiff; // feature of multisensor 
   	private float MoveDistance;// feature of multisensor 
   	private float BearingDiff;// feature of multisensor 
   	private int Userbehavior;//0~4
	private int states[] = new int[2];//Save previous value of states
   	
	private int initial_count = 1 ; // using for count first 4 times initial data value to full the prior neccessary data.
	
	private boolean bCheckPose = false; // using to save the status of pose. if invalid pose is occured, that will set false;
	
//	private int loss_limited = 0;
//	
	private String strReadPath;
	private FileWriter fw_RawData;
	/*private FileWriter fw_RawState;
	private FileWriter fw_FinalState;*/
	
	public void setGPS(Location loc)
	{
		Loc[1] = loc;
	}
	public void setOrientation(float[] orientation)
	{
		Orientation = orientation;
	}
	
   	private static double Dist(double lat1, double lon1, double lat2, double lon2)
	{
		double re=0;
		re=Math.sqrt(Math.pow((lat2-lat1)*60*1852, 2)+Math.pow((lon2-lon1)*60*1852*Math.cos(lat1*3.14159/180), 2));
		return re;
	}
	
	public BehaviorPerceive() {
		// TODO Auto-generated constructor stub
		for(int i=0;i<2;i++)
		{	
			states[i]=0;
			_LonLog[i]=0;
			_LatLog[i]=0;
			_preSpeed[i]=0;
			states[i]=0;
			_Bearing[i]=0;
		}

	}
	public void InitialPerceive()
	{// using to read TPM and config file.
		strReadPath = Environment .getExternalStorageDirectory ().getPath();
		WroteFile();
		ReadMat();
	}

	public void StartPerceive()
	{
		int CS = 0; //Classified State
    
		if(Loc[1]!=null)
    	{	  		
    		if(initial_count<5) // First Initial data.
    		{ 			
    			switch(initial_count)
    			{
    				case 1:
        				Loc[0] = Loc[1];
        	  			_Bearing[1] = Loc[1].getBearing();
        	  			_preSpeed[1] = Loc[1].getSpeed();
        	  			_LatLog[1] =  (float) Loc[1].getLatitude();
        	  			_LonLog[1] =  (float) Loc[1].getLongitude();
        	  			states[1]  = 0;
        				//Log.i("PGPS","PGPS algorithm First data");
        				break;
    				case 2:
    					Loc[0] = Loc[1];
    			  		states[0] = states[1];
    					_LonLog[0] = _LonLog[1];
    					_LatLog[0] = _LatLog[1];
    					_LonLog[1] = (float) Loc[1].getLongitude();
    					_LatLog[1] = (float) Loc[1].getLatitude();
    					_Bearing[0] = _Bearing[1];
    					_Bearing[1] = Loc[1].getBearing();
    					_preSpeed[0] = _preSpeed[1];	    				
    					_preSpeed[1] = Loc[1].getSpeed();
    					//Log.i("PGPS","PGPS algorithm second data");
        				break;
    				case 3:
    					Loc[0] = Loc[1];
    			  		states[0] = states[1];
    					_LonLog[0] = _LonLog[1];
    					_LatLog[0] = _LatLog[1];
    					_LonLog[1] = (float) Loc[1].getLongitude();
    					_LatLog[1] = (float) Loc[1].getLatitude();
    					_Bearing[0] = _Bearing[1];
    					_Bearing[1] = Loc[1].getBearing();
    					_preSpeed[0] = _preSpeed[1];	    				
    					_preSpeed[1] = Loc[1].getSpeed();
    			  		preOrientation[0] = Orientation[0];
    			  		preOrientation[1] = Orientation[1];
    			  		preOrientation[2] = Orientation[2];
    			  		
    			  		
    					//Log.i("PGPS","PGPS algorithm third data");
        				break;
    				case 4:
    					Loc[0] = Loc[1];
    			  		states[0] = states[1];
    					_LonLog[0] = _LonLog[1];
    					_LatLog[0] = _LatLog[1];
    					_LonLog[1] = (float) Loc[1].getLongitude();
    					_LatLog[1] = (float) Loc[1].getLatitude();
    					_Bearing[0] = _Bearing[1];
    					_Bearing[1] = Loc[1].getBearing();
    					_preSpeed[0] = _preSpeed[1];	    				
    					_preSpeed[1] = Loc[1].getSpeed();
    					//Log.i("PGPS","PGPS algorithm fourth data");
    			  		HeadingDiff = Math.abs(Orientation[0]- preOrientation[0]);
    			  		preOrientation[0] = Orientation[0];
    			  		preOrientation[1] = Orientation[1];
    			  		preOrientation[2] = Orientation[2];
    					//assume 0 = Heading 
    			  		break;
    			
    			}
    			initial_count++;	    			
    		}
    		else  // initial value of array is complete.
    		{	
    			
    			if((long)Loc[1].getTime()!=(long)Loc[0].getTime())
    			{//means newest input data is actually new, not the original.
    				Log.i("Omniguider", "For testing time Start Perceive");
    				
    				
    				ARActivity.getTime();
    				CS = ExtractFeature();
    				
    				if(CS == INVALID_Situation)
    	            {
    					//meaning that ignore data this time
    					bCheckPose = false;
    	            }
    	            else // valid
    	            {
    	            	bCheckPose = true;
    	            	CS = Classify(MoveDistance,BearingDiff,HeadingDiff,_preSpeed[1]);
    	                //classified state
    	        		/*try 
    	        		{
    	    				fw_RawState.write(CS+",");
    	    			}
    	        		catch (IOException e)
    	    			{
    	        			e.printStackTrace();
    	    			}*/
    	                CS = StateCrossCheck(CS);
    	                //Predicted state
    	                /*try 
    	        		{
    	    				fw_FinalState.write(CS+",");
    	    			} 
    	                catch (IOException e)
    	    			{
    	    				e.printStackTrace();
    	    			}*/
        	            states[0] = states[1];
        	            states[1] = CS;
        	            Log.i("Omniguider", "For testing time end Perceive");
    	            }

//    				MainActivity.pgpscontroller.setResult(0);
    			}
    			else // GPS loss �����p
    			{
    				//initial_count = 3;
    			}
    			
    		}	    		
    	}
    	else
    	{
    		//MainActivity.pgpscontroller.setResult(2);
    	}
		/*PGPS algorithm********************************************************************/
		// final
		//Log.i("PGPS","PGPS algorithm");
	}

	
	public void EndPerceive()
	{
		try
		{
			fw_RawData.flush();
			fw_RawData.close();
			/*fw_RawState.flush();
			fw_RawState.close();
			fw_FinalState.flush();
			fw_FinalState.close();*/
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
  	

	
	/*********************************ALGORITHM*************************************************************************************************************************/
  	private int ExtractFeature()
  	{	//Because the data filtering is finish. in this part, we will check the device orientation is valid or invalid
  		// The valid situation is that the device is 1. landscape, 2. shift horizontally, 3. no up down move and rotation. 
  		
  		float pitchDiff;
  		float rollDiff;
  		pitchDiff = Math.abs(Orientation[1]-preOrientation[1]);
  		rollDiff = Math.abs(Orientation[2]-preOrientation[2]);
  		if(pitchDiff > 20 || rollDiff > 20)
  		{
  			// meaning this time is not suitable to perceive user behavior
 			Loc[0] = Loc[1];
			_LonLog[0] = _LonLog[1];
			_LatLog[0] = _LatLog[1];
			_LonLog[1] = (float) Loc[1].getLongitude();
			_LatLog[1] = (float) Loc[1].getLatitude();
			MoveDistance = (float) Dist(_LatLog[0], _LonLog[0], _LatLog[1], _LonLog[1]);
			//Log.i("PGPS", "Model"+MoveDistance);
			_preSpeed[0] = _preSpeed[1];	    				
			_preSpeed[1] = Loc[1].getSpeed();
			//save the first feature "MoveDistance", 
			//because the update rate is 1hz, so the speed equal to distance
			_Bearing[0] = _Bearing[1];
			_Bearing[1] = Loc[1].getBearing();
			BearingDiff = Math.abs(_Bearing[1]-_Bearing[0]);
			//save the second feature "BearingDiff"
			HeadingDiff = Math.abs(Orientation[0]- preOrientation[0]);	
	  		preOrientation[0] = Orientation[0];
	  		preOrientation[1] = Orientation[1];
	  		preOrientation[2] = Orientation[2];
  			// meaning this time is not suitable to perceive user behavior
  			return INVALID_Situation;
  		}
  		else
  		{

  			//extract the feature data from the multisensor system
  			Loc[0] = Loc[1];
			_LonLog[0] = _LonLog[1];
			_LatLog[0] = _LatLog[1];
			_LonLog[1] = (float) Loc[1].getLongitude();
			_LatLog[1] = (float) Loc[1].getLatitude();
			MoveDistance = (float) Dist(_LatLog[0], _LonLog[0], _LatLog[1], _LonLog[1]);
			if(MoveDistance > 2.1)
				MoveDistance = Loc[1].getSpeed();
			_preSpeed[0] = _preSpeed[1];	    				
			_preSpeed[1] = Loc[1].getSpeed();
			//save the first feature "MoveDistance", 
			//because the update rate is 1hz, so the speed equal to distance
			_Bearing[0] = _Bearing[1];
			_Bearing[1] = Loc[1].getBearing();
			BearingDiff = Math.abs(_Bearing[1]-_Bearing[0]);
			//save the second feature "BearingDiff"
			HeadingDiff = Math.abs(Orientation[0]- preOrientation[0]);	
	  		preOrientation[0] = Orientation[0];
	  		preOrientation[1] = Orientation[1];
	  		preOrientation[2] = Orientation[2];
	  		//save the third feature "HeadingDiff"
	  		
			// Store raw data first
			/*try
			{
				fw_RawData.write(Loc[1].getLatitude()+","+Loc[1].getLongitude()+","+Loc[1].getSpeed()+","+Loc[1].getBearing()+","+Orientation[0]+","+MoveDistance+"\n");
			} catch (IOException e)
			{
				e.printStackTrace();
			}*/
	  		return 0;
  		}	
  	}

  	private int Classify(float MD, float Bd, float Hd, float Speed) // MD = MoveDistance, Bd = BearingDiff, Hd = HeadingDiff
  	{
  	      int states = 0;

  	        //if(Math.abs(MD) > 0.4 && Speed > 0.4)
  	      	if(Math.abs(MD) > 0.4 || Speed > 0.4)
  	        {
  	            if(Math.abs(Bd) > 10 && Math.abs(Hd) > 7)
  	            {
  	                states = 2;
  	            }
  	            else
  	            {
  	            	if(Math.abs(Hd) > 7 )
  	  	            {
  	  	                states = 4;
  	  	            }
  	            	else
  	            		states = 3;
  	            }
  	        }
  	        else
  	        {
	  	      	if(Math.abs(Hd) > 5)
		        {
		            states = 1;
		        }
	          	else
	          		states = 0;
  	        }
  	    return states;
  	}
  	
  	private int StateCrossCheck(int CS)
  	{

  	    int PS = 0;//Predicted next state
  	    int i,which;
  	    float MPr;// Max probability of next state


  	    MPr = 0;
  	    PS = states[0]; /////////////////////////////////////////focus//////
  	    
  	    for(i=0; i<5; i++)
  	    {
  	        if(MasterController.PBTPM[states[0]][states[1]][i] > MPr)
  	        {
  	            MPr = MasterController.PBTPM[states[0]][states[1]][i];
  	            PS = i;
  	        }
  	    }
  	    if(MPr <= 0)
  	        PS = INVALID_STATE;

  	    //2. Compare PS and CS
  	    //  Iniialize which = 0;
  	    // First, check if CS is a valid state
  	    if(CS == PS)
  	    {
  	        CS_count = 0;// Reset the counter
  	    }
  	    else
  	    {// CS is a valid state. Now, check if CS is equal to PS?
  	        if(PS != INVALID_STATE)
	  	    	if(CS_count < POSTURE_THRESHOLD) 
	  	        {
	  	        	CS_count++;
	  	        	CS = PS; // Trust Predicted States
	  	        }
	  	        else
	  	        {
	  	            //CS_count = 0;// Reset the counter
	  	            //Trust CS
	  	        }
  	        else
  	        {
  	        	//Trust CS
  	        }
  	     }
  	    return CS;
  	}

  	
  	
  	

	/*********************************ALGORITHM*************************************************************************************************************************/
	private void ReadMat()
  	{
//		OptTransMatrix = MasterController.PBTPM;
//		Log.i("omniguider","test"+OptTransMatrix[0][0][0]+"ff"+OptTransMatrix[0][0][1]);
  	}
  	
	private void WroteFile() {
		String directoryName = Environment.getExternalStorageDirectory().toString()+"/OmniGuider/Logdata";
		File directory = new File(directoryName);
		if(!directory.isDirectory())
		{
			if(!directory.mkdir())
			{
					
			}
		}
		File f_RawData = new File(strReadPath + "/Omniguider/Logdata/"+getTime()+"RawData.txt");
		/*File f_RawState = new File(strReadPath + "/Omniguider/Logdata/"+getTime()+"RawState.txt");
		File f_FinalState = new File(strReadPath + "/Omniguider/Logdata/"+getTime()+"FinalState.txt");*/
		try 
		{
			if(f_RawData.createNewFile())
			{
				fw_RawData = new FileWriter(f_RawData);
				fw_RawData.write("Time,Latitude,Longitude,Speed,Bearing,Heading\n");
			}
			/*if(f_RawState.createNewFile())
			{
				fw_RawState = new FileWriter(f_RawState);
			}
			if(f_FinalState.createNewFile())
			{
				fw_FinalState = new FileWriter(f_FinalState);
			}*/
			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
  	private String getTime()
  	{
  		String time;
    	Time t = new Time();
    	t.setToNow();
    	time= "D"+t.month+t.monthDay +"_"+"T"+t.hour+t.minute;
  		return time;
  	}
  	private String getaccurayTime()
  	{
  		String time;
    	Time t = new Time();
    	t.setToNow();
    	time= "D"+t.month+"."+t.monthDay +"_"+"T"+t.hour+"."+t.minute+"."+t.second;
  		return time;
  	}
  	
/**********************************************************************************
   Calculate entropy function and valuable

**********************************************************************************/

	public String getBehavior()
	{
		String status;
    	if(bCheckPose)
    	{
			switch(states[1])
	    	{
	    		case 0:
	    			status = "原地靜止";
	    			break;
	    		case 1:
	    			status = "原地旋轉";
	    			break;
	    		case 2:
	    			status = "轉彎";
	    			break;
	    		case 3:
	    			status = "移動時觀看興趣點";
	    			break;
	    		case 4:
	    			status = "移動搜尋";
	    			break;
	    		default:
	    			status = "Perceiving";
	    			break;
	    	}
			
			return status;
    	}
    	else
    		return "Invalid Pose";

	}
	public int getintBehavior() {
    	if(bCheckPose)
    	{	
    		try 
    		{
				fw_RawData.write("Time : "+getaccurayTime()+", status : "+states[1]+"\n");
			}
    		catch (IOException e)
			{
    			e.printStackTrace();
			}
    		return states[1];
    	}
    	else
    		return 100;

	}	
}
