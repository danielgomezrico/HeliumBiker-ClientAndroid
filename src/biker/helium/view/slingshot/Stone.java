package biker.helium.view.slingshot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import biker.helium.client.R;

public class Stone {
	public final float INITIAL_X, INITIAL_Y;
	public final int SCREEN_WIDTH, SCREEN_HEIGHT;

	private boolean animate;
	private float newX, newY;
	private Bitmap bStone;
	private SlingShot slingshot;

	public Stone(SlingShot slingshot, float initialX, float initialY, int screenWidth, int screenHeight){
		SCREEN_WIDTH = screenWidth;
		SCREEN_HEIGHT = screenHeight;
		
		INITIAL_X = initialX;
		INITIAL_Y = initialY;

		newX = INITIAL_X - 10;
		newY = INITIAL_Y - 10;
		
		animate = false;

		this.bStone = BitmapFactory.decodeResource(slingshot.resources, R.drawable.stone) ;
		
		this.slingshot = slingshot;	
	}

	public void setNewX(float newX) {
		this.newX = newX;
	}

	public void setNewY(float newY) {
		this.newY = newY;
	}
	
	public void setNewXNewY(float newX, float newY){
		this.newX = newX;
		this.newY = newY;
	}

	public void draw(Canvas canvas){
		canvas.drawBitmap(bStone, newX - (bStone.getWidth() / 2), newY - (bStone.getHeight() / 2), null);
	}

	public void startBackAnimation(final double directorAngle, final int delayEachMovement, final double operator){
		animate = true;
		final Stone meStone = this;
		
		new Thread(new Runnable() {
			@Override
			public void run() {

				while(animate){

					newX += (float)( Math.cos(directorAngle) * operator);
					newY += (float)( Math.sin(directorAngle));

					if(newX >= SCREEN_WIDTH + bStone.getWidth() || newY >= SCREEN_HEIGHT + bStone.getHeight() ){

						slingshot.removeStone(meStone);
						animate = false;
					}					

					try {
						Thread.sleep(delayEachMovement);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public double calcDirectorAngle(){

		double angle = Math.atan2( INITIAL_Y - newY, INITIAL_X - newX );  //* 180/Math.PI;//+ (Math.PI);

		if(angle > Math.toRadians(90)){
			angle = Math.toRadians(180) - angle;
		}

		return angle;
	}

	public void stopBackAnimation(){
		animate = false;
	}

}
