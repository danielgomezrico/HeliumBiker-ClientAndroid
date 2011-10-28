package biker.helium.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;
import biker.helium.Managers.Bluetooth.BluetoothClient.MessageType;
import biker.helium.activities.BluetoothActivity;
import biker.helium.client.R;
import biker.helium.view.slingshot.SlingShot;
 
public class GameView extends SurfaceView implements OnTouchListener, SurfaceHolder.Callback {
    
	private final int DISPLAY_WIDTH;
	private final int DISPLAY_HEIGHT;
	
    private SlingShot slingShot;
    private SurfaceUpdateThread updateThread;
    private int backgroundColor;

	public GameView(Context context) {
		super(context);
		getHolder().addCallback(this);
        updateThread = new SurfaceUpdateThread(this);
        
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DISPLAY_WIDTH = display.getWidth();
        DISPLAY_HEIGHT = display.getHeight();
        backgroundColor = getResources().getColor(R.color.background_color);
        
        slingShot = new SlingShot(getResources(), display.getWidth(), display.getHeight());
        
        setOnTouchListener(this);
        setFocusable(true);
        
		Toast.makeText(getContext(), "Pull the stone!!", Toast.LENGTH_LONG);

	}
	
	@Override
	public void onDraw(Canvas canvas) {
        canvas.drawColor(backgroundColor);
		slingShot.draw(canvas);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		switch (event.getAction() ) { 

		case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_MOVE :
			float x = event.getX();
			float y = event.getY();
			
			slingShot.setNewX(x);
			slingShot.setNewY(y);
			
//			TODO:Descomentar
			BluetoothActivity.sendMessage(MessageType.P, (DISPLAY_WIDTH/2) - x, (DISPLAY_HEIGHT/2) - y);
			
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

	

