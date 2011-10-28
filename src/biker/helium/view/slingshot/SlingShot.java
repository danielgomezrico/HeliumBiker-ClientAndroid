package biker.helium.view.slingshot;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import biker.helium.Managers.Bluetooth.BluetoothClient.MessageType;
import biker.helium.activities.BluetoothActivity;
import biker.helium.client.R;

public class SlingShot{
	public static final float BORDER_OFFSET = 15;
	public static final int TIME_NEW_PICKEABLE_STONE = 500;//ms
	
	
	public final float INITIAL_X, INITIAL_Y;
	public final int SCREEN_WIDTH, SCREEN_HEIGHT;

	public Resources resources;
	private Paint paint;
	private boolean isAnimating;
	
	/**
	 * x -> y and y -> x because we are using the window in portrait but incline manually the device
	 */
	private float newX, newY;
	private Stone pickeableStone;
	private boolean stoneThrowed;//If the stone had been threw actually
	private ArrayList<Stone> arrayListThrowedStones;

	public SlingShot(Resources resources, int screenWidth, int screenHeight){
		INITIAL_X = screenWidth/2;
		INITIAL_Y = screenHeight/2;

		SCREEN_WIDTH = screenWidth;
		SCREEN_HEIGHT = screenHeight;
		
		newX = INITIAL_X - 10;
		newY = INITIAL_Y - 10;
		
		this.resources = resources;
		
		isAnimating = false;
				
		paint = new Paint();
		paint.setColor(resources.getColor(R.color.line_color));
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2.0f);
		
//		stone = BitmapFactory.decodeResource(resources, R.drawable.stone);
		arrayListThrowedStones = new ArrayList<Stone>();
		pickeableStone = new Stone(this, INITIAL_X, INITIAL_Y,SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	public void setNewX(float newX) {
		if(!stoneThrowed){
			this.newX = newX;
			if(pickeableStone != null){
				pickeableStone.setNewX(newX);
			}
		}
	}

	public void setNewY(float newY) {
		if(!stoneThrowed){
			if(newY < INITIAL_Y - BORDER_OFFSET){
				this.newY = newY;
				if(pickeableStone != null){
					pickeableStone.setNewY(newY);
				}
			}
		}
	}

	public void draw(Canvas canvas){
		try{
			
		synchronized (arrayListThrowedStones) {
			for (Stone stone : arrayListThrowedStones) {
				stone.draw(canvas);
			}
		}
		
		if(pickeableStone != null){
			canvas.drawLine(INITIAL_X, INITIAL_Y, newX, newY, paint);
			canvas.drawPoint(INITIAL_X, INITIAL_Y, paint);
			canvas.drawLine(INITIAL_X + 20, INITIAL_Y, INITIAL_X - 10, INITIAL_Y - 10, paint);
			canvas.drawLine(INITIAL_X + 20, INITIAL_Y, INITIAL_X + 10, INITIAL_Y - 10, paint);

			pickeableStone.draw(canvas);
		}
	
		}catch(Exception e){
//			TODO:Check if this exception can be throwed
//			Log.d("**P","BUM!!!!!");
		}
	}

	public void startBackAnimation(){
		if(!isAnimating && pickeableStone != null){
			isAnimating = true;
			stoneThrowed = true;
			
//			SlingShot me = this;
			
			//Variable to send in bluetooth message
			final float threwX = newX;
			final float threwY = newY;
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					
					//Calc movement variables
					double angle = calcDirectorAngle();
					double operator = 1; //Says if must plus or substract for the newX
					int delay = 2;
	
					if(newX > INITIAL_X){
						operator *= -1;
					}
	
					while(isAnimating){
						
						newX = newX + (float)( Math.cos(angle) * operator) ;
						newY = newY + (float)( Math.sin(angle)) ;
						
						pickeableStone.setNewXNewY(newX, newY);
						
						if(newY >= INITIAL_Y && INITIAL_X + newX >= 0){//End of pull area

							pickeableStone.startBackAnimation(angle, delay, operator);
							arrayListThrowedStones.add(pickeableStone);
							
							synchronized (pickeableStone) {
								pickeableStone = null;
							}
							
							scheduleNewStone();
							
							BluetoothActivity.sendMessage(MessageType.S, INITIAL_X - threwX, INITIAL_Y - threwY);
							
							isAnimating = false;
						}					
						else{
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
						}
					}
				}

				
			}).start();
		}
	}
	
	/**
	 * Initialize pickeableStone variable
	 */
	private void initPickeableStone(){
		newX = INITIAL_X;
		newY = INITIAL_Y;
		pickeableStone = new Stone(this, INITIAL_X, INITIAL_Y,SCREEN_WIDTH, SCREEN_HEIGHT);
		stoneThrowed = false;
	}

	/**
	 * Schedule the initialization of a new pickeableStone in TIME_NEW_PICKEABLE_STONE
	 */
	public void scheduleNewStone(){
		Executors.newScheduledThreadPool(1).schedule(new Runnable(){
		    @Override
		    public void run(){
		    	initPickeableStone();
		    }
		}, TIME_NEW_PICKEABLE_STONE, TimeUnit.MILLISECONDS);
	}
	
	public double calcDirectorAngle(){

		double angle = Math.atan2( INITIAL_Y - newY, INITIAL_X - newX );  //* 180/Math.PI;//+ (Math.PI);

		if(angle > Math.toRadians(90)){
			angle = Math.toRadians(180) - angle;
		}

		return angle;
	}

	public void stopBackAnimation(){
		isAnimating = false;
	}

	public void removeStone(Stone stone){
		synchronized (arrayListThrowedStones) {
			arrayListThrowedStones.remove(stone);
		}
	}


}
