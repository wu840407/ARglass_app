package View;


import GlobalSetting.GeoUtils;
import GlobalSetting.MasterController;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;


import static GlobalSetting.MasterController.POI;
import static com.ccit.arglass2.ARActivity.latitude;
import static com.ccit.arglass2.ARActivity.longitude;


public class RadarView extends View
{
	private static float R_orientation;
	
	/************************************************************************/
	// 1 Private local variables
	/************************************************************************/

	private Paint mGridPaint;   
    private Paint mGridPaint_blue; 
    private Paint mPOIPaint;
    private Paint mBackground;
    public static double DB[][]={
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0},
            {0,0, 0}
    };
    
    public RadarView(Context context) 
    {
        this(context, null);
    }
    
    public RadarView(Context context, AttributeSet attrs) 
    {
        this(context, attrs, 0);
    }
    public RadarView(Context context, AttributeSet attrs, int defStyle) 
    {
        super(context, attrs, defStyle);
        mGridPaint = new Paint();
        mGridPaint.setColor(Color.CYAN);
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setStrokeWidth(1.0f);
        mGridPaint.setTextSize(20.0f);
        mGridPaint.setTextAlign(Align.CENTER);
        
        mGridPaint_blue = new Paint();
        mGridPaint_blue.setColor(Color.CYAN);
        mGridPaint_blue.setAntiAlias(true);
        mGridPaint_blue.setStyle(Style.FILL);
        mGridPaint_blue.setStrokeWidth(1.0f);
        mGridPaint_blue.setTextSize(10.0f);
        mGridPaint_blue.setTextAlign(Align.CENTER);
        mGridPaint_blue.setAlpha(100);
        
        mPOIPaint = new Paint();
        mPOIPaint.setColor(0xffcc3300);
        mPOIPaint.setStyle(Style.FILL_AND_STROKE);
        mPOIPaint.setTextAlign(Align.RIGHT);
        mPOIPaint.setTextSize(20f);
        mPOIPaint.setStrokeWidth(3f); 
        
        mBackground = new Paint();
        mBackground.setColor(0xaaffffff);
        mBackground.setAntiAlias(true);
        mBackground.setStyle(Style.FILL);
        mBackground.setTextAlign(Align.CENTER);
        mBackground.setAlpha(100);
//        mGridPaint_red = new Paint();
//        mGridPaint_red.setColor(0xffff0000);
//        mGridPaint_red.setAntiAlias(true);
//        mGridPaint_red.setStyle(Style.FILL);
//        mGridPaint_red.setStrokeWidth(1.0f);
//        mGridPaint_red.setTextSize(10.0f);
//        mGridPaint_red.setTextAlign(Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) 
    {
        super.onDraw(canvas);
        int center = getWidth()/2;
        int radius = center - 8;

        int iHeight = getHeight();
        int iWidth = getWidth();
        // Draw the rings
        final Paint gridPaint = mGridPaint;
        canvas.drawCircle(center, center, radius, gridPaint);
        canvas.drawCircle(center, center, radius * 3 / 4, gridPaint);
        canvas.drawCircle(center, center, radius >> 1, gridPaint);
        canvas.drawCircle(center,center, radius >> 2, gridPaint);
        canvas.drawCircle(center, center, radius, mBackground);
        //canvas.drawPoint(x, y, paint);
        // 0320 Try orientation drawline
//        double x1,y1;
//        double x12,y12;
//        double x2,y2;
//        double x23,y23;
//        double x3,y3;
//        double x34,y34;
//        double x4,y4;
//        double x41,y41;
//        // E
//        x1 = Math.cos(Math.toRadians( 360 - R_orientation ) );
//        y1 = Math.sin(Math.toRadians( 360 - R_orientation ) );
//        // se
//        x12 = Math.cos(Math.toRadians( 360 - R_orientation + 45 ) );
//        y12 = Math.sin(Math.toRadians( 360 - R_orientation + 45 ) );
//        // S
//        x2 = Math.cos(Math.toRadians( 360 - R_orientation + 90) );
//        y2 = Math.sin(Math.toRadians( 360 - R_orientation + 90) );
//        // sw
//        x23 = Math.cos(Math.toRadians( 360 - R_orientation + 135 ) );
//        y23 = Math.sin(Math.toRadians( 360 - R_orientation + 135 ) );
//        // W 
//        x3 = Math.cos(Math.toRadians( 360 - R_orientation + 180) );
//        y3 = Math.sin(Math.toRadians( 360 - R_orientation + 180) );
//        // nw
//        x34 = Math.cos(Math.toRadians( 360 - R_orientation + 225 ) );
//        y34 = Math.sin(Math.toRadians( 360 - R_orientation + 225 ) );
//        // N 
//        x4 = Math.cos(Math.toRadians( 360 - R_orientation + 270) );
//        y4 = Math.sin(Math.toRadians( 360 - R_orientation + 270) );
//        // ne
//        x41 = Math.cos(Math.toRadians( 360 - R_orientation + 315 ) );
//        y41 = Math.sin(Math.toRadians( 360 - R_orientation + 315) );
        //Log.i("Omniguider", "Orientation" + R_orientation);
        // E
        //canvas.drawBitmap(((BitmapDrawable)getResources().getDrawable(R.drawable.rada)).getBitmap(), null, showPicrada,  mPOIPaint);
        canvas.drawLine(center, center , center+(float)(radius*Math.cos(Math.toRadians(0))), center+(float)(radius*Math.sin(Math.toRadians(0))), gridPaint);
        canvas.drawText("E", center+(float)( (radius+16) * Math.cos(Math.toRadians(0))), center+(float)( (radius+16) * Math.sin(Math.toRadians(0))), gridPaint);
        canvas.drawText("se", center+(float)( (radius+16) * Math.cos(Math.toRadians(45))), center+(float)( (radius+16) * Math.sin(Math.toRadians(45))), gridPaint);
        // S
        canvas.drawLine(center, center , center+(float)(radius*Math.cos(Math.toRadians(90))), center+(float)(radius*Math.sin(Math.toRadians(90))), gridPaint);
        canvas.drawText("S", center+(float)( (radius+16) * Math.cos(Math.toRadians(90))), center+(float)( (radius+16) * Math.sin(Math.toRadians(90))), gridPaint);
        canvas.drawText("sw", center+(float)( (radius+16) * Math.cos(Math.toRadians(135))), center+(float)( (radius+16) * Math.sin(Math.toRadians(135))), gridPaint);
        // W
        canvas.drawLine(center, center , center+(float)(radius*Math.cos(Math.toRadians(180))), center+(float)(radius*Math.sin(Math.toRadians(180))), gridPaint);
        canvas.drawText("W", center+(float)( (radius+16) * Math.cos(Math.toRadians(180))), center+(float)( (radius+16) * Math.sin(Math.toRadians(180))), gridPaint);
        canvas.drawText("nw", center+(float)( (radius+16) * Math.cos(Math.toRadians(225))), center+(float)( (radius+16) *Math.sin(Math.toRadians(225))), gridPaint);
        // N  x4 y4 = 90
        canvas.drawLine(center, center , center+(float)(radius*Math.cos(Math.toRadians(270))), center+(float)(radius*Math.sin(Math.toRadians(270))), mGridPaint_blue);
        canvas.drawText("N", center+(float)( (radius+16) * Math.cos(Math.toRadians(270))), center+(float)( (radius+16) * Math.sin(Math.toRadians(270))), gridPaint);
        canvas.drawText("ne", center+(float)( (radius+16) * Math.cos(Math.toRadians(315))), center+(float)( (radius+16) * Math.sin(Math.toRadians(315))), gridPaint);
        
        // 0322 for draw point to "N" triangle
        Path nTriangle_north = new Path();
        nTriangle_north.moveTo(center, center);
        nTriangle_north.lineTo(center+(float)((radius)*Math.cos(Math.toRadians((360+R_orientation - 70)))), center+(float)((radius)*Math.sin(Math.toRadians((360+R_orientation-70)))));
        nTriangle_north.lineTo(center+(float)((radius)*Math.cos(Math.toRadians((360+R_orientation - 110)))), center+(float)((radius)*Math.sin(Math.toRadians((360+R_orientation-110)))));       
        nTriangle_north.close();
        canvas.drawPath(nTriangle_north, mGridPaint_blue);
        

        
        for(int i = 0;i < POI.length;i++)
		{
            double mDistance,mBearing;

    		mDistance = GeoUtils.distanceKm(latitude, longitude, Double.parseDouble(POI[i][1]), Double.parseDouble(POI[i][2]))*1000;
    		mBearing = GeoUtils.bearing(latitude, longitude, Double.parseDouble(POI[i][1]), Double.parseDouble(POI[i][2]));
            DB[i][0]=mDistance;
            DB[i][1]=mBearing;
    		if(MasterController.setType==999)
	    	{
	        	//畫雷達
	        	double drawingAngle = Math.toRadians(360+ mBearing - 90);
	        	float cos = (float) Math.cos(-drawingAngle);
	        	float sin = (float) Math.sin(drawingAngle);
	         	double mDistanceRatio = mDistance*0.7/1000;
	         	canvas.drawPoint(center + (float)(cos * (radius)*mDistanceRatio),center + (float)(sin * (radius)*mDistanceRatio), mPOIPaint);
	    	}
			/*else if(Integer.valueOf(POI[i][5]) == (MasterController.setType))// POI[][4] = info(old)
			{
	        	//畫雷達
	        	double drawingAngle = Math.toRadians(mBearing);
	        	float cos = (float) Math.cos(-drawingAngle);
	        	float sin = (float) Math.sin(drawingAngle);
	         	double mDistanceRatio = mDistance*0.7;
	         	//Log.i("Omniguider", "Ratio = "+mDistanceRatio);
	         	canvas.drawPoint(center + (float)(cos * (radius/2)*mDistanceRatio),center + (float)(sin * (radius/2)*mDistanceRatio), mPOIPaint);
			}*/
		}

        postInvalidate();
    }
    public static void setOrientation(float orientation)
    {
        R_orientation = orientation;

    }




}