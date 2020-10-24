/** 《函式庫、類別庫》
 * 開啟手機鏡頭
 */
package View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CustomCameraView extends SurfaceView 
{
	Camera camera;
	SurfaceHolder previewHolder;
	//private boolean bCameraOpen = false;
	public CustomCameraView(Context context) 
	{
		super(context);
		previewHolder = this.getHolder();
		previewHolder.addCallback(surfaceHolderListener);
	}
	
	SurfaceHolder.Callback surfaceHolderListener = new SurfaceHolder.Callback() 
	{
		public void surfaceCreated(SurfaceHolder holder) 
		{    
			try 
			{
				camera=Camera.open();            
				camera.setPreviewDisplay(previewHolder);
			}            
			catch (Throwable e)
			{
				e.printStackTrace();
			}           
		}
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{      
			try 
			{		
				Parameters params = camera.getParameters();
				params.setPictureFormat(PixelFormat.JPEG);
				camera.setParameters(params);    
				camera.startPreview();
			}            
			catch (Throwable e)
			{
				e.printStackTrace();
			}
	   }
	   public void surfaceDestroyed(SurfaceHolder arg0)
	   {
			try 
			{	
			   camera.stopPreview();
			   camera.release();   
			}            
			catch (Throwable e)
			{
				e.printStackTrace();
			}
	   }
	};
}