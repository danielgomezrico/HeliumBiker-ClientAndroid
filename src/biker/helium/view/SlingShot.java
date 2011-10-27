package biker.helium.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import biker.helium.client.R;

public class SlingShot{
	public final float INITIAL_X, INITIAL_Y;

	private Paint paint;

	private boolean animate = false;
		
	private float x2, y2;
	private Bitmap stone;


	public SlingShot(Resources resources, int screenWidth, int screenHeight){
		INITIAL_X = screenWidth/2;
		INITIAL_Y = screenHeight/2;
		
		x2 = INITIAL_X - 10;
		y2 = INITIAL_Y - 10;
		
		stone = BitmapFactory.decodeResource(resources, R.drawable.stone);
		
		paint = new Paint();
		paint.setColor(Color.WHITE);		
	}

	
	public void startBackAnimation(){
		animate = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				double angle = calcDirectorAngle();
				double offsetX = 1;
				double offsetY = 1;
				int delay = (int) (y2)/10;//4 Cuadrant

				if(x2 > INITIAL_X){
					offsetX *= -1;
					//delay = (int)(y2)/10;
				}

				while(animate){

					x2 += (float)( Math.cos(angle) * offsetX);

					y2 += (float)( Math.sin(angle) * offsetY);

					if(x2 >= INITIAL_X && y2 >= INITIAL_Y){
						
						y2 = INITIAL_Y;
						x2 = INITIAL_X;
						animate = false;
					}					

					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public double calcDirectorAngle(){

		double angle = Math.atan2( INITIAL_Y - y2, INITIAL_X - x2 );  //* 180/Math.PI;//+ (Math.PI);
		
		if(angle > Math.toRadians(90)){
			angle = Math.toRadians(180) - angle;
		}
		
		return angle;
	}

	public void stopBackAnimation(){
		animate = false;
	}

	public void draw(Canvas canvas){
		canvas.drawLine(INITIAL_X, INITIAL_Y, getX2(), getY2(), paint);
	    canvas.drawBitmap(stone, x2 - (stone.getWidth() / 2), y2 - (stone.getHeight() / 2), null);
	}

	
	
	
	
	

	public float getX2() {
		return x2;
	}

	public void setX2(float x2) {
		this.x2 = x2;
	}

	public float getY2() {
		return y2;
	}

	public void setY2(float y2) {
		if(y2 < INITIAL_Y){
			this.y2 = y2;
		}
	}

	
	
	public boolean isAnimating() {
		return animate;
	}

	public void setCheckMovement(boolean checkMovement) {
		this.animate = checkMovement;
	}

	

}
