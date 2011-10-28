package biker.helium.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import biker.helium.Managers.Bluetooth.BluetoothClient.MessageType;
import biker.helium.Managers.acceleromether.AccelerometerManager;
import biker.helium.Managers.acceleromether.IAccelerometerObserver;
import biker.helium.view.GameView;

public class GameActivity extends Activity implements IAccelerometerObserver  {
	
	private AccelerometerManager accelManager;
	
	/**
	 * Used to control the times that the GameActivity shows a message in changeAcceleromether(...)
	 * in case that the message was not sended successfully thought bluetooth
	 * (only one message).
	 * 
	 * This is because the changeAcceleromether is called so much times and faster and if 
	 * sending the message we get an error it will try to show so much messages and crash
	 */
	private boolean attemptedShowMessage;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
		setTitle("Game Control");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        attemptedShowMessage = false;

        try {
			accelManager = new AccelerometerManager(this);
		} catch (Exception e) {	}
        
        setContentView(new GameView(this.getBaseContext()));

    }
        
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if(accelManager != null){
    		accelManager.startListen();
    	}
    	
    	//TODO:Enviar mensaje de resumir por bluetooth
    }

    @Override
    protected void onPause() {
    	super.onPause();
   
    	if(accelManager!= null){
    		accelManager.stopListen();
    	}
    	
    	//TODO:Enviar mensaje de pausa por bluetooth

    }
   
    @Override
    protected void onDestroy() {
    	accelManager.stopListen();
    	super.onDestroy();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//Go back to BluetoothActivitys
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("ÀAre you sure you want to go to bluetooth window? (The actual connection will be lost)")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   finish();
        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	               dialog.cancel();
        	           }
        	       });
        	
        	AlertDialog alert = builder.create();
        	alert.setCancelable(false);
			alert.show();
			
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
	@Override
	public void changeAcceleromether(float x, float y) {
		
		boolean success = BluetoothActivity.sendMessage(MessageType.A, y, x);

		if(!(success && attemptedShowMessage)){//If not successfully sended and it's the first intend to show this message
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("The bluetooth connection is lost. Correct the connection problem and connect again")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					finish();
				}
			});

			AlertDialog alert = builder.create();
			alert.setCancelable(false);
			alert.show();
			
			attemptedShowMessage = true;
		}

	}


}