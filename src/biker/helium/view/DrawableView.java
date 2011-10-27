package biker.helium.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import biker.helium.client.R;
import biker.helium.view.slingshot.SlingShot;
 
public class DrawableView extends SurfaceView implements OnTouchListener, SurfaceHolder.Callback {
    
    private SlingShot slingShot;
    private SurfaceUpdateThread updateThread;
    private int backgroundColor;

	public DrawableView(Context context) {
		super(context);

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        slingShot = new SlingShot(getResources(), display.getWidth(), display.getHeight());

        getHolder().addCallback(this);
        updateThread = new SurfaceUpdateThread(this);
        
        setOnTouchListener(this);
        setFocusable(true);
        
        backgroundColor = getResources().getColor(R.color.background_color);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
        canvas.drawColor(backgroundColor);
//		canvas.drawColor(Color.BLUE);
		slingShot.draw(canvas);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		slingShot.setX2(event.getX());
		slingShot.setY2(event.getY());
		
		switch (event.getAction() ) { 

		case MotionEvent.ACTION_DOWN:
			slingShot.stopBackAnimation();
			break;

		case MotionEvent.ACTION_UP:
			slingShot.startBackAnimation();
			break;
		}

		//invalidate();

		return true;
	}

	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		updateThread.setRunning(true);
		updateThread.start();		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
	    
		// we have to tell thread to shut down & wait for it to finish, or else
	    // it might touch the Surface after we return and explode
	    boolean retry = true;
	    updateThread.setRunning(false);
	    
	    while (retry) {
	        try {
	        	updateThread.join();
	            retry = false;
	        } catch (InterruptedException e) {
	            // we will try it again and again...
	        }
	    }
		
	}
	

  
}

	

