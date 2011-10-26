package biker.helium.view;

import java.util.Observable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;

public class SlingShot{
	public static final int INITIAL_X = 0, INITIAL_Y = 0;

	private int x2, y2; //Line

	private Paint paint;

	private boolean checkMovement = false;
	
	private DrawableView mainView;

	public SlingShot(DrawableView mainView){
		x2 = INITIAL_X - 10;
		y2 = INITIAL_Y - 10;

		paint = new Paint();
		paint.setColor(Color.WHITE);
		
		this.mainView = mainView;
	}

	public void startBackAnimation(){
		checkMovement = true;

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(checkMovement){
					
					if(x2 <= INITIAL_X){
						x2 = x2 - 10;
					}

					if(y2 <= INITIAL_Y){
						y2 = y2 - 10;
					}	
					
					//TODO:Arreglar esto, se debe poner a actualizar con el handler
 					mainView.refreshHandler.sendMessage(Message.obtain(mainView.refreshHandler));
				}
			}
		}).start();
	}

	public void stopBackAnimation(){
		checkMovement = false;
	}

	public void draw(Canvas canvas){
		canvas.drawLine(SlingShot.INITIAL_X, SlingShot.INITIAL_Y, getX2(), getY2(), paint);
	}



	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	
	
	public boolean isCheckMovement() {
		return checkMovement;
	}

	public void setCheckMovement(boolean checkMovement) {
		this.checkMovement = checkMovement;
	}

	

}
