package Controller;

import java.io.File;
import java.io.IOException;

import GlobalSetting.MasterController;
import Model.BehaviorPerceive;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;


public class BehaviorController implements Runnable{
	//private Handler handler = new Handler();
	private BehaviorPerceive behaviorperceive = new BehaviorPerceive();
	
	private Location UserLocation;
    private float[] UserOrientation = new float[3];
	private String UserBehavior;
    //private static Location tempLoc;
	private static boolean IsStatus = false;
	private static int IsResult = 2;
	private static int IsLocation = 2;
	private static boolean killRun = false;
	private int interaction = 0;// 0 = normal; 1 = Disappear; 2 Focus Detail; 3 Focus Direction 4 Aggregation

	private int Directionkey = 55;
	
	private final Handler mhandler;

	private String strReadPath;
	//private FileWriter fw_Interaction;
	public BehaviorController(Handler h)
	{	// TODO Auto-generated constructor stub
		strReadPath = Environment .getExternalStorageDirectory ().getPath();
		//String directoryName = Environment.getExternalStorageDirectory().toString()+"/OmniGuider/Logdata";
		//File f_Interaction = new File(strReadPath + "/Omniguider/Logdata/"+getTime()+"Interaction.txt");
		/*try 
		{
			if(f_Interaction.createNewFile())
			{
				 fw_Interaction = new FileWriter(f_Interaction);
			}
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/
		mhandler = h;
		behaviorperceive.InitialPerceive();
	}
	public void setStatus(boolean status)
	{
		IsStatus = status;
	}
	public void setResult(int result)
	{
		IsResult = result;
	}
	public void KillRun()
	{
		killRun = true;
		behaviorperceive.EndPerceive();	
		/*try
		{
			fw_Interaction.flush();
			fw_Interaction.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		mhandler.removeCallbacks(this);
	}
	public void RestartRun()
	{
		if(killRun == true)
		{
			killRun = false;
			behaviorperceive.InitialPerceive();
			run();	
		}
	}
	public void StopService()
	{
		behaviorperceive.EndPerceive();		
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
        //log�ثe�ɶ�
		if(!killRun)
	    	//handler.postDelayed(this, 1000);
			mhandler.postDelayed(this, 1000);
			//check IsLocation
			IsStatus = true;

	    	UserOrientation = MasterController.SENSORService.getOrientation();
	    	behaviorperceive.setGPS(UserLocation);	
	    	behaviorperceive.setOrientation(UserOrientation);
	    	behaviorperceive.StartPerceive();
	    	UserBehavior = behaviorperceive.getBehavior();
	    	//Log.i("omniguider","test" + UserBehavior);

	    	interaction = AdaptiveInteraction(behaviorperceive.getintBehavior());

	    	Message message = new Message();
	    	switch(interaction)
			{	
				case 0:
		    		
	                message.what = 0; 
	                mhandler.sendMessage(message);

	                //View.POIView.setInteraction("Aggregation",4);
	                break;
				case 1:
		    		
	                message.what = 1; 
	                mhandler.sendMessage(message);

	                //View.POIView.setInteraction("Aggregation",4);
	                break;
				case 2:
		    		//if(!bInteracting)
		    		//{
		                message.what = 2; 
		                mhandler.sendMessage(message);

						Log.i("Omniguider","interaction"+bInteracting);
		                //View.POIView.setInteraction("Aggregation",4);
			    	//}
					break;
	                
				case 3:
		    		//if(!bInteracting)
		    		//{
		                message.what = 3; 
		                Bundle data = new Bundle();
		                data.putInt("Directionkey", Directionkey);
		                message.setData(data);
		                mhandler.sendMessage(message);

						Log.i("Omniguider","interaction"+bInteracting);
		                //View.POIView.setInteraction("Aggregation",4);
		    		//}
					break;
				case 4:
	                message.what = 4; 
	                mhandler.sendMessage(message);

					break;
				case 100:
					
	                message.what = 100; 
	                mhandler.sendMessage(message);

					break;
			}

	}

	public int IsLocation()
	{
		return IsLocation;
	}
	public String getBehavior()
	{
		return UserBehavior;
	}
	public boolean getStatus()
	{
		return IsStatus;
	}
	public String getInteraction()
	{
		switch(interaction)
		{
			case 0:
				return "Normal";
			case 1:
				return "Disappear";
			case 2:
				return "Stationary Focus";
			case 3:
				return "Moving Focus";
			case 4:
				return "Aggregation";
			default:
				return "Normal";
		}
	}

	
	/**Adaptive Interaction based on Perceive*****************************************/
	public boolean bInteracting = false;
	private int FocusDetailCount = 0;
	private int FocusDirectionCount = 0;
	private int Result = 0;
	private int AdaptiveInteraction(int behavior)
	{
		
		if(behavior == 100)
		{
			// invalid Pose
			Result = 100;
			return Result;
						
		}else
		{
			if(!bInteracting)
			{
				// meaning is not interacting with user
				switch(behavior)
				{
					case 0:
						FocusDirectionCount = 0;
						if(FocusDetailCount>=3)
						{
							Result = 2;//Focus Detail
							bInteracting = true;
							FocusDetailCount = 0;
						}

						else
						{
							FocusDetailCount=0;
							Result = 0;
						}
						break;
					case 1:
						FocusDirectionCount = 0;
						FocusDetailCount = 0;
						Result = 0; // Normal
						break;
					case 2:
						FocusDirectionCount = 0;
						FocusDetailCount = 0;
						Result = 1; // Disappear
						break;
					case 3:
						FocusDetailCount = 0;
						if(FocusDirectionCount>=3)
						{
							Result = 3;//Focus Direction
							bInteracting = true;
							FocusDirectionCount = 0;
						}
						else
						{
							FocusDirectionCount++;
							Result = 0;
						}
						break;
					case 4:
						FocusDetailCount = 0;
						FocusDirectionCount = 0;
						Result = 4; //Aggregation
						break;
				}
	    		//try/***********************************************************************************************/ 
	    		//{
				//	fw_Interaction.write(Result+",");
				//}
	    		//catch (IOException e)
				//{
	    		//	e.printStackTrace();
				//}/***********************************************************************************************/
				return Result;
			}
			else
			{
				//Result = 0;
				//try/***********************************************************************************************/ 
	    		//{
				//	fw_Interaction.write("9"+",");
				//}
	    		//catch (IOException e)
				//{
	    		//	e.printStackTrace();
				//}/***********************************************************************************************/
				
				return Result;
			}
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
}
