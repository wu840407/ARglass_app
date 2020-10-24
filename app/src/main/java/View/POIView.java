/**
 * 《函式庫、類別庫》
 * 景點與使用者方位
 */

package View;

import java.util.ArrayList;

import com.ccit.arglass2.R;
import GlobalSetting.GeoUtils;
import GlobalSetting.MasterController;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static GlobalSetting.MasterController.POI;
import static View.RadarView.DB;
import static com.ccit.arglass2.ARActivity.latitude;
import static com.ccit.arglass2.ARActivity.longitude;

public class POIView extends View
{	
/************************************************************************/
// 1. Private local variables
/************************************************************************/
    private Paint mGridPaint;
	private Path pathTriangle;	//箭頭的三角形
	private Paint mArrowPaint;	//箭頭用的畫筆
	private Paint mDistancePaint;
	//private Paint mRadarPaint;	//雷達畫筆	
	private Paint mTitlePaint;	//景點標題畫筆
	private Paint mBanarPaint;
	private Paint mBanarbackPaint; // banar background
	private Paint mTextPaint;   //文字背景
	private Paint mDescriptionPaint;
	
	
	private Paint mBehaviorPaint;
	private Paint mBehaviorbackPaint;
	//private Paint mMessagePaint;

/************************************************************************/	
	private double dRangeforMove=0.00900f;  //500M 移動距離
/************************************************************************/
	private int iHeight, iCenterHeight;
	private int iWidth, iCenterWidth;

	private boolean bInitVariable; //初始化
	private float fIconWidth;
	private float fIconHeight;
	private float fTitleY;
	private float fIconLeft;
	private float fIconRight;
	private float fIconTop;
	private float fIconBot;
	//private ProgressDialog gpsDialog;
	//private RectF mScanRect;
	/**投影座標*/
	private float fPoiYPosition;
	private float fPoiXPosition; //景點到手機畫面的投影位置
 	private double dRatioDeep;
	private double dRadarDeep;
	//private String[] tmp;
	//private String buff1="";
	private RectF showPicrada;
	private RectF showPicProvider;
	//private RectF showPicBanner4;
	private double dBearingToTarget1;
	private double dBearingToTarget;
	private Matrix mMatrix = new Matrix();

	private Paint mBanarPaint1;
	
	public static String getPOIVersion="1.0.33";


	// Http controller set value to under value
	
	
	// GPS Service set value to under value
	private double LocationLatRecord;
	private double LocationLngRecord;

	
	//Store POI data 
	private double[] mBearing = new double[150];
	private double[] mDistance = new double[150]; 
	private int[] iconSize = new int[150];
	private int[][] sortDistance = new int [150][3];
	private String[] unit = new  String[150];
	
	private Bitmap[] icon = new  Bitmap[150];
	
	private double POIdisMax;
	private int setRange;
	//private int[] sDistance;
	//for mOrientation

	private static boolean HasPOI = false;
	public static boolean hideBehavior = false;
	public static float HoriOrientation;
	public static float VerOrientation;
	private static int POInumber;

	private static double CurrentLat , CurrentLng;
	private static String nowProvider;
	public static String StartPerceiveTime="00:00";
	/*******************************************************************************************/
	private static int POInumberofFOV;
	private static String[][] POIofFOV;
	private static String[][] POIofAggregation;
	private static int POInumberofAggregation;
	private static int count = 0; // 降低sensor update rate
	private static int count_for_avg = 0;//用來平均水平的sensor data.
	private static float tempavg = 0;//儲存平均sensor data
	//Passing value interface passing is not good.
	
	private int POInumCheck = 0; // 測試個數小於POIlimited
	private final int POIlimited = 7;
	private int ISdrawLine = 0; // 1 TIME 1 LIME
	
	
	private static String strBehavior;
	private static String strInteraction;
	private static boolean bBehaviorStatus;
	private static int intInteraction;

	public synchronized static void setPOInum(int poinumber)
	{
		POInumber = poinumber;
		Log.i("omniguider","0908 TESTing = "+POInumber);
	}
	public synchronized static void setPOInumofFOV(int poinumberofFOV)
	{
		POInumberofFOV = poinumberofFOV;
	}
    public synchronized static void setPOI(String[][] poi)
    {
    	POI = poi;
    }
    public synchronized static void setPOIofFOV(String[][] poiofFOV)
    {
    	POIofFOV = POI;
    }
	public synchronized static void setPOInumofAggregation(int poinumberofAggregation)
	{
		 POInumberofAggregation = poinumberofAggregation;
	}
    public static void setAggregationPOI(String[][] poi)
    {
    	POIofAggregation = poi;
    }
    
	public synchronized static void setHoriOrientation(float orientation) // in POIView中，先行判斷狀態為何，根據狀態來決定sensor rate
    {
		// if(UserState == 靜止(純GPS時))
		// {都做	}
		// else{下面
		//		}
		//if (count == 5 || count == 10 || count == 15 || count == 20) // 5秒錯一次
		if (true ) // for samsung
		{
			//這個機制應該用再行走上，人沒有移動的時候正常更新//   //應該移到sensor部分處理  ，不然在0度跟360度那時候會出問題 (有些0 有些35X造成平均錯誤)
			count_for_avg++;
			tempavg = tempavg + orientation;
			if(count_for_avg == 10)
			{
				count_for_avg = 0;
				HoriOrientation = tempavg / 10;
				tempavg = 0;
			}						
			/**********************/
			
		}
		//HoriOrientation = orientation;	
    }
    public synchronized static void setVerOrientation(float orientation)
    {
		
    	if(count == 10) // 降低 sensor update rate
		//if(true) // for samsung
		{
			count = 0;
			if(Math.abs(orientation)<=5) //當垂直擺盪低於十度內都視為0 降低jitter
				VerOrientation = 0;
			else
				VerOrientation = orientation;
		}
    	count++;
    	
    }
    public static void setLat(double lat)
    {
    	CurrentLat = lat;
    }
    public static void setLng(double lng)
    {
    	CurrentLng = lng;
    }

    public static void setProvider(String provider)
    {
    	nowProvider = provider;
    }

    public static void setInteraction(String strinteracting, int intinteracting)
    {
    	if(hideBehavior == false)
    	{
    		strInteraction = strinteracting;
    		intInteraction = intinteracting;
    	}
    	else if(hideBehavior == true)
    	{
    		strInteraction = "disable behavior";
    		intInteraction = 0;
    	}
    }
    public static void setBehavior(String behavior)
    {
    	if(hideBehavior == false)
    	{
    		strBehavior = behavior;
    	}
		else if(hideBehavior == true)
    	{
			strBehavior = "disable behavior";
    	}
    }
    public static void setbBehaviorStatus(boolean status)
    {
    	bBehaviorStatus = status;
    }
    /********************************************************************************************/
	public POIView(Context context, int DeviceWidth, int DeviceHeight)  
    {
        this(context, DeviceWidth, DeviceHeight,null);
    }
    
    public POIView(Context context, int DeviceWidth, int DeviceHeight, AttributeSet attrs) 
    {
        this(context, DeviceWidth, DeviceHeight, attrs, 0);
    }
    
    public POIView(Context context, int DeviceWidth, int DeviceHeight, AttributeSet attrs, int defStyle) 
    {
        
    	super(context, attrs, defStyle);
        
        this.setKeepScreenOn(true);   

        //景點距離畫筆
        mDistancePaint = new Paint();
        mDistancePaint.setColor(Color.WHITE);
        mDistancePaint.setStyle(Style.FILL_AND_STROKE);
        mDistancePaint.setStrokeWidth(0f);
        mDistancePaint.setTextSize(DeviceWidth/30.0f);
        mDistancePaint.setTextAlign(Align.RIGHT);
        
        //景點標題畫筆
        mTitlePaint = new Paint();
        mTitlePaint.setColor(Color.WHITE);
        mTitlePaint.setStyle(Style.FILL_AND_STROKE);
        mTitlePaint.setStrokeWidth(0f);
        mTitlePaint.setTextSize(DeviceWidth/50.0f);
        mTitlePaint.setTextAlign(Align.CENTER);

        //景點描述畫筆
        mDescriptionPaint = new Paint();
        mDescriptionPaint.setColor(Color.WHITE);
        mDescriptionPaint.setStyle(Style.FILL_AND_STROKE);
        mDescriptionPaint.setStrokeWidth(0f);
        mDescriptionPaint.setTextSize(DeviceWidth/40.0f);
        mDescriptionPaint.setTextAlign(Align.LEFT);
        
        //scene mid-top title
        mBanarPaint = new Paint();
        mBanarPaint.setColor(Color.RED);
        mBanarPaint.setStyle(Style.FILL_AND_STROKE);
        mBanarPaint.setStrokeWidth(1);      
        mBanarPaint.setTextSize(DeviceWidth/30.0f);
        mBanarPaint.setTextAlign(Align.CENTER);

        // banar背景畫筆
        mBanarbackPaint = new Paint();
        mBanarbackPaint.setColor(Color.WHITE);
        mBanarbackPaint.setAlpha(150);
        mBanarbackPaint.setStyle(Style.FILL);
        
        
        mBanarPaint1 = new Paint();
        mBanarPaint1.setColor(Color.BLUE);
        mBanarPaint1.setStyle(Style.FILL_AND_STROKE);
        mBanarPaint1.setStrokeWidth(0);      
        mBanarPaint1.setTextSize(DeviceWidth/40.0f);

        mBanarPaint1.setTextAlign(Align.CENTER);
       
        //中間指向POI箭頭的畫筆
        mArrowPaint = new Paint();
        mArrowPaint.setAntiAlias(true);
        mArrowPaint.setColor(Color.RED);
        mArrowPaint.setStyle(Style.FILL);
      
                
        // 中間瞄準器的畫筆
        mGridPaint = new Paint();
        mGridPaint.setColor(0xFF00FF00);
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setStrokeWidth(1.5f);
        mGridPaint.setTextSize(10.0f);
        mGridPaint.setTextAlign(Align.CENTER);

        // 文字背景畫筆
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAlpha(150);
        mTextPaint.setStyle(Style.FILL);

        mBehaviorPaint = new Paint();
        mBehaviorPaint.setColor(Color.RED);
        mBehaviorPaint.setStyle(Style.FILL_AND_STROKE);
        mBehaviorPaint.setTextSize(DeviceWidth/35.0f);
        mBehaviorPaint.setTextAlign(Align.LEFT);
        mBehaviorPaint.setStrokeWidth(1f);
        
        mBehaviorbackPaint = new Paint();
        mBehaviorbackPaint.setColor(Color.WHITE);
        mBehaviorbackPaint.setAlpha(150);
        mBehaviorbackPaint.setStyle(Style.FILL);
        // 文字畫筆
//        mMessagePaint = new Paint();
//        mMessagePaint.setColor(Color.WHITE);
//        mMessagePaint.setStyle(Style.FILL_AND_STROKE);
//        mMessagePaint.setTextAlign(Align.RIGHT);
//        mMessagePaint.setTextSize(20f);
//        mMessagePaint.setStrokeWidth(3f); // draw point not text
              
//        // 雷達畫筆
//        mRadarPaint = new Paint();
//        mRadarPaint.setColor(0xFF387096);
//        mRadarPaint.setAlpha(200);
//        mRadarPaint.setAntiAlias(true);
//        mRadarPaint.setStyle(Style.FILL);
//        mRadarPaint.setStrokeWidth(2.0f);
    }
  
    @Override
    protected void onDraw(Canvas canvas) 
    {
    	super.onDraw(canvas);
		//Log.i("Omniguider", "Ondraw 1");
		
    	if (!bInitVariable)//初始化
    	{   
			iWidth = getWidth();
	    	iHeight = getHeight();
	        iCenterHeight = iHeight / 4;
	        iCenterWidth = iWidth  / 2;
	        fTitleY=iCenterHeight*1.87375f;
	        showPicProvider= new RectF( iWidth*0.0f, iHeight*-0.05f,iWidth*0.15f,iHeight*0.20f);//Provider ICON 縮圖 的位置
	        showPicrada = new RectF(iWidth*0.95f-iHeight*0.15f, iHeight*0.13f-iHeight*0.15f,iWidth*0.95f+iHeight*0.15f,iHeight*0.13f+iHeight*0.15f);//rada
	        bInitVariable=true;
        }
    	if(bBehaviorStatus)
    	{
    		canvas.drawRect(iWidth*0.0f, iHeight*0.50f, iWidth*0.20f,iHeight*0.70f,mBehaviorbackPaint);
    		drawMultilineText(strBehavior+"\n"+strInteraction+"\n"+hideBehavior+"\n"+StartPerceiveTime, iWidth*0.0f, iHeight*0.6f, mBehaviorPaint, canvas);
    	}

    	if( longitude==0 || latitude==0)
		{
			switch(MasterController.iLanguage)
			{
				case 0:
					canvas.drawText("No POI available in the current range.", iWidth*0.50f, iHeight*0.5f, mTitlePaint);
					break;
				case 1:
					canvas.drawText("目前範圍內沒有景點", iWidth*0.50f, iHeight*0.5f, mTitlePaint);
					break;
			}
		}
    	else
    	{
    		if (longitude!=0 || latitude!=0) // GPS is received.  check from gps controller  // actually this is POIUview's controller
            {
            		DefinePOItype(POIofAggregation , POInumberofAggregation);
            		//1.  畫雷達 底圖
             		//canvas.drawBitmap(((BitmapDrawable)getResources().getDrawable(R.drawable.rada)).getBitmap(), null, showPicrada, mRadarPaint);
            		//2.  畫Provider 是誰 穩不穩定

             		try
            		{
            			updateDistance(POIofAggregation , POInumberofAggregation); // 計算每個POI的距離資料以及深度資料
            		}
            		catch(Exception e)
            		{
            		}
            		//    	//====0810smallthree，如果資料抓取完畢，就不用在判別type====
             		
             		//for (int i=POInumber-1; i>-1; i--)
             		for (int i=0 ; i< POInumberofAggregation ; i++)
            		{
            			if( POInumberofAggregation == 0)
            			{
            				dBearingToTarget = mBearing[sortDistance[i][0]] - HoriOrientation;
            				fPoiXPosition = (float) (iCenterWidth + (dBearingToTarget /35.359 * iCenterWidth));
            				fPoiYPosition = (float) (iHeight*0.55f - VerOrientation*4);
            				
            			}
            			else
                	    {
            				dBearingToTarget = mBearing[sortDistance[i][0]] - HoriOrientation;
            				fPoiXPosition = (float) (iCenterWidth + (dBearingToTarget /35.359 * iCenterWidth));
            				fPoiYPosition = (float) (iHeight*0.55f - (mDistance[sortDistance[i][0]]-mDistance[sortDistance[0][0]])/dRatioDeep - VerOrientation*4);
                	    }
         			
            			
            			//Log.i("Omniguider","Number  "+ i +" degree : " + mBearing[sortDistance[i][0]]);
            			// mBearing = 0~360
            			updateIconSize(sortDistance[i][0]); // 計算此POI icon的大小    		        
            			clickPOI(canvas,i,POIofAggregation);
    		   			//0708smallthree，距離在設定的顯示範圍為1 顯示圖示
    		            showPOIonrange(canvas,i,POIofAggregation);
            		}
             		POInumCheck = 0;// initial check個數
            		ISdrawLine = 0;
            		
//               		MasterController.iFocusPOIName = "0";
//               		MasterController.iFocusPOIInfo = "0";
             		//0708smallthree，畫出對準POI的線
            		//DrawAimLine(canvas)
            }
    	}
        //畫圖結束
        postInvalidate();
    	
    	
    }
    

  //POI圖示分類
    public void POItoIconType(int POInum) 
    {

    }
    private synchronized void updateDistance(String[][] POI , int POINumber) 
    {

    	//int test = 0; // using to count number of poi , > 7 ,set invisiable 
    	for (int i=0; i<POINumber; i++)
    	{
    		mDistance[i] = GeoUtils.distanceKm(CurrentLat, CurrentLng, Double.parseDouble(POI[i][1]), Double.parseDouble(POI[i][2]));
    		mBearing[i] = GeoUtils.bearing(CurrentLat, CurrentLng, Double.parseDouble(POI[i][1]), Double.parseDouble(POI[i][2]));
    		//if((int)(mDistance[i]*1000)<setRange)
    		//{
    				sortDistance[i][1]=1;    // sortDistance[i][1] = 1 visiable
    		//}
    		//else
    		//{
    		//	sortDistance[i][1]=0;	// sortDistance[i][1] = 0 invisiable
    		//}
			switch(MasterController.iLanguage)
			{
				case 0:
					unit[i]="距離"+Integer.toString((int)(mDistance[i]*1000))+"m";  
					break;
				case 1:
					unit[i]="Distance "+Integer.toString((int)(mDistance[i]*1000))+"m";  
					break;
			}
    	}
    	if(POINumber>=1)
    	{
    		updateDeep(POI,POINumber);
    	}
    }
    private void updateDeep(String[][] POI , int POINumber)
	{
    	//Log.i("Omniguider", "6  updateDepp");
    	
    	//Global.sumofdatabase = Global.NUM;
    	for (int i=0; i<POINumber; i++)
    	{
    		sortDistance[i][0]=i;
    	}
    	//====
    	//test: int i=1 -> int i=0
    	//====
        for (int i = 1; i<POINumber; i++)
        {
        	int tmpi=0;
     	   	int tmpi1=0;
     	   	int tmpi2=0;
     	   	for(int j=i; j>0; j--)
     	   	{
     	   		if(mDistance[sortDistance[j][0]] < mDistance[sortDistance[j-1][0]])
     	   		{
     	   			tmpi = sortDistance[j][0];
     	   			tmpi1 = sortDistance[j][1];
     	   			tmpi2 = sortDistance[j][2];
     	   			sortDistance[j][0] = sortDistance[j-1][0];
     	   			sortDistance[j][1] = sortDistance[j-1][1];
     	   			sortDistance[j][2] = sortDistance[j-1][2];
     	   			sortDistance[j-1][0] = tmpi;
     	   			sortDistance[j-1][1] = tmpi1;
     	   			sortDistance[j-1][2] = tmpi2;
     	   		}
     	   		else break;
     	   	}
        }
        //===========0823smallthree，刪除
        for (int i=1; i<POINumber; i++)
        {
        	sortDistance[i][2]=Integer.parseInt(POI[sortDistance[i][0]][4])*10;
        }
        //找出POI Data內的最大值
        POIdisMax = mDistance[sortDistance[POINumber-1][0]];
        dRatioDeep = (mDistance[sortDistance[POINumber-1][0]] -mDistance[sortDistance[0][0]])/ (iHeight*0.3);//5
        dRadarDeep = (mDistance[sortDistance[POINumber-1][0]] -mDistance[sortDistance[0][0]])/ (iHeight*0.04f); //6  
	    //MasterController.initPOI=true;
	} 
    
    private void DefinePOItype(String[][] POI , int POINumber)  //這個要在view底下做
    {
    	//Log.i("Omniguider", "10  DrawAimLine");
    	

       		//Log.i("omniguider","!=4");
	    	for (int i =0 ; i < POINumber; i++)
	    	{
	    		//Log.i("Omniguider", "type number: "+POIofFOV[i][4]);
	    		//Global.showfront[i] = false;
	    		
	    		if(POI[i][4].equals("1"))
	    		{
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_01_25)).getBitmap();
	       			sortDistance[i][2]=10;
	    		}
	    		else if(POI[i][4].equals("2"))
	    		{
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_02_25)).getBitmap();
	       			sortDistance[i][2]=20;
	    		}
	    		else if(POI[i][4].equals("3"))
	    		{
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_03_25)).getBitmap();
	       			sortDistance[i][2]=30;
	    		}
	    		else if(POI[i][4].equals("4"))
	    		{
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_04_25)).getBitmap();
	       			sortDistance[i][2]=40;
	    		}
	    		else if(POI[i][4].equals("5"))
	    		{
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_05_25)).getBitmap();
	       			sortDistance[i][2]=50;
	    		}
	    		else if(POI[i][4].equals("6"))
	    		{
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_06_25)).getBitmap();
	       			sortDistance[i][2]=60;
	    		}
	    		else if(POI[i][4].equals("7"))
	    		{
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_07_25)).getBitmap();
	       			sortDistance[i][2]=70;
	    		}
	    		else if(POI[i][4].equals("8"))
	    		{
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_08_25)).getBitmap();
	       			sortDistance[i][2]=80;
	    		}
	    		else if(POI[i][4].equals("9"))
	    		{
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_09_25)).getBitmap();
	       			sortDistance[i][2]=90;
	    		}
				else if(POI[i][4].equals("10"))
				{
					icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.poinumber_5)).getBitmap();
					sortDistance[i][2]=100;
				}
				else if(POI[i][4].equals("11"))
				{
					icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.poinumber_1)).getBitmap();
					sortDistance[i][2]=110;
				}
				else if(POI[i][4].equals("111"))
				{

				}
	    		else
	    		{ 
	    			icon[i] = ((BitmapDrawable)getResources().getDrawable(R.drawable.function_theme_icon_10_25)).getBitmap();
	       			sortDistance[i][2]=0;
	    		}
	    	}

    }


	//upper is using sqlite to get stored data
	/*
	 * 
	 * Need to accroding iconsize to adaptive POIicon size in screen
	 * 
	 * */
	private  void updateIconSize(int i) 
    {
		//Log.i("Omniguider", "4  updateIconSize");
    	
		if(DB[i][0] < 100)
			iconSize[i] = (int) (96 - (DB[i][0])*3);
		else if(DB[i][0] >100 && DB[i][0] <105)
			iconSize[i] = 97;
		else if(DB[i][0] >105 && DB[i][0] <110)
			iconSize[i] = 80;
		else if(DB[i][0] >110 &&DB[i][0] <120)
			iconSize[i] = 60;
		else if(DB[i][0] >120 && DB[i][0] <130)
			iconSize[i] = 40;
		else if(DB[i][0] >130 && DB[i][0] <140)
			iconSize[i] =30;
		else
			iconSize[i] = 20;
    	fIconWidth =  (iconSize[i] /2f);
    	//Log.i("Omniguider","POIView iconsize : "+iconSize[i]);
    	fIconHeight= (iconSize[i] /2f);
    	fIconLeft = -fIconWidth;
    	fIconRight = fIconWidth;
    	fIconTop = -fIconHeight - (iHeight * 0.0625f);
    	fIconBot = fIconHeight - (iHeight * 0.0625f);
	}

    
    //0708smallthree，判斷按下POI的函式  //要再view做
    private void clickPOI(Canvas canvas,int i,String[][] POI)
    {
    	//Log.i("Omniguider", "7  clickPOI");
    	
    	  /***************************按到POI的事件 當按到POI會設定明亮度**************************************/
        if(fPoiXPosition >= MasterController.fTouchEventX - fIconWidth && fPoiXPosition <= MasterController.fTouchEventX + fIconWidth 
        && fPoiYPosition >= MasterController.fTouchEventY - fIconWidth + 40 && fPoiYPosition <= MasterController.fTouchEventY + fIconWidth +40
        && sortDistance[i][1] == 1 && (sortDistance[i][2] == MasterController.setType * 10 || MasterController.setType ==999) && MasterController.bTouchScreen == false)
        {
        	//MasterController.iTouchPOIID="";
        	//for test
        	mGridPaint.setAlpha(255);
        	
        	MasterController.iTouchPOIInfo = POI[sortDistance[i][0]][3];
        	MasterController.iTouchPOIName = POI[sortDistance[i][0]][1];
        	MasterController.iTouchPOIID = POI[sortDistance[i][0]][0];
        	//MasterController.POI_index = Integer.valueOf(POI[sortDistance[i][0]][0]);
        	MasterController.iTouchPOILat = POI[sortDistance[i][0]][1];
            MasterController.iTouchPOILog = POI[sortDistance[i][0]][2];
        	MasterController.bTouchScreen = true;
        	MasterController.fTouchEventX = 0;
        	MasterController.fTouchEventY = 0;
        	//Log.i("Omniguider","POIView iTouchPOIID :"+MasterController.iTouchPOIID);
        	//Log.i("Omniguider","POIView iTouchPOIName :"+MasterController.iTouchPOIName);
        	//Log.i("Omniguider","POIView iTouchPOIInfo :"+MasterController.iTouchPOIInfo);
        	
        	/*
        	 * This event is receive user touch event that is triggered. 
        	 * So, add the extend function to intent other activity to show the infowindows
        	 * 
        	 * */
        }
        else
        {
			mGridPaint.setAlpha(160);
        }
/***************************按到POI的事件 當按到POI會設定明亮度**************************************/  
    }
    

    
    private void showPOIonrange(Canvas canvas,int i,String[][] POI)
    {
    	//Log.i("Omniguider", "8  showPOIonrange");
    	
    	//地址顯示
    	//RectF showIcon = new RectF (fPoiXPosition+fIconLeft , fPoiYPosition+fIconTop, fPoiXPosition+fIconRight, fPoiYPosition+fIconBot);

    	if(mDistance[sortDistance[0][0]] < (int)(POIdisMax/2))
    		setRange = (int)(POIdisMax*1000/2);
    	else
  			setRange = (int)(mDistance[sortDistance[0][0]]+1)*1000;
 	
    	//if(sortDistance[i][1]==1)
		if(sortDistance[i][1]==1 && dBearingToTarget < 20 && dBearingToTarget > -20 && POInumCheck <POIlimited) // 視野內且個數小於7	 this 7 must be using value parameters to let user control    	
		{
			
    		//顯示的POI type ※999為全部顯示
    		if(MasterController.setType==999)
    		{
    			POInumCheck++; // maybe can be filtered by poitype in poicontroller nor here
         		//圖片會隨著相機角度變換圖片角度
    			if(intInteraction == 4)
    			{
    				mMatrix.setScale(100f/icon[sortDistance[i][0]].getWidth(),100f/icon[sortDistance[i][0]].getHeight());  
             		mMatrix.postTranslate(fPoiXPosition+fIconLeft, fPoiYPosition+fIconTop);  
             		mMatrix.postSkew(0, (float)dBearingToTarget/100, fPoiXPosition, fPoiYPosition);
             		Log.i("Omniguider","aggre height"+ String.valueOf(100f/icon[sortDistance[i][0]].getHeight()));
    			}//3.0
    			else
    			{
    				mMatrix.setScale(100f/icon[sortDistance[i][0]].getWidth(),100f/icon[sortDistance[i][0]].getHeight());  
             		mMatrix.postTranslate(fPoiXPosition+fIconLeft, fPoiYPosition+fIconTop);  
             		mMatrix.postSkew(0, (float)dBearingToTarget/100, fPoiXPosition, fPoiYPosition);
             		
             		//Log.i("Omniguider","Height="+ String.valueOf(60f/icon[sortDistance[i][0]].getHeight()));
    			}//0.48
    			
    			//對焦&觸碰時，在雷達圖上表示此POI的點會變色(紅為點選)
         		if(POI[sortDistance[i][0]][0] == MasterController.iTouchPOIID)
         		{
         			//mGridPaint.setAlpha(150);
         			//for test 
        			mGridPaint.setAlpha(255);
        			//mMessagePaint.setColor(Color.RED);
         		}
         		else
         		{	
        			//mGridPaint.setAlpha(180);
         			//fortest
         			mGridPaint.setAlpha(160);
         			//mMessagePaint.setColor(Color.WHITE);
         		}
         		//畫POI的圖 , add poi iconsize to adaptive POIIcon size in screen
         		try
         		{
         			canvas.drawBitmap(icon[sortDistance[i][0]], mMatrix, mGridPaint);
         			//Log.i("Omniguider","Draw Icon");
         		}
         		catch(Exception ex)
        		{
        			ex.printStackTrace();
        		}

         		//景點名稱 title upon Icon in scene
         		canvas.drawText(POI[sortDistance[i][0]][0], fPoiXPosition+fIconLeft+40, fPoiYPosition+fIconTop-10, mTitlePaint);
         		
         		//0512 draw POI title and Line and info by "target" ↓↓↓//
        		dBearingToTarget1 = mBearing[sortDistance[i][0]] - HoriOrientation;
        		if (-3< dBearingToTarget1 & dBearingToTarget1<3 && ISdrawLine == 0) // testline = 0 means no target, testline = 1 means find the 1targets so only draw one arrow
        		{   ISdrawLine = 1;
        			//mGridPaint.setAlpha(255);
	                // for test
	                //距離在設定的顯示範圍為1 顯示圖示		
            				canvas.drawText(POI[sortDistance[i][0]][0],iWidth*0.5f,fTitleY*0.15f,mBanarPaint);
            				canvas.drawRect(iWidth*0.30f, fTitleY*0.0f, iWidth*0.70f,fTitleY*0.2f,mBanarbackPaint);
            				//MasterController.POI_index = Integer.valueOf(POI[sortDistance[i][0]][4]);
                       		MasterController.iFocusPOIName = POI[sortDistance[i][0]][0];
                       		MasterController.iFocusPOIInfo = POI[sortDistance[i][0]][3];
            				MasterController.SetPOItype = POI[sortDistance[i][0]][4];
            				//Log.i("omniguider","whyyyyyy:"+MasterController.iFocusPOIName+"\n+"+MasterController.iFocusPOIInfo);
            				//boolean bPOIfocusNodata;
            				//如果是商家而沒有促銷資料或
//            					if(POIofFOV[sortDistance[i][0]][4].equals("1")||POIofFOV[sortDistance[i][0]][4].equals("2")||POIofFOV[sortDistance[i][0]][4].equals("3")||POIofFOV[sortDistance[i][0]][4].equals("7"))
//            					{
//            						switch(MasterController.iLanguage)
//            						{
//            							case 0:
//            								canvas.drawText("尚無促銷資訊!!",iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
//            								
//            								break;
//            							case 1:
//            								canvas.drawText("No Special Offer yet.",iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
//            								//canvas.drawText(POI[i][4],iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
//            								//POI[I][4] = poi Detail 資料
//            								break;
//    	        		    		}
//            						
//    	        		        }		
            					//中間瞄準器開始
            					mArrowPaint.setAlpha(80);
            					pathTriangle = new Path();	        		        
            					pathTriangle.moveTo(fPoiXPosition, fPoiYPosition-iHeight*0.0125f);
            					pathTriangle.lineTo(fPoiXPosition-(iCenterWidth*0.1f*0.5f), fPoiYPosition*1.2f*0.98f);
            					pathTriangle.lineTo(fPoiXPosition-(iCenterWidth*0.1f/4), fPoiYPosition*1.2f*0.98f);
            					pathTriangle.lineTo(iCenterWidth*0.975f, iHeight*0.89f-MasterController.typeButtonhigh);
            					pathTriangle.lineTo(iCenterWidth*1.025f, iHeight*0.89f-MasterController.typeButtonhigh);
            					pathTriangle.lineTo(fPoiXPosition+(iCenterWidth*0.1f/4),fPoiYPosition*1.2f*0.98f);
            					pathTriangle.lineTo(fPoiXPosition+(iCenterWidth*0.1f*0.5f), fPoiYPosition*1.2f*0.98f);
            					canvas.drawPath(pathTriangle, mArrowPaint);
            						
            					canvas.drawRect(iWidth*0.40f, iHeight*0.7f-MasterController.typeButtonhigh, iWidth,iHeight-MasterController.typeButtonhigh,mTextPaint);	
            					//canvas.drawText(POIofFOV[sortDistance[i][0]][1], iWidth*0.40f, iHeight*0.75f-MasterController.typeButtonhigh, mTitlePaint);
            					canvas.drawText(unit[sortDistance[i][0]], iWidth, iHeight*0.98f-MasterController.typeButtonhigh, mDistancePaint);
            					drawMultilineText(POI[sortDistance[i][0]][3], iWidth*0.40f,  iHeight*0.80f-MasterController.typeButtonhigh, mDescriptionPaint,canvas);
            					//canvas.drawText(POI[sortDistance[i][0]][8], iWidth*0.40f, iHeight*0.76f-MasterController.typeButtonhigh, mDescriptionPaint);      				
        			
        		}
        		else if (-3< dBearingToTarget1 && dBearingToTarget1<3)
        		{
        			//MasterController.POI_index = Integer.valueOf(POI[sortDistance[i][0]][0]);
//        			bPOIfocusNodata = true;
//        			bSetFirstPOIfocus = false;
        		}
        		else
        			MasterController.POI_index = 0;
         		
         	}
    		else if(sortDistance[i][2] == (MasterController.setType * 10))// 根據type 顯示POI
    		{
    			POInumCheck++;
    				//sortDistance[i][2] == type number * 10
    				//圖片會隨著相機角度變換圖片角度
             	mMatrix.setScale(100f/icon[sortDistance[i][0]].getWidth(),100f/icon[sortDistance[i][0]].getHeight());  
             	mMatrix.postTranslate(fPoiXPosition+fIconLeft, fPoiYPosition+fIconTop);  
             	mMatrix.postSkew(0, (float)dBearingToTarget/100, fPoiXPosition, fPoiYPosition);
             	//對焦&觸碰時，在雷達圖上表示此POI的點會變色(紅為點選)
             	if(POI[sortDistance[i][0]][0] == MasterController.iTouchPOIID)
             	{
            		mGridPaint.setAlpha(255);
             		//mMessagePaint.setColor(Color.RED);
             	}
             	else
             	{	
             		mGridPaint.setAlpha(160);
             		//mMessagePaint.setColor(Color.WHITE);
             	}
             	//畫POI的圖 // accroding iconsize adaptive iconsize in screen
             	try
             	{
             		canvas.drawBitmap(icon[sortDistance[i][0]], mMatrix, mGridPaint);
             	}
             	catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
             	//景點名稱 title upon Icon in scene
            	canvas.drawText(POI[sortDistance[i][0]][1], fPoiXPosition+fIconLeft+40, fPoiYPosition+fIconTop-10, mTitlePaint);
            		
            		
            	//0512 draw POI title and Line and info by "target" ↓↓↓//
           		dBearingToTarget1 = mBearing[sortDistance[i][0]] - HoriOrientation;
           		if (-3< dBearingToTarget1 & dBearingToTarget1<3 && ISdrawLine == 0)
           		{	
           			ISdrawLine = 1;
               		canvas.drawText(POI[sortDistance[i][0]][1],iWidth*0.5f,fTitleY*0.15f,mBanarPaint);
               		canvas.drawRect(iWidth*0.30f, fTitleY*0.0f, iWidth*0.70f,fTitleY*0.2f,mBanarbackPaint);
               		MasterController.POI_index = Integer.valueOf(POI[sortDistance[i][0]][0]);
               		MasterController.iFocusPOIName = POI[sortDistance[i][0]][0];
               		MasterController.iFocusPOIInfo = POI[sortDistance[i][0]][8];
               		MasterController.SetPOItype = POI[sortDistance[i][0]][4];
               		//Log.i("omniguider","whyyyyyy:"+MasterController.iFocusPOIName+"\n+"+MasterController.iFocusPOIInfo+"\n"+MasterController.POI_index);
               		//boolean bPOIfocusNodata;
                				
               				//如果是商家而沒有促銷資料或
//               					if(POIofFOV[sortDistance[i][0]][5].equals("1")||POIofFOV[sortDistance[i][0]][5].equals("2")||POIofFOV[sortDistance[i][0]][5].equals("3")||POIofFOV[sortDistance[i][0]][5].equals("7"))
//               					{
//               						switch(MasterController.iLanguage)
//               						{
//               							case 0:
//               								canvas.drawText("尚無促銷資訊!!",iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
//               								
//               								break;
//               							case 1:
//               								canvas.drawText("No Special Offer yet.",iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
//               								//canvas.drawText(POI[i][4],iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
//               								//POI[I][4] = poi Detail 資料
//               								break;
//       	        		    		}
//               						
//       	        		        }		

               					//中間瞄準器開始
               		mArrowPaint.setAlpha(80);
               		pathTriangle = new Path();	        		        
               		pathTriangle.moveTo(fPoiXPosition, fPoiYPosition-iHeight*0.0125f);
               		pathTriangle.lineTo(fPoiXPosition-(iCenterWidth*0.1f*0.5f), fPoiYPosition*1.2f*0.98f);
               		pathTriangle.lineTo(fPoiXPosition-(iCenterWidth*0.1f/4), fPoiYPosition*1.2f*0.98f);
               		pathTriangle.lineTo(iCenterWidth*0.975f, iHeight*0.89f-MasterController.typeButtonhigh);
               		pathTriangle.lineTo(iCenterWidth*1.025f, iHeight*0.89f-MasterController.typeButtonhigh);
               		pathTriangle.lineTo(fPoiXPosition+(iCenterWidth*0.1f/4),fPoiYPosition*1.2f*0.98f);
               		pathTriangle.lineTo(fPoiXPosition+(iCenterWidth*0.1f*0.5f), fPoiYPosition*1.2f*0.98f);
               		canvas.drawPath(pathTriangle, mArrowPaint);
               						
               		canvas.drawRect(iWidth*0.40f, iHeight*0.7f-MasterController.typeButtonhigh, iWidth,iHeight-MasterController.typeButtonhigh,mTextPaint);	
               		//canvas.drawText(POIofFOV[sortDistance[i][0]][1], iWidth*0.40f, iHeight*0.75f-MasterController.typeButtonhigh, mTitlePaint);
               		canvas.drawText(unit[sortDistance[i][0]], iWidth, iHeight*0.98f-MasterController.typeButtonhigh, mDistancePaint);
               		drawMultilineText(POI[sortDistance[i][0]][8], iWidth*0.40f,  iHeight*0.80f-MasterController.typeButtonhigh, mDescriptionPaint,canvas);
               		//canvas.drawText(POI[sortDistance[i][0]][4], iWidth*0.40f, iHeight*0.76f-MasterController.typeButtonhigh, mDescriptionPaint);
               		//Log.i("Omniguider", "POIView Havi");
          		}
        		else if (-3< dBearingToTarget1 && dBearingToTarget1<3)
        		{
        			MasterController.POI_index = Integer.valueOf(POI[sortDistance[i][0]][0]);
//        			bPOIfocusNodata = true;
//        			bSetFirstPOIfocus = false;
        		}
        		else
        			MasterController.POI_index = 0;
    		}
         	//有指定POI顯示type
        }
    }
    
    
    //0512 combine LineonPOI，畫出瞄準POI的線
    private void DrawAimLine(Canvas canvas)
    {
    	//Log.i("Omniguider", "9  DrawAimLine");
    	
    	if(longitude!=0||latitude!=0)
    	{
    		int test1 = 0;//testing for the same thinking with 個數<7
        	for (int i=0; i<POInumber; i++)
        	{
        		dBearingToTarget1 = mBearing[sortDistance[i][0]] - HoriOrientation;
        		if (-5< dBearingToTarget1 & dBearingToTarget1<5)
        		{
        			mGridPaint.setAlpha(255);
	                // for test
        			if(POInumber==1)
	                {
	                	dBearingToTarget = mBearing[sortDistance[i][0]] - HoriOrientation;
	               		fPoiXPosition = (float) (iCenterWidth + (dBearingToTarget /35.359 * iCenterWidth));
	                    fPoiYPosition = (float) (iHeight*0.55f - VerOrientation*4);
	                }
	                else
	                {
	                	dBearingToTarget = mBearing[sortDistance[i][0]] - HoriOrientation;
	                	fPoiXPosition = (float) (iCenterWidth + (dBearingToTarget /35.359 * iCenterWidth));
	                	fPoiYPosition = (float) (iHeight*0.55f - (mDistance[sortDistance[i][0]]-mDistance[sortDistance[0][0]])/dRatioDeep-VerOrientation*4);
	                }
	                //距離在設定的顯示範圍為1 顯示圖示
        			if(sortDistance[i][1]==1 )
        			//if(sortDistance[i][1]==1)
        			{	
        	    		if(MasterController.setType==999)
        				{	mGridPaint.setAlpha(255);
                    	
        	    			
            				canvas.drawText(POI[sortDistance[i][0]][1],iWidth*0.5f,fTitleY*0.15f,mBanarPaint);
            				canvas.drawRect(iWidth*0.30f, fTitleY*0.0f, iWidth*0.70f,fTitleY*0.2f,mBanarbackPaint);
            				MasterController.POI_index = Integer.valueOf(POI[sortDistance[i][0]][0]);
                       		MasterController.iFocusPOIName = POIofFOV[sortDistance[i][0]][0];
                       		MasterController.iFocusPOIInfo = POIofFOV[sortDistance[i][0]][8];
            				MasterController.SetPOItype = POI[sortDistance[i][0]][5];
            				//boolean bPOIfocusNodata;

            				
            				//如果是商家而沒有促銷資料或
            					if(POI[sortDistance[i][0]][5].equals("1")||POI[sortDistance[i][0]][5].equals("2")||POI[sortDistance[i][0]][5].equals("3")||POI[sortDistance[i][0]][5].equals("7"))
            					{
            						switch(MasterController.iLanguage)
            						{
            							case 0:
            								canvas.drawText("尚無促銷資訊!!",iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
            								
            								break;
            							case 1:
            								canvas.drawText("No Special Offer yet.",iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
            								//canvas.drawText(POI[i][4],iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
            								//POI[I][4] = poi Detail 資料
            								break;
    	        		    		}
            						
    	        		        }		
            				
            				if(MasterController.HaveNavi==false)
            				{
            					//中間瞄準器開始
            					mArrowPaint.setAlpha(80);
            					pathTriangle = new Path();	        		        
            					pathTriangle.moveTo(fPoiXPosition, fPoiYPosition-iHeight*0.0125f);
            					pathTriangle.lineTo(fPoiXPosition-(iCenterWidth*0.1f*0.5f), fPoiYPosition*1.2f*0.98f);
            					pathTriangle.lineTo(fPoiXPosition-(iCenterWidth*0.1f/4), fPoiYPosition*1.2f*0.98f);
            					pathTriangle.lineTo(iCenterWidth*0.975f, iHeight*0.89f-MasterController.typeButtonhigh);
            					pathTriangle.lineTo(iCenterWidth*1.025f, iHeight*0.89f-MasterController.typeButtonhigh);
            					pathTriangle.lineTo(fPoiXPosition+(iCenterWidth*0.1f/4),fPoiYPosition*1.2f*0.98f);
            					pathTriangle.lineTo(fPoiXPosition+(iCenterWidth*0.1f*0.5f), fPoiYPosition*1.2f*0.98f);
            					
            					canvas.drawPath(pathTriangle, mArrowPaint);
            						
            					canvas.drawRect(iWidth*0.40f, iHeight*0.7f-MasterController.typeButtonhigh, iWidth,iHeight-MasterController.typeButtonhigh,mTextPaint);	
            					//canvas.drawText(POI[sortDistance[i][0]][1], iWidth*0.40f, iHeight*0.75f-MasterController.typeButtonhigh, mTitlePaint);
            					canvas.drawText(unit[sortDistance[i][0]], iWidth, iHeight*0.98f-MasterController.typeButtonhigh, mDistancePaint);
            					drawMultilineText(POI[sortDistance[i][0]][4], iWidth*0.40f,  iHeight*0.80f-MasterController.typeButtonhigh, mDescriptionPaint,canvas);
            					//canvas.drawText(POI[sortDistance[i][0]][4], iWidth*0.40f, iHeight*0.76f-MasterController.typeButtonhigh, mDescriptionPaint);
            					//Log.i("Omniguider", "POIView Havi");
            				}
            				else
            				{
            					canvas.drawRect(iWidth*0.40f, fTitleY*0.2f, iWidth*0.60f,fTitleY*0.3f,mTextPaint);
            					canvas.drawText(unit[sortDistance[i][0]], iWidth*0.55f, fTitleY*0.27f, mDistancePaint);
            					//Log.i("Omniguider", "POIView NoHavi");
            				}
        				}
        				else
        				{
        	    			if(sortDistance[i][2] == (MasterController.setType * 10))
        	    			{
        	    				canvas.drawText(POI[sortDistance[i][0]][1],iWidth*0.5f,fTitleY*0.15f,mBanarPaint);
                				canvas.drawRect(iWidth*0.30f, fTitleY*0.0f, iWidth*0.70f,fTitleY*0.2f,mBanarbackPaint);
                				MasterController.POI_index = Integer.valueOf(POI[sortDistance[i][0]][0]);
                           		MasterController.iFocusPOIName = POIofFOV[sortDistance[i][0]][0];
                           		MasterController.iFocusPOIInfo = POIofFOV[sortDistance[i][0]][8];
                				MasterController.SetPOItype = POI[sortDistance[i][0]][5];
                				//boolean bPOIfocusNodata;

                				
                				//如果是商家而沒有促銷資料或
                					if(POI[sortDistance[i][0]][5].equals("1")||POI[sortDistance[i][0]][5].equals("2")||POI[sortDistance[i][0]][5].equals("3")||POI[sortDistance[i][0]][5].equals("7"))
                					{
                						switch(MasterController.iLanguage)
                						{
                							case 0:
                								canvas.drawText("尚無促銷資訊!!",iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
                								
                								break;
                							case 1:
                								canvas.drawText("No Special Offer yet.",iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
                								//canvas.drawText(POI[i][4],iWidth*0.5f,fTitleY*0.25f,mBanarPaint1);
                								//POI[I][4] = poi Detail 資料
                								break;
        	        		    		}
                						
        	        		        }		
                				
                				if(MasterController.HaveNavi==false)
                				{
                					//中間瞄準器開始
                					mArrowPaint.setAlpha(80);
                					pathTriangle = new Path();	        		        
                					pathTriangle.moveTo(fPoiXPosition, fPoiYPosition-iHeight*0.0125f);
                					pathTriangle.lineTo(fPoiXPosition-(iCenterWidth*0.1f*0.5f), fPoiYPosition*1.2f*0.98f);
                					pathTriangle.lineTo(fPoiXPosition-(iCenterWidth*0.1f/4), fPoiYPosition*1.2f*0.98f);
                					pathTriangle.lineTo(iCenterWidth*0.975f, iHeight*0.89f-MasterController.typeButtonhigh);
                					pathTriangle.lineTo(iCenterWidth*1.025f, iHeight*0.89f-MasterController.typeButtonhigh);
                					pathTriangle.lineTo(fPoiXPosition+(iCenterWidth*0.1f/4),fPoiYPosition*1.2f*0.98f);
                					pathTriangle.lineTo(fPoiXPosition+(iCenterWidth*0.1f*0.5f), fPoiYPosition*1.2f*0.98f);
                		        
                					canvas.drawPath(pathTriangle, mArrowPaint);
                						
                					canvas.drawRect(iWidth*0.40f, iHeight*0.7f-MasterController.typeButtonhigh, iWidth,iHeight-MasterController.typeButtonhigh,mTextPaint);	
                					//canvas.drawText(POI[sortDistance[i][0]][1], iWidth*0.40f, iHeight*0.75f-MasterController.typeButtonhigh, mTitlePaint);
                					canvas.drawText(unit[sortDistance[i][0]], iWidth, iHeight*0.98f-MasterController.typeButtonhigh, mDistancePaint);
                					drawMultilineText(POI[sortDistance[i][0]][4], iWidth*0.40f,  iHeight*0.80f-MasterController.typeButtonhigh, mDescriptionPaint,canvas);
                					//canvas.drawText(POI[sortDistance[i][0]][4], iWidth*0.40f, iHeight*0.76f-MasterController.typeButtonhigh, mDescriptionPaint);
                					//Log.i("Omniguider", "POIView Havi");
                				}
                				else
                				{
                					canvas.drawRect(iWidth*0.40f, fTitleY*0.2f, iWidth*0.60f,fTitleY*0.3f,mTextPaint);
                					canvas.drawText(unit[sortDistance[i][0]], iWidth*0.55f, fTitleY*0.27f, mDistancePaint);
                					//Log.i("Omniguider", "POIView NoHavi");
                				}

        	    			}
            			}

        			}
        			break;
        		}
        		else
        		{
//        			bPOIfocusNodata = true;
//        			bSetFirstPOIfocus = false;
//        			MasterController.SetPOIfocus = "0";
               		MasterController.iFocusPOIName = "0";
               		MasterController.iFocusPOIInfo = "0";
        		}
        	}
    	}
    }
    //=========================================
    //0710smallthree
    //target: 將原本POItoIconType中判別POI type的部分
    //        拉出來為一個function方便每次更新
    //=========================================

   //儲存最後一次選擇的POI類別
//    private void StrorePOIType()
//    {
//    	//Log.i("Omniguider", "12  StorePOItype");
//    	
//		if(MasterController.lastPOItype == "999")
//			MasterController.lastPOItype = MasterController.iGetPOIType;
//		else
//		{
//			if(MasterController.lastPOItype == MasterController.iGetPOIType)
//				SamePOItype =true;
//		}
//		
//	}

    void drawMultilineText(String str, float f, float g, Paint paint, Canvas canvas) {
        float      lineHeight = 0;
        float      yoffset    = 0;
        String[] lines      = str.split("\n");

        //Rect mBounds = null;
		// set height of each line (height of text + 20%)
        //paint.getTextBounds("Ig", 0, 2, mBounds);
        lineHeight = g*0.1f;
        // draw each line
        for (int i = 0; i < lines.length; ++i) {
            canvas.drawText(lines[i], f, g + yoffset, paint);
            yoffset = yoffset + lineHeight;
        }
    }
    

    
}