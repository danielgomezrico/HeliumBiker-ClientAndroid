package biker.helium.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;



public class DrawableView extends View implements OnTouchListener{
    
    private final int initialX = 0;
    private final int initialY = 0;
    
    private SlingShot slingShot;
    
    public Handler refreshHandler = new Handler(){
    	@Override  
    	public void handleMessage(Message msg) {  
    		DrawableView.this.invalidate();  
    	}
    };
    

	public DrawableView(Context context) {
		super(context);

        this.setOnTouchListener(this);
		
        slingShot = new SlingShot(this);
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		slingShot.draw(canvas);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {

		slingShot.setX2((int) event.getX());
		slingShot.setY2((int) event.getY());

		switch (event.getAction() ) { 

		case MotionEvent.ACTION_DOWN:
			slingShot.stopBackAnimation();
			break;

		case MotionEvent.ACTION_UP:
			slingShot.startBackAnimation();
			break;
		}

		invalidate();

		return true;
	}
	

  
}

	

